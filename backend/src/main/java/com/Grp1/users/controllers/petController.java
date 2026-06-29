package com.Grp1.users.controllers;

import com.Grp1.users.models.Pet;
import com.Grp1.users.service.IpetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@CrossOrigin(origins = "*")
public class petController {

    private final IpetService petService;

    public petController(IpetService petService) {
        this.petService = petService;
    }

    @RequestMapping("/api/pets")
    public List<Pet> getAllPets() {
        return petService.getPets();
    }

    @RequestMapping("/api/pets/{id}")
    public Pet getPet(@PathVariable int id) {
        return petService.getPet(id);
    }

    @RequestMapping(value = "/api/add-pet", method = RequestMethod.POST)
    public Pet addPet(@RequestBody Pet pet) {
        return petService.addPet(pet);
    }

    @RequestMapping(value = "/api/update-pet/{id}", method = RequestMethod.PUT)
    public Pet updatePet(@PathVariable int id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @RequestMapping(value = "/api/delete-pet/{id}", method = RequestMethod.DELETE)
    public void deletePet(@PathVariable int id) {
        petService.deletePet(id);
    }
}
