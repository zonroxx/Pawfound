package com.Grp1.users.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.Grp1.users.models.Pet;
import com.Grp1.users.repository.petRepository;

import java.util.List;

@Service
public class petService implements IpetService {

    @Autowired
    private petRepository repository;

    @Override
    public List<Pet> getPets() {
        return (List<Pet>) repository.findAll();
    }

    @Override
    public Pet getPet(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Pet addPet(Pet pet) {
        return repository.save(pet);
    }

    @Override
    public Pet updatePet(int id, Pet updatedPet) {
        return repository.findById(id)
                .map(existingPet -> {
                    existingPet.setName(updatedPet.getName());
                    existingPet.setBreed(updatedPet.getBreed());
                    existingPet.setAge(updatedPet.getAge());
                    existingPet.setSex(updatedPet.getSex());
                    existingPet.setSize(updatedPet.getSize());
                    existingPet.setColor(updatedPet.getColor());
                    existingPet.setWeight(updatedPet.getWeight());
                    existingPet.setPhotoUrl(updatedPet.getPhotoUrl());
                    existingPet.setHealthStatus(updatedPet.getHealthStatus());
                    existingPet.setVaccinationStatus(updatedPet.getVaccinationStatus());
                    existingPet.setDewormed(updatedPet.getDewormed());
                    existingPet.setNeuteredSpayed(updatedPet.getNeuteredSpayed());
                    existingPet.setMedicalNotes(updatedPet.getMedicalNotes());
                    existingPet.setTemperament(updatedPet.getTemperament());
                    existingPet.setHouseTrained(updatedPet.getHouseTrained());
                    existingPet.setAdoptionFee(updatedPet.getAdoptionFee());
                    existingPet.setLocation(updatedPet.getLocation());
                    existingPet.setAvailabilityStatus(updatedPet.getAvailabilityStatus());
                    existingPet.setIntakeDate(updatedPet.getIntakeDate());
                    existingPet.setDescription(updatedPet.getDescription());
                    existingPet.setTrainingLevel(updatedPet.getTrainingLevel());
                    existingPet.setContactInfo(updatedPet.getContactInfo());
                    return repository.save(existingPet);
                })
                .orElse(null);
    }

    @Override
    public void deletePet(int id) {
        repository.findById(id).ifPresent(repository::delete);
    }
}

