package com.example.securebusiness.repository;

import com.example.securebusiness.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DbStorageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByName(String imageName);
}
