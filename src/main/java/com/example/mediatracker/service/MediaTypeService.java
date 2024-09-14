package com.example.mediatracker.service;

import com.example.mediatracker.controller.MediaTypeController;
import com.example.mediatracker.dto.MediaTypeRecordDto;
import com.example.mediatracker.exception.ResourceNotFoundException;
import com.example.mediatracker.model.MediaTypeModel;
import com.example.mediatracker.repository.MediaItemRepository;
import com.example.mediatracker.repository.MediaTypeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MediaTypeService {

    private final MediaTypeRepository mediaTypeRepository;
    private final MediaItemRepository mediaItemRepository;
    private final Validator validator;

    public MediaTypeService(MediaTypeRepository mediaTypeRepository, MediaItemRepository mediaItemRepository, Validator validator) {
        this.mediaTypeRepository = mediaTypeRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.validator = validator;
    }

    public List<MediaTypeModel> findAllMediaType(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaTypeModel> mediaTypeListPage = mediaTypeRepository.findAll(pageable);
        if(!mediaTypeListPage.isEmpty()) {
            for(MediaTypeModel mediaType : mediaTypeListPage) {
                Long id = mediaType.getId();
                mediaType.add(linkTo(methodOn(MediaTypeController.class).findById(id)).withSelfRel());
            }
        }

        return mediaTypeListPage.getContent();
    }

    public Optional<MediaTypeModel> findMediaTypeById(Long requestedId) {
        Optional<MediaTypeModel> mediaType = mediaTypeRepository.findById(requestedId);
        if(mediaType.isEmpty()) {
            return Optional.empty();
        }
        mediaType.get().add(linkTo(methodOn(MediaTypeController.class).findAll(0, 20)).withRel("MediaTypeList"));

        return mediaType;
    }

    public MediaTypeModel saveMediaType(MediaTypeRecordDto mediaTypeRecordDto) {
        Set<ConstraintViolation<MediaTypeRecordDto>> violations = validator.validate(mediaTypeRecordDto);

        if(!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(ConstraintViolation<MediaTypeRecordDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage()).append("; ");
            }
            sb.setLength(sb.length() - 2);
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violations);
        }

        MediaTypeModel mediaTypeToSave = new MediaTypeModel();
        BeanUtils.copyProperties(mediaTypeRecordDto, mediaTypeToSave);

        return mediaTypeRepository.save(mediaTypeToSave);
    }

    public boolean updateMediaType(Long requestedId, MediaTypeRecordDto mediaTypeRecordDto) {
        Optional<MediaTypeModel> mediaTypeToUpdate = mediaTypeRepository.findById(requestedId);
        if(mediaTypeToUpdate.isEmpty()) {
            return false;
        }
        BeanUtils.copyProperties(mediaTypeRecordDto, mediaTypeToUpdate.get());
        mediaTypeRepository.save(mediaTypeToUpdate.get());

        return true;
    }

    public boolean deleteMediaType(Long requestedId) {
        Pageable pageable = PageRequest.of(0, 1);
        if(!mediaItemRepository.findByMediaType_Id(requestedId, pageable).isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete media type with ID " + requestedId + " because it is associated with one or more media items.");
        }

        Optional<MediaTypeModel> mediaTypeToDelete = mediaTypeRepository.findById(requestedId);
        if(mediaTypeToDelete.isEmpty()) {
            return false;
        }
        mediaTypeRepository.delete(mediaTypeToDelete.get());

        return true;
    }

}
