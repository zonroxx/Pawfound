package com.Grp1.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Grp1.users.models.Pet;

public interface petRepository extends JpaRepository<Pet, Integer> {
}
