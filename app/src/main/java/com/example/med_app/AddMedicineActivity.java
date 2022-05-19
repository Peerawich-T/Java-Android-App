package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import java.util.ArrayList;

import model.Medicine;
import model.Patient;
import service.DatabaseService;

public class AddMedicineActivity extends AppCompatActivity {

    Button addBtn;
    CheckBox sunCheckBox, monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox;
    EditText medicineNameEditText, dosageEditText, dosagesPerOnetime, timeEditText, expiredEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        sunCheckBox = findViewById(R.id.sunCheckBox);
        monCheckBox = findViewById(R.id.monCheckBox);
        tueCheckBox = findViewById(R.id.tueCheckBox);
        wedCheckBox = findViewById(R.id.wedCheckBox);
        thuCheckBox = findViewById(R.id.thuCheckBox);
        friCheckBox = findViewById(R.id.friCheckBox);
        satCheckBox = findViewById(R.id.satCheckBox);
        medicineNameEditText = findViewById(R.id.medcineNameEditText);
        dosageEditText = findViewById(R.id.dosagesEditText);
        dosagesPerOnetime = findViewById(R.id.dosagesPerOneTimeEditText);
        timeEditText = findViewById(R.id.timeEditText);
        expiredEditText = findViewById(R.id.expiredEditTextDate);
        addBtn = findViewById(R.id.addMedBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                String uid = (String) i.getSerializableExtra("patientUID");
                DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("Patient");
                patientRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {

                        }
                        else {


                            DataSnapshot i = task.getResult();
                            Gson gson = new Gson();
                            Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
//                          System.out.println(medicineNameEditText.getText().toString());
                            String medName = medicineNameEditText.getText().toString();
                            String expiredDate = expiredEditText.getText().toString();
                            String time = timeEditText.getText().toString();
                            int remainValue = Integer.parseInt(dosageEditText.getText().toString());
                            int dosagesPerOneTake = Integer.parseInt(dosagesPerOnetime.getText().toString());
                            ArrayList<String> date = new ArrayList<>();
                            if(sunCheckBox.isChecked()){
                                date.add("Sun");
                            }
                            if(monCheckBox.isChecked()){
                                date.add("Mon");
                            }
                            if(tueCheckBox.isChecked()){
                                date.add("Tue");
                            }
                            if(wedCheckBox.isChecked()){
                                date.add("Wed");
                            }
                            if(thuCheckBox.isChecked()){
                                date.add("Thu");
                            }
                            if(friCheckBox.isChecked()){
                                date.add("Fri");
                            }
                            if(satCheckBox.isChecked()){
                                date.add("Sat");
                            }
                            Medicine medicine = new Medicine(medName, expiredDate, remainValue, dosagesPerOneTake,date, time);
                            if(patient.dupMed(medicine)){
                                System.out.println("dup");
                                medicineNameEditText.setError("This med already added!");
                            }
                            else{
                                System.out.println("notDup");
                                patient.addMedicine(medicine);
                                DatabaseService databaseService = new DatabaseService();
                                databaseService.updatePatient(patient, patient.getUid());
                                finish();

                            }



                        }
                    }
                });




            }
        });
    }
}