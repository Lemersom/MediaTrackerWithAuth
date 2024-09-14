package com.example.mediatracker.repository;

import com.example.mediatracker.enums.MediaStatus;
import com.example.mediatracker.model.MediaItemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItemModel, Long>, JpaSpecificationExecutor<MediaItemModel> {

    Page<MediaItemModel> findByMediaType_Id(Long mediaTypeId, Pageable pageable);

}
