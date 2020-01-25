package com.farhad.questionpanel.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.farhad.questionpanel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailET;
    private Button submitButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_possword_reset);

        setTitle("Password Reset");
        auth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.passResetEmail_id);
        submitButton = findViewById(R.id.passResetButton_id);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString();

                if (email.isEmpty()) {
                    emailET.setText("Please enter your Email Address");
                } else {

                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {

                                passResetSentAlert();

                            } else {
                                Toast.makeText(PasswordResetActivity.this, "Please Try again..", Toast.LENGTH_SHORT).show();
                                emailET.setText("");
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PasswordResetActivity.this, "Please Try again..", Toast.LENGTH_SHORT).show();
                            emailET.setText("");

                        }
                    });

                }

            }



            });


    }

    private void passResetSentAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordResetActivity.this);

        builder.setTitle("Alert..!");
        builder.setMessage("Please check your Email then click those link and Reset your password")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }



}
