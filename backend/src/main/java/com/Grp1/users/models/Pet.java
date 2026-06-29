package com.Grp1.users.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String breed;
    private int age;
    private String sex;
    private String size;
    private String color;
    private Double weight;

    private String photoUrl;

    private String healthStatus;
    private String vaccinationStatus;
    private Boolean dewormed;
    private Boolean neuteredSpayed;
    private String medicalNotes;

    private String temperament;
    private Boolean houseTrained;

    private Double adoptionFee;
    private String location;
    private String availabilityStatus;
    private LocalDate intakeDate;

    private String description;
    private String trainingLevel;
    private String contactInfo;

    public Pet(){

    }

    public Pet(int id, String name, String breed, int age) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getBreed() {return breed;}
    public void setBreed(String breed) {this.breed = breed;}

    public int getAge() {return age;}
    public void setAge(int age) {this.age = age;}

    public String getSex() {return sex;}
    public void setSex(String sex) {this.sex = sex;}

    public String getSize() {return size;}
    public void setSize(String size) {this.size = size;}

    public String getColor() {return color;}

    public void setColor(String color) {this.color = color;}

    public Double getWeight() {return weight;}
    public void setWeight(Double weight) {this.weight = weight;}

    public String getPhotoUrl() {return photoUrl;}
    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getHealthStatus() {return healthStatus;}
    public void setHealthStatus(String healthStatus) {this.healthStatus = healthStatus;}

    public String getVaccinationStatus() {return vaccinationStatus;}
    public void setVaccinationStatus(String vaccinationStatus) {this.vaccinationStatus = vaccinationStatus;}

    public Boolean getDewormed() {return dewormed;}
    public void setDewormed(Boolean dewormed) {this.dewormed = dewormed;}

    public Boolean getNeuteredSpayed() {return neuteredSpayed;}
    public void setNeuteredSpayed(Boolean neuteredSpayed) {this.neuteredSpayed = neuteredSpayed;}

    public String getMedicalNotes() {return medicalNotes;}
    public void setMedicalNotes(String medicalNotes) {this.medicalNotes = medicalNotes;}

    public String getTemperament() {return temperament;}
    public void setTemperament(String temperament) {this.temperament = temperament;}

    public Boolean getHouseTrained() {return houseTrained;}
    public void setHouseTrained(Boolean houseTrained) {this.houseTrained = houseTrained;}

    public Double getAdoptionFee() {return adoptionFee;}
    public void setAdoptionFee(Double adoptionFee) {this.adoptionFee = adoptionFee;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getAvailabilityStatus() {return availabilityStatus;}
    public void setAvailabilityStatus(String availabilityStatus) {this.availabilityStatus = availabilityStatus;}

    public LocalDate getIntakeDate() {return intakeDate;}
    public void setIntakeDate(LocalDate intakeDate) {this.intakeDate = intakeDate;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getTrainingLevel() {return trainingLevel;}
    public void setTrainingLevel(String trainingLevel) {this.trainingLevel = trainingLevel;}

    public String getContactInfo() {return contactInfo;}
    public void setContactInfo(String contactInfo) {this.contactInfo = contactInfo;}
}
