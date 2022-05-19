package com.example.med_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText newPassword, confirmNewPassword;
    Button changePasswordBtn;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        user = FirebaseAuth.getInstance().getCurrentUser();

        newPassword = findViewById(R.id.newpasswordEditText);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordEditText);

        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("xxx",newPassword.getText().toString());
                if(newPassword.getText().toString().isEmpty()){
                    newPassword.setError("Required Field");
                    return;
                }

                if(confirmNewPassword.getText().toString().isEmpty()){
                    confirmNewPassword.setError("Please confirm your password");
                    return;
                }
                if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                    confirmNewPassword.setError("password not match");
                    return;
                }

                user.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResetPasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), CareTakerActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}