package model;

import java.util.ArrayList;

public class Caretaker {
    private String name;
    private String uid;
    private ArrayList<String> patients;

    public Caretaker(String UID, String name){
        this.name = name;
        this.uid = UID;
        patients = new ArrayList<>();
        patients.add("AntiNull");
    }

    public Caretaker(String name, String uid, ArrayList<String> patients) {
        this.name = name;
        this.uid = uid;
        this.patients = patients;
    }

    public String getName() {
        return name;
    }

    public void addPatient(String patientUID){
       if(!hasThisPatient(patientUID)) {
             patients.add(patientUID);
       }
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String UID) {
        this.uid= UID;
    }

    public ArrayList<String> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<String> patients) {
        this.patients = patients;
    }

    public boolean hasThisPatient(String uid){
        for(String i :patients){
            if(i.equals(uid)){
                return  true;
            }
        }
        return  false;
    }

}
