package com.farhad.questionpanel.AdminPanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farhad.questionpanel.MainActivity;
import com.farhad.questionpanel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {


    EditText notificationET;
    Button submit;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private FirebaseFirestore mFirestore;
    String uId;
    String pushId;
    ProgressDialog dialog;

    private RecyclerView recyclerView;
    private List<BlockUserClass> blockUserClassList;
    BlockUserAdapter adapter;
    BlockUserClass blockUserClass;
    private Button smsButton;

    int smsSubmitCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        setTitle("Notification");

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mFirestore = FirebaseFirestore.getInstance();


        blockUserClass = new BlockUserClass();
        blockUserClassList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        dialog = new ProgressDialog(this);

        if (user != null){
            uId = user.getUid();
            pushId = myRef.push().getKey();

            blockUser();

        }

        notificationET = findViewById(R.id.notifyTV_id);
        submit = findViewById(R.id.submitNotify);
        smsButton = findViewById(R.id.showSMS_submit_id);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification();
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                smsSubmitCount= smsSubmitCount+1;
                submit.setText("SMS Submit");
                smsButton.setVisibility(View.GONE);

            }
        });


    }

    private void blockUser() {

        myRef.child("BlockUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                   blockUserClassList.clear();

                   for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                       blockUserClass = snapshot.getValue(BlockUserClass.class);

                   }
                   blockUserClassList.add(blockUserClass);
                   adapter = new BlockUserAdapter(NotificationActivity.this,blockUserClassList);
                   recyclerView.setAdapter(adapter);
                   adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void notification() {

        String notify = notificationET.getText().toString();

        if (notify.isEmpty()){

            notificationET.setError("Enter Some Data");
        }else {

            dialog.setMessage("Data is uploading...");
            dialog.show();

            if (smsSubmitCount > 0){

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateAndTime = sdf.format(new Date());

                Map<String,Object> smsPost= new HashMap<>();
                smsPost.put("mTime",currentDateAndTime);
                smsPost.put("mSMS",notify);

                mFirestore.collection("SMS_Collection").add(smsPost).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            notificationET.setText("");
                            Toast.makeText(NotificationActivity.this, "Data Submit Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notificationET.setText("");
                        dialog.dismiss();
                        Toast.makeText(NotificationActivity.this, "Data Submit Field", Toast.LENGTH_SHORT).show();
                    }
                });


            }else {
                myRef.child("Notification").setValue(notify)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    dialog.dismiss();
                                    Toast.makeText(NotificationActivity.this, "Notification submit success", Toast.LENGTH_SHORT).show();

                                }else {
                                    dialog.dismiss();
                                    Toast.makeText(NotificationActivity.this, "Check net connection", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        Toast.makeText(NotificationActivity.this, "Check net connection", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(NotificationActivity.this, MainActivity.class));
        finish();
    }
}
