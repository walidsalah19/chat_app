package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


public class registar extends AppCompatActivity {
    private FirebaseAuth authreg;
    private EditText phone_virify,text_virify;
    private Button btn_virfy,virify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken  mResendToken;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        authreg = FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        phone_virify = (EditText) findViewById(R.id.phonenumber);
        btn_virfy = findViewById(R.id.button_virify);
        virify = (Button) findViewById(R.id.virify);
        text_virify = findViewById(R.id.virify_text);
        virify.setVisibility(View.INVISIBLE);
        text_virify.setVisibility(View.INVISIBLE);
        btn_virfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone =phone_virify.getText().toString();
                if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(registar.this, "enter you'r phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(authreg)
                                    .setPhoneNumber(phone)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(registar.this)                 // Activity (for callback binding)
                                    .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(registar.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;
                virify.setVisibility(View.VISIBLE);
                text_virify.setVisibility(View.VISIBLE);
                phone_virify.setVisibility(View.INVISIBLE);
                btn_virfy.setVisibility(View.INVISIBLE);
                Toast.makeText(registar.this, "code was sanded", Toast.LENGTH_SHORT).show();
            }
        };
        virify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String viry=text_virify.getText().toString();
                if(TextUtils.isEmpty(viry))
                {
                    Toast.makeText(registar.this, "enter the code", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, viry);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        authreg.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            gotomain();
                        }
                        else {

                        }
                    }
                });
    }
    private void gotomain()
    {
        String user=authreg.getCurrentUser().getUid();
        reference.child(user);
        Intent l=new Intent(registar.this,MainActivity.class);
        startActivity(l);
        finish();
    }}