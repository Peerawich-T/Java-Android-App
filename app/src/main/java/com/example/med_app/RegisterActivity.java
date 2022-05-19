package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import model.Caretaker;
import model.Medicine;
import model.Patient;
import service.DatabaseService;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText email;
    EditText password;
    EditText confirmPassword;
    CheckBox careTaker,patient;
    DatabaseService databaseService = new DatabaseService();
    TextView  roleError;
    EditText fullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button signIn = findViewById(R.id.signInBtn);

        email = findViewById(R.id.emailRegisEditText);
        password = findViewById(R.id.passwordRegisEditText);
        confirmPassword = findViewById(R.id.confirmPasswiordEditText);
        careTaker = findViewById(R.id.careTakerCheckbox);
        patient = findViewById(R.id.patientCheckBox);
        roleError = findViewById(R.id.roleErrorTextView);
        fAuth = FirebaseAuth.getInstance();
        fullName= findViewById(R.id.fullnameEditText);
        careTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patient.setChecked(false);
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                careTaker.setChecked(false);
            }
        });
        if(fullName.getText().toString().isEmpty()){
            fullName.setError("Full Name is required");
        }
        if(email.getText().toString().isEmpty()){
            email.setError("Email is required");
        }

        if(password.getText().toString().isEmpty()){
            password.setError("Password is required");
        }
        if(confirmPassword.getText().toString().isEmpty()){
            confirmPassword.setError("Please confirm your password");
        }
        if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            confirmPassword.setError("password not match");
        }

    }
    public void signInOnClk(View view) {
             fullName= findViewById(R.id.fullnameEditText);
            fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if(careTaker.isChecked()){
                        Caretaker caretaker = new Caretaker(fAuth.getCurrentUser().getUid(), fullName.getText().toString());
                        databaseService.addCareTakerToDB(caretaker);
                        startActivity(new Intent(getApplicationContext(), CareTakerActivity.class));
                    }
                    if(patient.isChecked()){
                        Patient patient = new Patient(fAuth.getCurrentUser().getUid(), fullName.getText().toString());
                        databaseService.addPatientToDB(patient);
                        startActivity(new Intent(getApplicationContext(), PatientActivity.class));
                    }
                    if(!(careTaker.isChecked() || patient.isChecked())){
                        roleError.setVisibility(View.VISIBLE);
                    }

                   finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


    }
}