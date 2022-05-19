package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import model.Medicine;
import model.Patient;
import service.DatabaseService;

public class PatientInformationActivity extends AppCompatActivity {
    DatabaseReference patientRef;
    TextView patientFullNameTextView;
    Button addButton;
    LayoutInflater inflater;
    AlertDialog.Builder add_alert;
    EditText medicineNameEditText, dosageEditText, dosagesPerOnetime, expiredEditText, timeEditText;
    FirebaseAuth auth;
    CheckBox sunCheckBox, monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox;
    ListView medicineListView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_information);
        addButton = findViewById(R.id.addButton);
        medicineListView = findViewById(R.id.medicinesListView);
        inflater = getLayoutInflater();
        auth = FirebaseAuth.getInstance();
        add_alert = new AlertDialog.Builder(this);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View view) {
                Intent  i = getIntent();
                String patientUID = (String)i.getSerializableExtra("patientUID");
                Intent  next  = new Intent(PatientInformationActivity.this, AddMedicineActivity.class);
                next.putExtra("patientUID", patientUID);
                startActivity(next);


            }

        });
        patientFullNameTextView = findViewById(R.id.patientFullNameTextView);
        Intent i = getIntent();
        String patientUID = (String)i.getSerializableExtra("patientUID");
        patientRef = FirebaseDatabase.getInstance().getReference("Patient");
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot i : snapshot.getChildren()){
                    Gson gson = new Gson();
                    Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                    if(patient.getUid().equals(patientUID)){
                        //System.out.println(patient.getName());
                        patientFullNameTextView.setText(patient.getName());
                        ArrayList<String> medNames = new ArrayList<>();

                        for(String j : patient.getMedicines()){

                            if(!j.equals("antiNull")) {
                                String medName = j.split("_")[0];
                                medNames.add(medName);
                            }


                        }
                        ArrayAdapter<String> medicineAdapter = new ArrayAdapter<String>(PatientInformationActivity.this, R.layout.list_item, medNames );
                        medicineListView.setAdapter(medicineAdapter);
                        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(PatientInformationActivity.this, MedInfoActivity.class);
                                intent.putExtra("medName", patient.getMedicines().get(i+1));
                                intent.putExtra("patientUID", patientUID);
                                startActivity(intent);
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



    }
}