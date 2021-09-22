package com.ttl.ritz7chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.holderGroupChat> {

    private final static int MSG_TYPE_LEFT = 0;
    private final static int MSG_TYPE_right = 1;

    private Context context;
    private ArrayList<modeGroupChat> modelGroupChatList;
    private FirebaseAuth firebaseAuth;



    public AdapterGroupChat(Context context, ArrayList<modeGroupChat> modelGroupChatList) {
        this.context = context;
        this.modelGroupChatList = modelGroupChatList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public holderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_right){
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.row_chat_right, parent, false);
            return new holderGroupChat(view);
        }else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.row_chat_left, parent, false);
            return new holderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupChat holder, int position) {

        modeGroupChat model = modelGroupChatList.get(position);
        String message = model.getMessage();
        String msgType = model.getType();
        String senderUid = model.getSender();
        String time = model.getTime();

        if (msgType.equals("text")){
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setText(message);

        }
        else if (msgType.equals("image")){
            holder.messageTv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(message).placeholder(R.drawable.user).into(holder.messageIv);
            }catch (Exception e){
                holder.messageIv.setImageResource(R.drawable.group);
            }

        }else if (msgType.equals("pdf")){
            holder.messageTv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(message).placeholder(R.drawable.user).into(holder.messageIv);
            }catch (Exception e){
                holder.messageIv.setImageResource(R.drawable.group);
            }

        }else if (msgType.equals("camera")){
            holder.messageTv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(message).placeholder(R.drawable.user).into(holder.messageIv);
            }catch (Exception e){
//                holder.messageIv.setImageResource(R.drawable.group);
            }
        }

        holder.messageTv.setText(message);
        holder.timeTv.setText(time);
        if (!modelGroupChatList.get(position).getSender().equals(firebaseAuth.getUid())) {
            setUsername(model, holder);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (modelGroupChatList.get(position).getSender().equals(firebaseAuth.getUid().toString())){
            return MSG_TYPE_right;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    private void setUsername(modeGroupChat model, final holderGroupChat holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UsersChat");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            holder.nameTv.setText(name);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelGroupChatList.size();
    }

    class holderGroupChat extends RecyclerView.ViewHolder{
        TextView nameTv, messageTv, timeTv;
        ImageView messageIv;
        public holderGroupChat(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            messageIv = itemView.findViewById(R.id.messageIv);
        }
    }
}
