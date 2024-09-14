package com.example.mediatracker.repository;

import com.example.mediatracker.model.MediaTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaTypeRepository extends JpaRepository<MediaTypeModel, Long> {
}
