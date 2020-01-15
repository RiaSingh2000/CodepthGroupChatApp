package com.codepth.groupchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ImageDialog extends AppCompatDialogFragment {
    EditText caption;
    ImageView selImg;
    ImageButton send;
    FirebaseUser fuser;
    Uri uri;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_dialog,null);
        builder.setView(view)
                .setTitle("")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        Bundle args = new Bundle();
        uri=Uri.parse(args.getString("uri"));
        selImg.setImageURI(uri);
        caption=view.findViewById(R.id.text);
        selImg=view.findViewById(R.id.sel_img);
        send=view.findViewById(R.id.send);
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendMessage();
            }
        });

        return  builder.create();
    }

    public void  sendMessage(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("message",caption.getText().toString());
        hashMap.put("img",uri);
        hashMap.put("sender",fuser.getUid());
        hashMap.put("senderName",fuser.getDisplayName());

        // String id=databaseReference.push().getKey();
        databaseReference.child("chats").push().setValue(hashMap);
    }
}
