package com.codepth.groupchat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepth.groupchat.Model.MessageModel;
import com.codepth.groupchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    Context context;
    ArrayList<MessageModel>msg_list;
    String fuerId;
    int n;

    public  static  final  int  MSG_TYPE_LEFT=0;
    public  static  final  int  MSG_TYPE_RIGHT=1;



    public ChatsAdapter(Context context, ArrayList<MessageModel> msg_list,String fuserId) {
        this.context = context;
        this.msg_list = msg_list;
        this.fuerId=fuserId;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        n=viewType;
        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_layout_left,parent,false);
            return new ChatsAdapter.ChatsViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_layout_right,parent,false);
            return new ChatsAdapter.ChatsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int p) {
        MessageModel obj=msg_list.get(p);
            if(obj.getMsg()!=""){
                holder.msg.setVisibility(View.VISIBLE);
                holder.msg.setText(obj.getMsg());
            }

            if(obj.getImgUri()!="") {
                holder.msg_img.setVisibility(View.VISIBLE);
                Glide.with(context).load(Uri.parse(obj.getImgUri())).into(holder.msg_img);
            }
            if(getItemViewType(p)==MSG_TYPE_LEFT)
                if(obj.getSender()!="")
                    holder.sender.setText(obj.getSenderName());

    }

    @Override
    public int getItemCount() {
        return msg_list.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder{
        TextView sender,msg;
        ImageView msg_img;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            if(n==MSG_TYPE_LEFT) {
                sender = itemView.findViewById(R.id.send);
                msg = itemView.findViewById(R.id.message);
                msg_img = itemView.findViewById(R.id.msg_img);
            }
            else {
                msg = itemView.findViewById(R.id.message);
                msg_img = itemView.findViewById(R.id.msg_img);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        //firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(msg_list.get(position).getSender().equals(fuerId))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
