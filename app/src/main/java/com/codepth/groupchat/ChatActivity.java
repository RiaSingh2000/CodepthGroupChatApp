package com.codepth.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepth.groupchat.Adapters.ChatsAdapter;
import com.codepth.groupchat.Common.VerticalSpacingItemDecoration;
import com.codepth.groupchat.Model.MessageModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    //Button logOut;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    RecyclerView messages;
    ArrayList<MessageModel> msg_list;
    EditText text;
    ImageButton send;
    ImageView camera;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser fuser;


    private static final int IMAGE_REQUEST=1;
    Uri imgUri;
    String downUri="";

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        receiveMessage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       // logOut=findViewById(R.id.logout);

        mAuth=FirebaseAuth.getInstance();
        messages=findViewById(R.id.messages);
        msg_list=new ArrayList<>();
        text=findViewById(R.id.text);
        camera=findViewById(R.id.camera);
        send=findViewById(R.id.send);
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        LinearLayoutManager manager=new LinearLayoutManager(ChatActivity.this);
        messages.setLayoutManager(manager);
        manager.setStackFromEnd(true);

        messages.addItemDecoration(new VerticalSpacingItemDecoration(20));

        messages.setHasFixedSize(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!text.getText().toString().trim().equals("")||imgUri!=null) {
                    camera.setImageResource(R.drawable.camera);
                    uploadImage();
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ChatActivity.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
             String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

           // Toast.makeText(this, ""+personName, Toast.LENGTH_SHORT).show();


            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", personName);
            hashMap.put("email",personEmail );
            hashMap.put("personId",personId);
            databaseReference.child(fuser.getUid()).setValue(hashMap);
        }

//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.signOut();
//                ChatActivity.this.finish();
//            }
//        });

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                    startActivity(new Intent(ChatActivity.this,MainActivity.class));
                    //ChatActivity.this.finish();
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChatActivity.this.finish();
    }

    public void  sendMessage(String uri){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("message",text.getText().toString());
        hashMap.put("img",uri);
        hashMap.put("sender",fuser.getUid());
        hashMap.put("senderName",fuser.getDisplayName());

       // String id=databaseReference.push().getKey();
        databaseReference.child("chats").push().setValue(hashMap);
    }

    public void receiveMessage(){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msg_list.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    //MessageModel messageModel = snapshot.getValue(MessageModel.class);
                    String message=snapshot.child("message").getValue().toString();
                    String imgUri=snapshot.child("img").getValue().toString();
                    String sender=snapshot.child("sender").getValue().toString();
                    String senderName=snapshot.child("senderName").getValue().toString();
                    msg_list.add(new MessageModel(imgUri,message,sender,senderName));
                    //Toast.makeText(ChatActivity.this, ""+snapshot.child("message").getValue(), Toast.LENGTH_SHORT).show();

                }

               // Toast.makeText(ChatActivity.this, ""+msg_list.get(1).getMsg(), Toast.LENGTH_SHORT).show();
                messages.setAdapter(new ChatsAdapter(ChatActivity.this,msg_list,fuser.getUid()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST &&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imgUri=data.getData();
            //openDialog(imgUri);
            camera.setImageURI(imgUri);
        }
    }

    public void openDialog(Uri uri){
        ImageDialog imageDialog=new ImageDialog();
        Bundle args = new Bundle();
        args.putString("uri",imgUri.toString());
        imageDialog.setArguments(args);
        imageDialog.show(getSupportFragmentManager(),"Image Dialog");

    }

    private String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    public void uploadImage(){
        if(imgUri!=null){
            final ProgressDialog progressDialog=new ProgressDialog(ChatActivity.this);
                    progressDialog.setTitle("Uploading");
                    progressDialog.show();
                    progressDialog.setCancelable(false);

            final StorageReference reference=storageReference.child("images/"+ UUID.randomUUID().toString());
            reference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downUri=uri.toString();
                                    Toast.makeText(ChatActivity.this, ""+downUri, Toast.LENGTH_SHORT).show();
                                    sendMessage(downUri);
                                    imgUri=null;
                                    text.setText("");
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress=(int)(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading: "+progress+"%");

                        }
                    });

        }
        else {
            sendMessage("");
            text.setText("");
        }
    }
}
