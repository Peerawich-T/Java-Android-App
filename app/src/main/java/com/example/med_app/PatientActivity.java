package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.util.Calendar;

import model.Patient;
import service.DatabaseService;

public class PatientActivity extends AppCompatActivity {

    TextView verifyEmailPatientMsg, uidTextView;
    Button verifyEmailPatientBtn, refreshWidgetBtn;
    FirebaseAuth auth;
    FirebaseUser user ;
    ListView medToday;
    LayoutInflater inflater;
    AlertDialog.Builder deleteAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        auth = FirebaseAuth.getInstance();
        Button logOut = findViewById(R.id.logOutPatientBtn);
        verifyEmailPatientMsg = findViewById(R.id.verifyEmailText);
        verifyEmailPatientBtn = findViewById(R.id.verufyPatientEmail);
        medToday = findViewById(R.id.todayListView);
        inflater = getLayoutInflater();
        deleteAlert = new AlertDialog.Builder(this);
        uidTextView = findViewById(R.id.uidTextView);

        user = auth.getCurrentUser();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Patient");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String date = Calendar.getInstance().getTime().toString();

                for (DataSnapshot i : snapshot.getChildren()) {
                    Gson gson = new Gson();
                    Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                    if (patient.getUid().equals(auth.getUid())) {
                        uidTextView.setText("UID : "+patient.getUid());
                        ArrayList<String> showData = new ArrayList<>();
                        for (String j : patient.getMedicineBydDay(date.split(" ")[0])) {
                            if(!j.equals("antiNull"))
                                showData.add(j.split("_")[0] + " : " + j.split("_")[4].replace("t", ","));
                        }
                        ArrayAdapter<String> patientAdapter = new ArrayAdapter<String>(PatientActivity.this, R.layout.list_item, showData);
                        medToday.setAdapter(patientAdapter);
                        medToday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                                String currentMedName = showData.get(i).split(" : ")[0];
                                View view = inflater.inflate(R.layout.set_zero_pop, null);

                                deleteAlert.setTitle("Medicine out of Stock")
                                        .setMessage("Update medicine out of Stock")
                                        .setPositiveButton("update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                patient.setMedZero(currentMedName);
                                                DatabaseService databaseService = new DatabaseService();
                                                databaseService.updatePatient(patient, patient.getUid());

                                            }
                                        }).setNegativeButton("Cancel", null).setView(view).create().show();
                            }
                        });
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        if(!auth.getCurrentUser().isEmailVerified()){
            verifyEmailPatientMsg.setVisibility(View.VISIBLE);
            verifyEmailPatientBtn.setVisibility(View.VISIBLE);
        }


        verifyEmailPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PatientActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        verifyEmailPatientBtn.setVisibility(View.GONE);
                        verifyEmailPatientBtn.setVisibility(View.GONE);
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


        //getALL medicine data
        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("Patient");
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.getChildren()){
                    Gson gson = new Gson();
                    Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                    if(patient.getUid().equals(auth.getUid())){
                        for(String j : patient.getMedicines()){
                            String[] medData = j.split("_");


                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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