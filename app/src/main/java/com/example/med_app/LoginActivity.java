package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import model.Caretaker;
import model.Patient;

public class LoginActivity extends AppCompatActivity {
    private Button register, login, forgetPassword;
    private EditText username, password;
    FirebaseAuth firebaseAuth;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    FirebaseUser user;
    CheckBox caretaker, patient;
    TextView roleError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         reset_alert = new AlertDialog.Builder(this);
         inflater = getLayoutInflater();
         register = findViewById(R.id.registerBtn);
         username = findViewById(R.id.emailEditText);
         login   = findViewById(R.id.login);
         password = findViewById(R.id.passwordEdittext);
         forgetPassword = findViewById(R.id.forgetPassword);
         caretaker = findViewById(R.id.careTakerLogInChecKBox);
         patient = findViewById(R.id.patientLogInCheckBox);
         roleError = findViewById(R.id.roleErrorLogin);

        caretaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patient.setChecked(false);
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caretaker.setChecked(false);
            }
        });
         firebaseAuth = FirebaseAuth.getInstance();

         forgetPassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 View view = inflater.inflate(R.layout.reset_pop, null);
                 reset_alert.setTitle("Reset Forgot password ?")
                         .setMessage("Enter Your email to get Password Reset Link.")
                         .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int which) {

                                 EditText email = view.findViewById(R.id.resetEmailEditText);
                                 if(email.getText().toString().isEmpty()){
                                     email.setError("Required field");
                                     return;
                                 }
                                 firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {
                                         Toast.makeText(LoginActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull @NotNull Exception e) {
                                         Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                     }
                                 });
                             }
                         }).setNegativeButton("Cancel", null).setView(view).create().show();
             }
         });
         register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
              finish();
            }
        });
         login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if (username.getText().toString().isEmpty()) {
                     username.setError("Email is Missing!");
                     return;
                 }
                 if (password.getText().toString().isEmpty()) {
                     password.setError("Password is Missing!");
                     return;
                 }

                 firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {
                         if (caretaker.isChecked()) {
                             //verify role
                             DatabaseReference myRef  = FirebaseDatabase.getInstance().getReference("Caretaker");
                             myRef.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                     boolean check = false;
                                     for(DataSnapshot i : snapshot.getChildren()){
                                         Gson gson = new Gson();
                                         Caretaker caretaker = gson.fromJson(i.getValue().toString(), Caretaker.class);
                                         if(caretaker.getUID().equals(firebaseAuth.getCurrentUser().getUid())){
                                             check = true;
                                         }
                                     }
                                     if(check){
                                         startActivity(new Intent(getApplicationContext(), CareTakerActivity.class));
                                         finish();
                                     }
                                     else{
                                         Toast.makeText(LoginActivity.this, "This account isn't caretaker.", Toast.LENGTH_SHORT).show();
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                 }
                             });


                         }

                         if (patient.isChecked()) {
                             // verify role
                             DatabaseReference myRef  = FirebaseDatabase.getInstance().getReference("Patient");
                             myRef.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                     boolean check = false;
                                     for(DataSnapshot i : snapshot.getChildren()){
                                         Gson gson = new Gson();
                                         Patient patient= gson.fromJson(i.getValue().toString(), Patient.class);
                                         if(patient.getUid().equals(firebaseAuth.getCurrentUser().getUid())){
                                             check = true;

                                         }
                                     }
                                     System.out.println(check);
                                     if(check){
                                         startActivity(new Intent(getApplicationContext(), PatientActivity.class));
                                         finish();
                                     }
                                     else{
                                         Toast.makeText(LoginActivity.this ,"This account isn't patient.", Toast.LENGTH_SHORT).show();
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                 }
                             });


                         }


                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull @NotNull Exception e) {

                     }
                 });

             }
         });
    }
    @Override
    public void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("Patient");
            DatabaseReference caretakerRef = FirebaseDatabase.getInstance().getReference("Caretaker");
            patientRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot i : snapshot.getChildren()){
                        Gson gson = new Gson();
                        Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                        if(currentUID.equals(patient.getUid())){
                            startActivity(new Intent(getApplicationContext(), PatientActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
            caretakerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot i : snapshot.getChildren()){
                        Gson gson = new Gson();
                        Caretaker caretaker = gson.fromJson(i.getValue().toString(), Caretaker.class);
                        if(currentUID.equals(caretaker.getUID())){
                            startActivity(new Intent(getApplicationContext(), CareTakerActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }



}