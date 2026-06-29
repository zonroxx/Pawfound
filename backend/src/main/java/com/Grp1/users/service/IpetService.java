package com.Grp1.users.service;

import com.Grp1.users.models.Pet;

import java.util.List;

public interface IpetService {
    List<Pet> getPets();
    Pet getPet(int id);
    Pet addPet(Pet pet);
    Pet updatePet(int id, Pet pet);
    void deletePet(int id);
}
