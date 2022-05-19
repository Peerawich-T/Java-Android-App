package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import model.Caretaker;
import model.Patient;


public class CareTakerActivity extends AppCompatActivity {

    Caretaker currentCaretaker;

    TextView verifyEmailMsg;
    Button verifyEmailBtn, addPatientBtn, checkMedBtn;
    FirebaseAuth auth;
    FirebaseUser user;
    ListView patientListView;
    ArrayList<String> patientName;
    DatabaseReference myRef, patientRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        Button logOut = findViewById(R.id.logOutBtn);
        verifyEmailMsg = findViewById(R.id.textView);
        verifyEmailBtn = findViewById(R.id.verify);
        addPatientBtn = findViewById(R.id.addPatientBtn);
        patientListView = findViewById(R.id.patientListView);




        // initiate caretaker
        myRef = FirebaseDatabase.getInstance().getReference("Caretaker");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.getChildren()){
                    Gson gson = new Gson();
                    Caretaker caretaker = gson.fromJson(i.getValue().toString(), Caretaker.class);
                    if(caretaker.getUID().equals(auth.getCurrentUser().getUid())){
                        patientRef = FirebaseDatabase.getInstance().getReference("Patient");
                        patientRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                patientName = new ArrayList<>();
                                for(DataSnapshot i : snapshot.getChildren()){
                                    Gson gson = new Gson();
                                    Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                                if(!caretaker.getPatients().isEmpty()) {
                                    for (String j : caretaker.getPatients()) {
                                        if (j.equals(patient.getUid())) {
                                            patientName.add(patient.getName());
                                        }
                                    }
                                }

                                }
                                ArrayAdapter<String> patientAdapter = new ArrayAdapter<String>(CareTakerActivity.this, R.layout.list_item, patientName );
                               patientListView.setAdapter(patientAdapter);
                               patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                       DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Patient");
                                       myRef.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                               for(DataSnapshot k : snapshot.getChildren()){
                                                   Gson gson  = new Gson();
                                                   Patient patient = gson.fromJson(k.getValue().toString(), Patient.class);
                                                   if(patient.getName().equals(patientName.get(i))){
                                                       String patientUID = patient.getUid();
                                                       Intent a  = new Intent(CareTakerActivity.this,PatientInformationActivity.class);
                                                       a.putExtra("patientUID", patientUID);
                                                       startActivity(a);
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                           }
                                       });


                                   }
                               });


                            }



                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        user = auth.getCurrentUser();


        if(!auth.getCurrentUser().isEmailVerified()){
            verifyEmailMsg.setVisibility(View.VISIBLE);
            verifyEmailBtn.setVisibility(View.VISIBLE);
        }

        verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CareTakerActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        verifyEmailBtn.setVisibility(View.GONE);
                        verifyEmailMsg.setVisibility(View.GONE);
                    }
                });
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), AddPatientActivity.class));
                finish();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

       return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.resetUserPassword){
            startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



}