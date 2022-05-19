package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import model.Patient;
import service.DatabaseService;

public class MedInfoActivity extends AppCompatActivity {
    EditText  remain,expired;
    TextView medNameTextView, expiredTextView, timeTextView, dayTextView, dosageTextView, remainTextView;
    Button deleteBtn, updateBtn;
    DatabaseReference patientRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);
        Intent intent = getIntent();
        String medData  = (String)intent.getSerializableExtra("medName");

        medNameTextView = findViewById(R.id.medNameTextView);
        expiredTextView = findViewById(R.id.expiredTextView);
        timeTextView = findViewById(R.id.timeToTakeTextView);
        dayTextView = findViewById(R.id.dayToTakeTextView);
        dosageTextView = findViewById(R.id.dosageTextView);
        remainTextView = findViewById(R.id.remainTextView);
        remain = findViewById(R.id.updateRemainEdit);
        expired = findViewById(R.id.updateExpiredEdit);
        deleteBtn = findViewById(R.id.deleteMedBtn);
        updateBtn = findViewById(R.id.updateBtn);


        medNameTextView.setText("Medicine's name : "+medData.split("_")[0]);
        expiredTextView.setText("Expired date : "+medData.split("_")[1]);
        timeTextView .setText("Time to take : "+medData.split("_")[4].replace("t",","));
        dayTextView .setText("Day to take : "+medData.split("_")[5].replace("z",","));
        dosageTextView .setText("Dosage : "+medData.split("_")[3]);
        remainTextView.setText("Remain Value : "+medData.split("_")[2]);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String patientUID = (String)intent.getSerializableExtra("patientUID");

                patientRef = FirebaseDatabase.getInstance().getReference("Patient");
                patientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot i: snapshot.getChildren()){
                            Gson gson = new Gson();
                            Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);

                            if(patient.getUid().equals(patientUID)){

                                for(String j : patient.getMedicines()){
                                    if(j.equals(medData)){

                                        patient.getMedicines().remove(j);
                                        DatabaseService databaseService = new DatabaseService();
                                        databaseService.updatePatient(patient, patient.getUid());
                                        finish();

                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

        //update remain value and expired data
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expired.getText().toString().equals("") || remain.getText().toString().equals("")) {
                    if (expired.getText().toString().equals("")) {
                        expired.setError("Please fill this Text");

                    }
                    if (remain.getText().toString().equals("")) {
                        remain.setError("Please fill this Text");

                    }
                    return;
                }
                String patientUID = (String)intent.getSerializableExtra("patientUID");
                patientRef = FirebaseDatabase.getInstance().getReference("Patient");
                patientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot i: snapshot.getChildren()){
                            Gson gson = new Gson();
                            Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);

                            if(patient.getUid().equals(patientUID)){

                                for(String j : patient.getMedicines()){
                                    if(j.equals(medData)){

                                        patient.updateMed(medData.split("_")[0], remain.getText().toString(), expired.getText().toString());
                                        expiredTextView.setText("Expired date : "+expired.getText());
                                        remainTextView.setText("Remain Value : "+remain.getText());
                                        DatabaseService databaseService = new DatabaseService();
                                        databaseService.updatePatient(patient, patient.getUid());

                                    }
                                }
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
}