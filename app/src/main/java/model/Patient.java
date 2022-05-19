package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Patient {
    private String  uid;
    private String name;
    private ArrayList<String> medicines;

    public String getName() {
        return name;
    }

    public Patient(String uid, ArrayList<String> medicines, String name) {
        this.uid = uid;
        this.medicines = medicines;
        this.name = name;
    }
    public Patient(String uid, String name) {
        this.name = name;
        this.uid = uid;
        this.medicines = new ArrayList<String>();
        medicines.add("antiNull");
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getMedicines() {
        return medicines;
    }

    public void setMedicines(ArrayList<String> medicines) {
        this.medicines = medicines;
    }


    public void addMedicine(Medicine medicine){
        String medicineData = medicine.getName()+"_"+medicine.getExpiredDate()+"_"+medicine.getRemainValue()+"_"
                                +medicine.getDosagePerOneTake()+"_"+medicine.getTime().replace(",","t")+"_";
        for(String i : medicine.getDay()){
            medicineData +=i;
            if(!i.equals(medicine.getDay().get(medicine.getDay().size()-1))){
                medicineData+="z";
            }

        }
        medicines.add(medicineData);

    }
    public ArrayList<String> getMedicineBydDay(String day){
        ArrayList<String> thisDayMedicine = new ArrayList<>();
        for(String j : medicines){
            if(!j.equals("antiNull")) {
                String[] dayOfMedicine = j.split("_")[5].split("z");
                for (String i : dayOfMedicine) {
                    if (day.equals(i)) {
                        thisDayMedicine.add(j);
                        break;
                    }
                }
            }
        }
        return  thisDayMedicine;
    }

    public boolean dupMed(Medicine medicine){
        for(String  i : medicines) {
            if (!i.equals("antiNull")) {
                if (medicine.getName().equals(i.split("_")[0])) {
                    return true;
                }
            }
        }
        return false ;
    }
    public boolean setMedZero(String medName){

        for(String i : medicines){
            if(!i.equals("antiNull")) {
                if (i.split("_")[0].equals(medName)) {
                    String backup = i.split("_")[0] + "_" + i.split("_")[1] + "_" + "0" + "_" + i.split("_")[3] + "_" + i.split("_")[4] + "_" + i.split("_")[5];
                    medicines.remove(i);
                    medicines.add(backup);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updateMed(String medName, String remainVal, String expiredDate){

        for(String i : medicines) {
            if (!i.equals("antiNull")) {
                if (i.split("_")[0].equals(medName)) {
                    String backup = i.split("_")[0] + "_" + expiredDate + "_" + remainVal + "_" + i.split("_")[3] + "_" + i.split("_")[4] + "_" + i.split("_")[5];
                    medicines.remove(i);
                    medicines.add(backup);
                    return true;
                }
            }
        }
        return false;
    }

    
}
