package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import model.Caretaker;
import model.Patient;
import service.DatabaseService;

public class AddPatientActivity extends AppCompatActivity {
    DatabaseReference myRef, caretakerRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    EditText patientUID;
    Button addPatientBtn;
    DatabaseService databaseService = new DatabaseService();
    ArrayList<Patient> patients;
    Caretaker currentCaretaker;
    Boolean checkUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        patientUID = findViewById(R.id.patientUIDText);
        addPatientBtn = findViewById(R.id.addPatientUID);
        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                user = auth.getCurrentUser();

                myRef = FirebaseDatabase.getInstance().getReference("Patient");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        // check patient is in database
                        System.out.println("shit");
                        boolean checkInDB = false;
                        for(DataSnapshot i : snapshot.getChildren()){
                            Gson gson = new Gson();
                            Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                            if(patient.getUid().equals(patientUID.getText().toString())){
                                checkInDB = true;
                            }

                        }
                        if(!checkInDB){
                            Toast.makeText(AddPatientActivity.this, "Don't have patient in System", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        caretakerRef = FirebaseDatabase.getInstance().getReference("Caretaker");
                        caretakerRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                                for(DataSnapshot i : snapshot.getChildren()){
                                    Gson gson = new Gson();
                                    Caretaker caretaker  = gson.fromJson(i.getValue().toString(), Caretaker.class);
                                    if(caretaker.getUID().equals(auth.getCurrentUser().getUid())){
                                        currentCaretaker = caretaker;
                                        if(caretaker.getPatients().contains(patientUID.getText().toString())){
                                            Toast.makeText(AddPatientActivity.this, "Patient Already add", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else{
                                            caretaker.addPatient(patientUID.getText().toString());
                                            updateCaretakerAndChangeLayout(caretaker);
                                        }
                                    }
                                }




                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Toast.makeText(AddPatientActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(AddPatientActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    public void updateCaretakerAndChangeLayout(Caretaker caretaker){
        databaseService.updateCareTaker(caretaker, caretaker.getUID());
        startActivity(new Intent(AddPatientActivity.this, CareTakerActivity.class));
        finish();

    }


}
