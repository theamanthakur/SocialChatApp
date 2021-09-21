package com.ttl.ritz7chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.messages.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<messages> userMessagesList;
    private FirebaseAuth mAuth;

    private final static int MSG_TYPE_LEFT = 0;
    private final static int MSG_TYPE_right = 1;

    private DatabaseReference usersRef,msgRef,isAdminRef;
    Context context;
    int click = 0;


    public MessageAdapter (List<messages> userMessagesList,Context context)
    {
        this.userMessagesList = userMessagesList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_right){
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.single_chat_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.single_chat_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {


        String messageSenderId = mAuth.getCurrentUser().getUid();
        messages messages = userMessagesList.get(i);
//        while (i>=1) {
//            Messages msg2 = userMessagesList.get(i - 1);
//        }

        String fromUserID = messages.getFrom();
        String toUserID = messages.getTo();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.proimg).into(messageViewHolder.messageIv);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String message = messages.getMessage();
        String msgType = messages.getType();
        String senderUid = messages.getFrom();
        String time = messages.getTime();
        messageViewHolder.messageIv.setVisibility(View.GONE);
        messageViewHolder.messageTv.setVisibility(View.GONE);


        if (msgType.equals("text")){
            messageViewHolder.messageTv.setVisibility(View.VISIBLE);

            messageViewHolder.messageTv.setText(message);
            messageViewHolder.timeTv.setText(time);
            String date = messages.getDate().toString();
            if(i>=1) {
                messages msg2 = userMessagesList.get(i - 1);

                String datePrevious = msg2.getDate().toString();
                if (date.compareTo(datePrevious) == 0) {
                    messageViewHolder.dateTv.setVisibility(View.GONE);


                } else {
                    messageViewHolder.dateTv.setVisibility(View.VISIBLE);
                    messageViewHolder.dateTv.setText(date);
                }

            }

        }
        else if (msgType.equals("image")){

            messageViewHolder.messageIv.setVisibility(View.VISIBLE);
            messageViewHolder.timeTv.setText(time);
            try {
                Picasso.get().load(message).placeholder(R.drawable.user).into(messageViewHolder.messageIv);
            }catch (Exception e){
                messageViewHolder.messageIv.setImageResource(R.drawable.group);
            }

            ///*********Date*********////////////////
            String date = messages.getDate().toString();
            if(i>=1) {
                messages msg2 = userMessagesList.get(i - 1);

                String datePrevious = msg2.getDate().toString();
                if (date.compareTo(datePrevious) == 0) {
                    messageViewHolder.dateTv.setVisibility(View.GONE);


                } else {
                    messageViewHolder.dateTv.setVisibility(View.VISIBLE);
                    messageViewHolder.dateTv.setText(date);
                }

            }
        }

//        messageViewHolder.messageTv.setText(message);


//
//        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
//        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
//        messageViewHolder.senderMessageText.setVisibility(View.GONE);
//        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
//        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
//        messageViewHolder.receiverFile.setVisibility(View.GONE);
//        messageViewHolder.senderFile.setVisibility(View.GONE);
//        messageViewHolder.dateText.setVisibility(View.GONE);
//        messageViewHolder.senderTime.setVisibility(View.GONE);
//        messageViewHolder.recieverTime.setVisibility(View.GONE);
//        messageViewHolder.likeImgsender.setVisibility(View.GONE);
//        messageViewHolder.likeImgReciever.setVisibility(View.GONE);

//
//        if (fromMessageType.equals("text"))
//        {
//            if (fromUserID.equals(messageSenderId))
//            {
////                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
////                messageViewHolder.senderTime.setVisibility(View.VISIBLE);
////
////                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
//                messageViewHolder.messageTv.setTextColor(Color.BLACK);
//                messageViewHolder.messageTv.setText(messages.getMessage());
//                messageViewHolder.timeTv.setText(messages.getTime());
//
////                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        click++;
////                        Handler handler = new Handler();
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                if (click == 2){
////                                    messageViewHolder.likeImgsender.setVisibility(View.VISIBLE);
////                                    Toast.makeText(context, usersRef.toString(), Toast.LENGTH_SHORT).show();
////                                }else if (click == 3){
////                                    messageViewHolder.likeImgsender.setVisibility(View.GONE);
////                                }
////                                click=0;
////                            }
////                        },500);
////                    }
////                });
//
////                String date = messages.getDate().toString();
////                if(i>=1) {
////                    Messages msg2 = userMessagesList.get(i - 1);
////
////                    String datePrevious = msg2.getDate().toString();
////                    if (date.compareTo(datePrevious) == 0) {
////                        messageViewHolder.dateText.setVisibility(View.GONE);
////
////
////                    } else {
////                        messageViewHolder.dateText.setVisibility(View.VISIBLE);
////                        messageViewHolder.dateText.setText(date);
////                    }
////
////                }
//
//            }
////            else
////            {
////                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
////                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
////                messageViewHolder.recieverTime.setVisibility(View.VISIBLE);
////
////                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
////                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
////                messageViewHolder.receiverMessageText.setText(messages.getMessage());
////                messageViewHolder.recieverTime.setText(messages.getTime());
////
////                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        click++;
////                        Handler handler = new Handler();
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                if (click == 2){
////                                    messageViewHolder.likeImgReciever.setVisibility(View.VISIBLE);
////                                }else if (click == 3){
////                                    messageViewHolder.likeImgReciever.setVisibility(View.GONE);
////                                }
////                                click=0;
////                            }
////                        },500);
////                    }
////                });
//////****----------------Show Date ----------*************///////////////
////                if(i>=1) {
////                    Messages msg2 = userMessagesList.get(i - 1);
////                    String date = messages.getDate().toString();
////                    String datePrevious = msg2.getDate().toString();
////                    if (date.compareTo(datePrevious) == 0) {
////                        messageViewHolder.dateText.setVisibility(View.GONE);
////
////
////                    } else {
////                        messageViewHolder.dateText.setVisibility(View.VISIBLE);
////                        messageViewHolder.dateText.setText(date);
////                    }
////
////                }
//////************--------------------------*********-------------------***********
////            }
//        }else if(fromMessageType.equals("image")){
//            if (fromUserID.equals(messageSenderId)){
//                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
//                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
//
//            }else{
//                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
//                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
//
//            }
////
//            //****----------------Show Date ----------*************///////////////
//            if(i>=1) {
//                Messages msg2 = userMessagesList.get(i - 1);
//                String date = messages.getDate().toString();
//                String datePrevious = msg2.getDate().toString();
//                if (date.compareTo(datePrevious) == 0) {
//                    messageViewHolder.dateText.setVisibility(View.GONE);
//
//
//                } else {
//                    messageViewHolder.dateText.setVisibility(View.VISIBLE);
//                    messageViewHolder.dateText.setText(date);
//                }
//
//            }
////************--------------------------*********-------------------***********
//        }
//        else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx")){
//            if (fromUserID.equals(messageSenderId)){
//                messageViewHolder.senderFile.setVisibility(View.VISIBLE);
////                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
////                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);
//                if (fromMessageType.equals("pdf")) {
//                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ritz7-9c6ce.appspot.com/o/Image%20Files%2Fpdffile.png?alt=media&token=91f5bd79-4774-427d-bd33-32a001fe066c")
//                            .into(messageViewHolder.senderFile);
//                }else if (fromMessageType.equals("docx")){
//
//                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ritz7-9c6ce.appspot.com/o/Image%20Files%2Fdoc.png?alt=media&token=83e08096-2e9a-4798-aac1-4ddecd585822")
//                            .into(messageViewHolder.senderFile);
//                }
////                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
////                        messageViewHolder.itemView.getContext().startActivity(intent);
////                    }
////                });
////****----------------Show Date ----------*************///////////////
//                if(i>=1) {
//                    Messages msg2 = userMessagesList.get(i - 1);
//                    String date = messages.getDate().toString();
//                    String datePrevious = msg2.getDate().toString();
//                    if (date.compareTo(datePrevious) == 0) {
//                        messageViewHolder.dateText.setVisibility(View.GONE);
//
//
//                    } else {
//                        messageViewHolder.dateText.setVisibility(View.VISIBLE);
//                        messageViewHolder.dateText.setText(date);
//                    }
//
//                }
////************--------------------------*********-------------------***********
//            }
//            else {
//                messageViewHolder.receiverFile.setVisibility(View.VISIBLE);
////                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                if (fromMessageType.equals("pdf")) {
//                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ritz7-9c6ce.appspot.com/o/Image%20Files%2Fpdffile.png?alt=media&token=91f5bd79-4774-427d-bd33-32a001fe066c")
//                            .into(messageViewHolder.receiverFile);
//                }else if (fromMessageType.equals("docx")){
//
//                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ritz7-9c6ce.appspot.com/o/Image%20Files%2Fdoc.png?alt=media&token=83e08096-2e9a-4798-aac1-4ddecd585822")
//                            .into(messageViewHolder.receiverFile);
//                }
//
//
////                String date = messages.getDate().toString();
////
////                String datePrevious = msg2.getDate().toString();
////                if (date.compareTo(datePrevious) == 0){
////                    messageViewHolder.dateText.setVisibility(View.GONE);
////
////
////                }else{
////                    messageViewHolder.dateText.setVisibility(View.VISIBLE);
////                    messageViewHolder.dateText.setText(date);
////                }
//
//            }
//            //****----------------Show Date ----------*************///////////////
//            if(i>=1) {
//                Messages msg2 = userMessagesList.get(i - 1);
//                String date = messages.getDate().toString();
//                String datePrevious = msg2.getDate().toString();
//                if (date.compareTo(datePrevious) == 0) {
//                    messageViewHolder.dateText.setVisibility(View.GONE);
//
//
//                } else {
//                    messageViewHolder.dateText.setVisibility(View.VISIBLE);
//                    messageViewHolder.dateText.setText(date);
//                }
//
//            }
////************--------------------------*********-------------------***********
//        }
//
////        if (fromMessageType == messageSenderId)
////        {
////            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
////                @Override
////                public boolean onLongClick(View v) {
////                    if (userMessagesList.get(i).getType().equals("pdf") || userMessagesList.get(i).getType().equals("docx") ){
////
////                        CharSequence options[] = new CharSequence[]{
////                                "Download",
////                                "Delete for me",
////                                "Del for every"
////                        };
////                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
////                        builder.setTitle("Choose One");
////                        builder.setItems(options, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int pos) {
////                                if (pos == 0){
////                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
////                                    messageViewHolder.itemView.getContext().startActivity(intent);
////
////                                }else if (pos == 1){
////                                    deleteSentMessage(i,messageViewHolder);
////
////                                }else if (pos == 2){
////                                    deletedMessageForeveryone(i,messageViewHolder);
////
////                                }else if (pos == 3){
////
////                                }
////
////                            }
////                        });
////
////                    }
////                    return true;
////                }
////            });
//        }
        if (fromUserID.equals(messageSenderId)){

            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (userMessagesList.get(i).getType().equals("pdf") || userMessagesList.get(i).getType().equals("docx") ){
//                        CharSequence options[] = new CharSequence[]{
//                                "Download",
//                                "Delete for me",
//                                "Delete for everyone",
//
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//
//                        builder.getContext();
//                        builder.setTitle("Choose One");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//                                }else if (pos == 1){
//                                    deleteSentMessage(i,messageViewHolder);
//
//                                }else if (pos == 2){
//                                    deletedMessageForeveryone(i,messageViewHolder);
//
//                                }else if (pos == 3){
//
//                                }
//                            }
//                        });
//                        builder.show();

                    }else  if (userMessagesList.get(i).getType().equals("text") ){
//                        CharSequence optionstext[] = new CharSequence[]{
//                                "Assign Task",
//                                "Delete for me",
//                                "Delete for everyone",
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Choose One");
//                        builder.setItems(optionstext, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//                                    String taskUser = userMessagesList.get(i).getMessage().toString();
//                                    HashMap<String, Object> profileMap = new HashMap<>();
//                                    profileMap.put("task",taskUser);
//                                    isAdminRef = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(messageSenderId);
//                                    msgRef = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(toUserID);
//                                    isAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.hasChild("isAdmin")){
//                                                msgRef.updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {
//                                                            Toast.makeText(messageViewHolder.itemView.getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            Toast.makeText(messageViewHolder.itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                            else {
//                                                Toast.makeText(messageViewHolder.itemView.getContext(), "You're not Admin", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }else if (pos == 1){
//                                    deleteSentMessage(i,messageViewHolder);
//
//                                }else if (pos == 2){
//                                    deletedMessageForeveryone(i,messageViewHolder);
//
//                                }
//                            }
//                        });
//                        builder.show();

                    }else  if (userMessagesList.get(i).getType().equals("image") ){
//                        CharSequence options[] = new CharSequence[]{
//                                "View Image",
//                                "Delete for me",
//                                "Delete for everyone",
//
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Choose One");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),imageViewerActivity.class);
//                                    intent.putExtra("url",userMessagesList.get(i).getMessage());
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//
//                                }else if (pos == 1){
//                                    deleteSentMessage(i,messageViewHolder);
//
//                                }else if (pos == 2){
//                                    deletedMessageForeveryone(i,messageViewHolder);
//
//                                }else if (pos == 3){
//
//                                }
//                            }
//                        });
//                        builder.show();
                    }
                    return true;
                }
            });

        }
        else {
            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (userMessagesList.get(i).getType().equals("pdf") || userMessagesList.get(i).getType().equals("docx") ){
//                        CharSequence options[] = new CharSequence[]{
//                                "Download",
//                                "Delete for me",
//                                "Cancel",
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Choose One");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//                                }else if (pos == 1){
//                                    deleteRecievedMessage(i,messageViewHolder);
//
//                                }else if (pos == 2){
//
//                                }
//                            }
//                        });
//                        builder.show();
                    }else  if (userMessagesList.get(i).getType().equals("text") ){
//                        CharSequence options[] = new CharSequence[]{
//                                "Assign Task",
//                                "Delete for me",
//                                "Cancel",
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Choose One");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//
//                                    String taskUser = userMessagesList.get(i).getMessage().toString();
//                                    HashMap<String, Object> profileMap = new HashMap<>();
//                                    profileMap.put("task",taskUser);
//                                    isAdminRef = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(messageSenderId);
//                                    msgRef = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(fromUserID);
//                                    isAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.hasChild("isAdmin")){
//                                                msgRef.updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {
//                                                    Toast.makeText(messageViewHolder.itemView.getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    Toast.makeText(messageViewHolder.itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                                }
//                                                    }
//                                                });
//                                            }
//                                            else {
//                                        Toast.makeText(messageViewHolder.itemView.getContext(), "You're not Admin", Toast.LENGTH_SHORT).show();
//                                    }
//                                        }
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                }else if (pos == 1){
//                                    deleteRecievedMessage(i,messageViewHolder);
//
//                                }else if (pos == 2){
//
//                                }
//                            }
//                        });
//                        builder.show();
                    }else  if (userMessagesList.get(i).getType().equals("image") ){
//                        CharSequence options[] = new CharSequence[]{
//                                "View Image",
//                                "Delete for me",
//                                "Cancel",
//                        };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Choose One");
//                        builder.show();
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos) {
//                                if (pos == 0){
//
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),imageViewerActivity.class);
//                                    intent.putExtra("url",userMessagesList.get(i).getMessage());
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//                                }else if (pos == 1){
//                                    deleteRecievedMessage(i,messageViewHolder);
//                                }else if (pos == 2){
//
//                                }
//                            }
//                        });

                    }
                    return true;
                }
            });

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (userMessagesList.get(position).getFrom().equals(mAuth.getUid())){
            return MSG_TYPE_right;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Message Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error deleting message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteRecievedMessage(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Message Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error deleting message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void deletedMessageForeveryone(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(holder.itemView.getContext(), "Message Deleted For Everyone", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error deleting message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



//    @Override
//    public void applyTexts(String task) {
//
//    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
//        public TextView senderMessageText, receiverMessageText, dateText,senderTime,recieverTime;
//        public CircleImageView receiverProfileImage;
//        public ImageView messageSenderPicture, messageReceiverPicture,senderFile, receiverFile,
//        likeImgsender,likeImgReciever;

        TextView dateTv, messageTv, timeTv;
        ImageView messageIv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
//            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
//            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
//            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
//            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
//            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
//            dateText = (TextView) itemView.findViewById(R.id.dateText);
//            senderFile = itemView.findViewById(R.id.message_sender_file_view);
//            receiverFile = itemView.findViewById(R.id.message_receiver_file_view);
//            senderTime = (TextView) itemView.findViewById(R.id.senderTime);
//            recieverTime = (TextView) itemView.findViewById(R.id.recieverTime);
//            likeImgsender = itemView.findViewById(R.id.message_sender_like_view);
//            likeImgReciever = itemView.findViewById(R.id.message_reciever_like_view);


            messageTv = itemView.findViewById(R.id.messageTvSC);
            timeTv = itemView.findViewById(R.id.timeTvSC);
            messageIv = itemView.findViewById(R.id.messageIvSC);
            dateTv = itemView.findViewById(R.id.dateTv);
        }
    }


}
