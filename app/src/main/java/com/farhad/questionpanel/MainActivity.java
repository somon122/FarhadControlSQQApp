package com.farhad.questionpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.farhad.questionpanel.AdminPanel.NotificationActivity;
import com.farhad.questionpanel.LogIn.LogInActivity;
import com.farhad.questionpanel.Withdraw.WithdrawActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    TextView balanceTv, profileName, profileEmail, notificationTv;
    CircleImageView circleImageView;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;
    String pushId;
    int mainPoints;
    int blockCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        balanceTv = findViewById(R.id.balanceShow_id);
        profileName = findViewById(R.id.profileName_id);
        profileEmail = findViewById(R.id.profileEmail_id);
        notificationTv = findViewById(R.id.notificationShow_id);
        circleImageView = findViewById(R.id.profileCircleImage_id);


        if (user != null) {
            uId = user.getUid();
            pushId = myRef.push().getKey();
            balanceControl();
            userProfile();
        }


    }


    private void userProfile() {

        user = auth.getCurrentUser();
        Picasso.get().load(user.getPhotoUrl()).placeholder(getDrawable(R.drawable.ic_launcher_background)).into(circleImageView);
        profileName.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logOut) {

            alert();
        }
        if (id == R.id.action_adminPanel) {

            adminPanel("122farhad#");

        }


        return super.onOptionsItemSelected(item);
    }


    private void alert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you Sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Successfully LogOut ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                        finish();


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this, "Thank You for Staying...", Toast.LENGTH_SHORT).show();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void balanceControl() {

        myRef.child("UserMainPoints").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    mainPoints = Integer.parseInt(value);
                    balanceTv.setText("Your Points : " + value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRef.child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    notificationTv.setText(value);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("UserMistakeAmount").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    blockCheck = Integer.parseInt(value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
        }

    }

    public void Share(View view) {

        shareApp();

    }

    public void InstallApp(View view) {

        try {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.farhad.quiz_question")));

        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.farhad.quiz_question")));
        }

    }

    public void withdrawButton(View view) {

        if (blockCheck <= 2) {
            if (mainPoints >= 20) {
                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
            } else {
                problemAlert();
            }

        } else {
            Toast.makeText(this, "Your account is already blocked", Toast.LENGTH_SHORT).show();
        }


    }

    private void shareApp() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "App link : " + "https://play.google.com/store/apps/details?id=" + "com.farhad.quiz_question";
        String shareSub = "Android App";
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Quiz_Question_app"));

    }


    private void adminPanel(final String password) {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.admin_control, null);


        final EditText passwordET = view1.findViewById(R.id.adminCheckPassword_id);
        Button submit = view1.findViewById(R.id.adminSubmit_id);


        builder.setTitle("Admin Panel");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPassword = passwordET.getText().toString();

                if (mPassword.isEmpty()) {

                    passwordET.setError("Please enter password");

                } else {

                    if (mPassword.equals(password)) {

                        Toast.makeText(MainActivity.this, "Password is matches", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, NotificationActivity.class));


                    } else {

                        Toast.makeText(MainActivity.this, "Password is not matches", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void problemAlert() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Sorry ..!")
                .setMessage("You have not enough Balance\n\nMinimum Withdraw 20Tk..!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
