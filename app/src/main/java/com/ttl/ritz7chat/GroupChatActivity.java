package com.ttl.ritz7chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText userMsgText;
    private TextView groupTitle;
    CircleImageView groupIcon;
    private ImageButton btnSendMsg, btnFile;
    RecyclerView chatRv;
    ProgressDialog dialog;
    private ScrollView scrollView;
    String getGroupId, myGroupRole = "", currUserId, currUsername, currDate, currTime;
    DatabaseReference databaseReference, groupRef, groupMsgRefKey;
    FirebaseAuth auth, currentUserID;
    ImageView btnAdd, btnInfo;
    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl;
    private Uri fileuri;
    StorageTask uploadTask;
    //    ProgressDialog dialog;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 438;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1000;

    private String[] camPermission;
    private String[] strgPermission;

    private AdapterGroupChat adapter;
    private ArrayList<modeGroupChat> groupChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        getGroupId = getIntent().getExtras().get("groupId").toString();


        chatRv = findViewById(R.id.rvGroupChat);
        auth = FirebaseAuth.getInstance();
        currUserId = auth.getCurrentUser().getUid();

        camPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        strgPermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        initializeFeilds();
        getUserInfo();
        loadGroupInfo();
        loadGroupChats();
        loadMyGroupRole();
        if (myGroupRole.equals("participant")) {
            btnAdd.setVisibility(View.GONE);

        }

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = userMsgText.getText().toString().trim();

                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(GroupChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    saveMsgToDatabase();
                    userMsgText.setText("");
                }
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(GroupChatActivity.this, ""+getGroupId, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(GroupChatActivity.this, GroupParticipantAddActivity.class);
//                intent.putExtra("groupId", getGroupId);
//                startActivity(intent);
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(GroupChatActivity.this, groupInfoActivity.class);
//                intent.putExtra("groupId", getGroupId);
//                startActivity(intent);
            }
        });

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Images",
                        "Camera",
                        "PDF File",
                        "Doc File"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
                builder.setTitle("Select the File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);


                        }
                        if (i==1)
                        {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,101);
                        }
                        if (i == 2) {
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select PDF File"), 438);
                        }
                        if (i == 3) {
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select MS Word File"), 438);
                        }
                    }
                });

                builder.show();
            }
        });
        
    }



    private void loadMyGroupRole () {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
        ref1.child(getGroupId).child("Participants")
                .orderByChild("uid").equalTo(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            myGroupRole = "" + ds.child("role").getValue();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadGroupChats () {
        groupChatList = new ArrayList<>();

        groupRef.child(getGroupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupChatList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            modeGroupChat model = ds.getValue(modeGroupChat.class);
                            groupChatList.add(model);
                        }
                        adapter = new AdapterGroupChat(GroupChatActivity.this, groupChatList);
                        chatRv.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void loadGroupInfo () {

        groupRef.orderByChild("groupId").equalTo(getGroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String grouptitle = "" + ds.child("groupTitle").getValue();
                            String groupDesc = "" + ds.child("groupDescription").getValue();
                            String groupIconUrl = "" + ds.child("groupIcon").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String createdBy = "" + ds.child("createdBy").getValue();
                            groupTitle.setText(grouptitle);
                            try {
                                Picasso.get().load(groupIconUrl).placeholder(R.drawable.group).into(groupIcon);
                            } catch (Exception e) {
                                groupIcon.setImageResource(R.drawable.group);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        groupRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    displayMsg(snapshot);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    displayMsg(snapshot);
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void displayMsg(DataSnapshot snapshot) {
//        Iterator iterator = snapshot.getChildren().iterator();
//
//        while (iterator.hasNext()){
//      //            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
////            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
////            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
////            textViewDisplayMsg.append(chatName + ":\n" + chatMsg + "\n" + chatTime + "    " + chatDate + "\n\n\n");
////            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
////        }
////    }      String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();


    private void saveMsgToDatabase () {

        String message = userMsgText.getText().toString();
        String timestamp = "" + System.currentTimeMillis();


        Calendar cForDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
        currDate = dateFormat.format(cForDate.getTime());

        Calendar cForTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        currTime = timeFormat.format(cForTime.getTime());

        HashMap<String, Object> msgInfoMap = new HashMap<>();

        msgInfoMap.put("sender", auth.getUid());
        msgInfoMap.put("message", message);
        msgInfoMap.put("date", currDate);
        msgInfoMap.put("timestamp", timestamp);
        msgInfoMap.put("time", currTime);
        msgInfoMap.put("type", "text");
        groupRef.child(getGroupId).child("Messages").child(timestamp)
                .setValue(msgInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        }
    }

    private void getUserInfo () {

        databaseReference.child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currUsername = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFeilds () {

        toolbar = findViewById(R.id.groupChatLayout);
        groupTitle = findViewById(R.id.groupTitletv);
        groupIcon = findViewById(R.id.groupIcon);
        btnFile = findViewById(R.id.btnFiles);
        btnAdd = findViewById(R.id.btnAddPart);
        btnInfo = findViewById(R.id.btnInfo);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getGroupName);
        userMsgText = findViewById(R.id.input_groupMsg);
        btnSendMsg = findViewById(R.id.send_msg);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        dialog = new ProgressDialog(this);
//        scrollView = findViewById(R.id.groupChatScrollview);
//        textViewDisplayMsg = findViewById(R.id.grpChatMsgDisplay);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String timestamp = ""+System.currentTimeMillis();
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 101){
                onCaptureImageResult(data);
            }
        }
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            dialog.setTitle("Sending File");
            dialog.setMessage("Please wait while image is uploading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            fileuri = data.getData();
            if (!checker.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
//                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();

//                final String messagePushID = userMessageKeyRef.getKey();
                StorageReference filePath = storageReference.child(fileuri.getLastPathSegment() + "." + checker);

                filePath.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String filaName = fileuri.getLastPathSegment();

                            Map messageTextBody = new HashMap<>();
//                            messageTextBody.put("message", task.getResult().getDownloadUrl().toString());
                            messageTextBody.put("name", fileuri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("sender", auth.getUid());
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(timestamp, messageTextBody);
//                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                            groupRef.child(getGroupId).child("Messages").child(timestamp)
                                    .setValue(messageTextBody)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(GroupChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage((int) p + " % Uploading...");
                    }
                });

            } else if (checker.equals("image")) {
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
//                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();
//
//                final String messagePushID = userMessageKeyRef.getKey();
//                StorageReference filePath = storageReference.child(messagePushID + "."+"jpg");
//                uploadTask = filePath.putFile(fileuri);
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//                        if (!task.isSuccessful()){
//                            throw task.getException();
//                        }
//                        return  filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()){
//
//                            String filnePath = "Group_Imgs/" + ""+System.currentTimeMillis();
//                            StorageReference ref = FirebaseStorage.getInstance().getReference(filnePath);
//
//                            ref.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    HashMap<String,Object> msgInfoMap = new HashMap<>();
//
//                                    msgInfoMap.put("sender", auth.getUid());
//                                    msgInfoMap.put("message", filePath.getResult().getDownloadUrl().toString());
//                                    msgInfoMap.put("name", fileuri.getLastPathSegment());
//                                    msgInfoMap.put("type", checker);
//                                    msgInfoMap.put("date", currDate);
//                                    msgInfoMap.put("timestamp", timestamp);
//                                    msgInfoMap.put("time", currTime);
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });
//
//
//
//                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful())
//                                    {
//                                        dialog.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Image Sent Successfully...", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else
//                                    {
//                                        dialog.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                    }
//                                    MessageInputText.setText("");
//                                }
//                            });
//                        }
//                    }
//                });
//            }

            }
        }

//    private void sendImageGallery() {
//            final ProgressDialog dialog = new ProgressDialog(this);
//            dialog.setTitle("Sending File");
//            dialog.setMessage("Please wait while image is uploading...");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.show();
//
//            String filnePath = "Group_Imgs/" + ""+System.currentTimeMillis();
//        StorageReference ref = FirebaseStorage.getInstance().getReference(filnePath);
//
//        ref.putFile(image_uri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(GroupChatActivity.this, "IN AC", Toast.LENGTH_SHORT).show();
//                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!task.isSuccessful());
//                        Uri m = task.getResult();
//
//                        if (task.isSuccessful()){
////                            Uri downUrl = task.getResult();
//                            Toast.makeText(GroupChatActivity.this, "IN RC", Toast.LENGTH_SHORT).show();
//                            String timestamp =""+ System.currentTimeMillis();
//                            Calendar cForDate = Calendar.getInstance();
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
//                            currDate = dateFormat.format(cForDate.getTime());
//
//                            Calendar cForTime = Calendar.getInstance();
//                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
//                            currTime = timeFormat.format(cForTime.getTime());
//
//                            HashMap<String,Object> msgInfoMap = new HashMap<>();
//
//                            msgInfoMap.put("sender", auth.getUid());
//                            msgInfoMap.put("message",downUrl );
//                            msgInfoMap.put("date", currDate);
//                            msgInfoMap.put("timestamp", timestamp);
//                            msgInfoMap.put("time", currTime);
//                            msgInfoMap.put("type","image");
//                            groupRef.child(getGroupId).child("Messages").child(timestamp)
//                                    .setValue(msgInfoMap)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            dialog.dismiss();
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    dialog.dismiss();
//                                }
//                            });
//
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[] = bytes.toByteArray();
        uploadToFirebase(bb);
    }

    private void uploadToFirebase(byte[] bb) {
        dialog.setTitle("Sending File");
        dialog.setMessage("Please wait while image is uploading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        checker = "camera";
        String timestamp = String.valueOf(System.currentTimeMillis());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
        StorageReference sr = storageReference.child("mypic" + "." + "jpg");

        sr.putBytes(bb).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Map messageTextBody = new HashMap<>();
//                    messageTextBody.put("message", task.getResult().getDownloadUrl().toString());
                    messageTextBody.put("type", checker);
                    messageTextBody.put("sender", auth.getUid());
                    messageTextBody.put("time", saveCurrentTime);
                    messageTextBody.put("date", saveCurrentDate);

                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(timestamp, messageTextBody);
                    groupRef.child(getGroupId).child("Messages").child(timestamp)
                            .setValue(messageTextBody)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

    }

}