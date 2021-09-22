package com.ttl.ritz7chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupList extends RecyclerView.Adapter<AdapterGroupList.holderGroupList>{

    private Context context;
    private ArrayList<modelGroupsList> groupList;

    public AdapterGroupList(Context context, ArrayList<modelGroupsList> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public holderGroupList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group_chat, parent, false);
        return new holderGroupList(view);

    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupList holder, int position) {

        modelGroupsList model = groupList.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.sender.setText("");
        holder.grpMsg.setText("");
        holder.time.setText("");

        loadLastMessage(model, holder);

        holder.grpName.setText(groupTitle);

        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.group).into(holder.imageView);
        }catch (Exception e){
            holder.imageView.setImageResource(R.drawable.group);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(context,GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    private void loadLastMessage(modelGroupsList model, holderGroupList holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String msg = ""+ds.child("message").getValue();
                            String time = ""+ds.child("time").getValue();
                            String uid = ""+ds.child("sender").getValue();
                            String msgType = ""+ds.child("type").getValue();

                            if (msgType.equals("camera") || msgType.equals("image")) {
                                holder.grpMsg.setText("Sent Image");
                            }else {
                                holder.grpMsg.setText(msg);
                            }
                            holder.time.setText(time);

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UsersChat");
                            userRef.orderByChild("uid").equalTo(uid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                String name = ""+ds.child("name").getValue();

                                                holder.sender.setText(name);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class holderGroupList extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView grpName, sender, grpMsg, time;

        public holderGroupList(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.groupImage);
            grpName = itemView.findViewById(R.id.textgrpName);
            sender = itemView.findViewById(R.id.textmsgsender);
            grpMsg = itemView.findViewById(R.id.textmsg);
            time = itemView.findViewById(R.id.textTime);

        }
    }
}
