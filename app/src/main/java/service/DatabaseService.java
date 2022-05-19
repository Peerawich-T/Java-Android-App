package service;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import model.Caretaker;
import model.Patient;

public class DatabaseService {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;

    public void  addCareTakerToDB(Caretaker caretaker){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Caretaker");

        myRef.child(caretaker.getUID()).setValue(caretaker);
    }

    public void updateCareTaker(Caretaker caretaker , String UID){
        HashMap caretakerMap = new HashMap();
        caretakerMap.put("uid", caretaker.getUID());
        caretakerMap.put("patients", caretaker.getPatients());
        myRef = database.getReference("Caretaker");
        myRef.child(UID).updateChildren(caretakerMap);
    }


    public void  addPatientToDB(Patient patient){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Patient");

        myRef.child(patient.getUid()).setValue(patient);
    }

    public void updatePatient(Patient patient, String uid){
        HashMap patientMap = new HashMap();
        patientMap.put("uid", patient.getUid());
        patientMap.put("medicines", patient.getMedicines());
        myRef = database.getReference("Patient");
        myRef.child(uid).updateChildren(patientMap);
    }






}
