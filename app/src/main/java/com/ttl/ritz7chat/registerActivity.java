package com.ttl.ritz7chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ttl.ritz7chat.utilities.Constants;

import java.util.HashMap;

public class registerActivity extends AppCompatActivity {


//    Button btnCreate;
//    EditText edtEmail, edtPass;
    TextView textHaveAcc;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    ProgressBar progressBar;
    ProgressDialog mdialogue;
private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgress;
    private com.ttl.ritz7chat.utilities.PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//          progressBar =(ProgressBar) findViewById(R.id.progressBar);

        signUpProgress    = findViewById(R.id.progressBarSignUp);
        inputFirstName       = findViewById(R.id.inputFirstName);
        inputLastName        = findViewById(R.id.inputLastName);
        inputEmail           = findViewById(R.id.inputEmail);
        inputPassword        = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUp         = findViewById(R.id.buttonSignUp);
        textHaveAcc = findViewById(R.id.already_have_acc);
        mdialogue = new ProgressDialog(this);

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.textSignIn).setOnClickListener(view -> onBackPressed());


//        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
//        mAuth.signOut();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        InitializeFeilds();
        textHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this,loginActivity.class));
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegister();

            }
        });
    }

    private void UserRegister() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            inputPassword.setError("Enter Valid Password");
            inputPassword.requestFocus();
            return;
        }else if (password.length()<6){
            inputPassword.setError("Enter more than 6 character");
            inputPassword.requestFocus();
            return;
        }
        mdialogue.setTitle("Creating New Account...");
        mdialogue.setMessage("Just a moment, We're setting up things for you...");
        mdialogue.setCanceledOnTouchOutside(true);
        mdialogue.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

//                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
//
                    String currUserId = mAuth.getCurrentUser().getUid();
                    mdatabase.child("UsersChat").child(currUserId).setValue("");
//
//                    mdatabase.child("Users").child(currUserId).child("device_token")
//                            .setValue(deviceToken);
                    signUp();
                    mdialogue.dismiss();
                    sendToMainActivity();
                    Toast.makeText(registerActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                }else{
                    mdialogue.dismiss();
                    String msg = task.getException().toString();
                    Toast.makeText(registerActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(registerActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void InitializeFeilds() {
//        edtEmail = findViewById(R.id.inputEmail);
//        edtPass = findViewById(R.id.regisPass);
//        btnCreate = findViewById(R.id.btnCreateAcc);
//        mdialogue = new ProgressDialog(this);



    }

    private void signUp() {
//        buttonSignUp.setVisibility(View.INVISIBLE);
//        signUpProgress.setVisibility(View.VISIBLE);

        FirebaseFirestore database    = FirebaseFirestore.getInstance();
        HashMap<String, Object> users = new HashMap<>();
        users.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
        users.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
        users.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
        users.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(users)
                .addOnSuccessListener(documentReference -> {
//                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
//                    preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
//                    preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    signUpProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(registerActivity.this, "Error woi : "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}