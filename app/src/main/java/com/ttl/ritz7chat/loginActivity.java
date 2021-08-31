package com.ttl.ritz7chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ttl.ritz7chat.utilities.Constants;
import com.ttl.ritz7chat.utilities.PreferenceManager;

public class loginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgress;
    private PreferenceManager preferenceManager;
    FirebaseUser currentUser;

    FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    ProgressDialog dialogue;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialogue = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
//        UsersRef = FirebaseDatabase.getInstance().getReference().child("UsersChat");
                currentUser = mAuth.getCurrentUser();


        findViewById(R.id.textSignUp).setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), registerActivity.class)));

        preferenceManager = new PreferenceManager(getApplicationContext());
        signInProgress    = findViewById(R.id.progressBarSignIn);

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.textSignUp).setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), registerActivity.class)));

        inputEmail    = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn  = findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(view -> {
            signIn();
        });
    }

    private void signIn() {

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

        dialogue.setTitle("Signing in...");
        dialogue.setMessage("Just a moment, Logging you in Ritz7Chat");
        dialogue.setCanceledOnTouchOutside(true);
        dialogue.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(loginActivity.this, ""+currentUser, Toast.LENGTH_SHORT).show();
//                String currentUserId = mAuth.getCurrentUser().getUid();

//                String deviceToken = FirebaseInstanceId.getInstance().getToken();
//
//                UsersRef.child(currentUserId).child("device_token")
//                        .setValue(deviceToken)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful())
//                                {
//                                    sendToMainActivity();
//                                    Toast.makeText(loginActivity.this, "Logged in Successful...", Toast.LENGTH_SHORT).show();
//                                    dialogue.dismiss();
//                                }
//                            }
//                        });
                if (task.isSuccessful()){
                    Toast.makeText(loginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    dialogue.dismiss();
                    sendToMainActivity();
                    Toast.makeText(loginActivity.this, "Welcome To Ritz7Chat", Toast.LENGTH_SHORT).show();
                }else{
                    dialogue.dismiss();
                    String err = task.getException().toString();
                    Toast.makeText(getApplicationContext(),err,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendToMainActivity() {
        Intent intent = new Intent(loginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}