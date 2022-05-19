package com.example.med_app.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.example.med_app.R;
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
import java.util.Date;
import java.util.List;

import model.Patient;

public class WidgetDataProvider  implements RemoteViewsService.RemoteViewsFactory{
    List<WidgetItem> medList = new ArrayList<>();
    Context context;
    Intent intent;

    private FirebaseAuth auth;

    private DatabaseReference patientRef;



    private String userId;

    private FirebaseUser user;


    private void initializeData() throws NullPointerException {

        try {
            medList.clear();

            auth = FirebaseAuth.getInstance();

            user = auth.getCurrentUser();
            assert user != null;
            Date date  = Calendar.getInstance().getTime();
            String day = date.toString().split(" ")[0];
            userId = auth.getCurrentUser().getUid();
            patientRef = FirebaseDatabase.getInstance().getReference("Patient");
            patientRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    boolean notDup = true;
                    for(DataSnapshot i : snapshot.getChildren()){
                        Gson gson = new Gson();
                        Patient patient = gson.fromJson(i.getValue().toString(), Patient.class);
                        if(patient.getUid().equals(userId)){
                            patient.getMedicines().remove("antiNull");

                            for(String j :patient.getMedicineBydDay(day)){
                                String showData = j.split("_")[0]+" : "+j.split("_")[4].replace("t",",");
                                for(WidgetItem k : medList ){
                                    if(showData.equals(k.text)){
                                        notDup = false ;
                                    }
                                }
                                if(notDup) {
                                    medList.add(new WidgetItem(showData));
                                }


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        initializeData();

    }

    @Override
    public void onDataSetChanged() {
        initializeData();


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("TAG", "Total count is " + medList.size());
        return medList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_item, medList.get(i).text);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
