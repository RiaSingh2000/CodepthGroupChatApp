package com.codepth.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codepth.groupchat.Adapters.ChatsAdapter;
import com.codepth.groupchat.Common.VerticalSpacingItemDecoration;
import com.codepth.groupchat.Model.MessageModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    //Button logOut;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    RecyclerView messages;
    ArrayList<MessageModel> msg_list;
    EditText text;
    ImageButton camera,send;

    DatabaseReference databaseReference;
    FirebaseUser fuser;



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

        LinearLayoutManager manager=new LinearLayoutManager(ChatActivity.this);
        messages.setLayoutManager(manager);
        manager.setStackFromEnd(true);

        messages.addItemDecoration(new VerticalSpacingItemDecoration(20));

        messages.setHasFixedSize(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                text.setText("");
                receiveMessage();
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

    public void  sendMessage(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("message",text.getText().toString());
        hashMap.put("img","");
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
}
