package com.example.mediatracker.service;

import com.example.mediatracker.controller.MediaItemController;
import com.example.mediatracker.dto.MediaItemRecordDto;
import com.example.mediatracker.exception.ResourceNotFoundException;
import com.example.mediatracker.model.MediaItemModel;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MediaItemService {

    private final MediaItemRepository mediaItemRepository;
    private final MediaTypeRepository mediaTypeRepository;
    private final Validator validator;

    public MediaItemService(MediaItemRepository mediaItemRepository, MediaTypeRepository mediaTypeRepository, Validator validator) {
        this.mediaItemRepository = mediaItemRepository;
        this.mediaTypeRepository = mediaTypeRepository;
        this.validator = validator;
    }

    public List<MediaItemModel> findAllMediaItem(Specification<MediaItemModel> spec, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaItemModel> mediaItemsListPage = mediaItemRepository.findAll(spec, pageable);
        if(!mediaItemsListPage.isEmpty()) {
            for(MediaItemModel mediaItem : mediaItemsListPage) {
                Long id = mediaItem.getId();
                mediaItem.add(linkTo(methodOn(MediaItemController.class).findById(id)).withSelfRel());
            }
        }

        return mediaItemsListPage.getContent();
    }

    public Optional<MediaItemModel> findMediaItemById(Long requestedId) {
        Optional<MediaItemModel> mediaItem = mediaItemRepository.findById(requestedId);
        if(mediaItem.isEmpty()) {
            return Optional.empty();
        }
        mediaItem.get().add(linkTo(methodOn(MediaItemController.class).findAll(null, null, null, null, 0, 20)).withRel("MediaItemsList"));

        return mediaItem;
    }

    public MediaItemModel saveMediaItem(MediaItemRecordDto mediaItemRecordDto) {
        Set<ConstraintViolation<MediaItemRecordDto>> violations = validator.validate(mediaItemRecordDto);

        if(!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(ConstraintViolation<MediaItemRecordDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage()).append("; ");
            }
            sb.setLength(sb.length() - 2);
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violations);
        }

        MediaTypeModel mediaType = mediaTypeRepository.findById(mediaItemRecordDto.mediaTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid media type ID"));

        MediaItemModel mediaItemToSave = new MediaItemModel();
        BeanUtils.copyProperties(mediaItemRecordDto, mediaItemToSave);

        mediaItemToSave.setMediaType(mediaType);

        return mediaItemRepository.save(mediaItemToSave);
    }

    public boolean updateMediaItem(Long requestedId, MediaItemRecordDto mediaItemRecordDto) {
        Optional<MediaItemModel> mediaItemToUpdate = mediaItemRepository.findById(requestedId);
        if(mediaItemToUpdate.isEmpty()) {
            return false;
        }

        Optional<MediaTypeModel> mediaType = mediaTypeRepository.findById(mediaItemRecordDto.mediaTypeId());
        if (mediaType.isEmpty()) {
            throw new ResourceNotFoundException("Invalid media type id: " + mediaItemRecordDto.mediaTypeId());
        }

        BeanUtils.copyProperties(mediaItemRecordDto, mediaItemToUpdate.get());

        mediaItemToUpdate.get().setMediaType(mediaType.get());

        mediaItemRepository.save(mediaItemToUpdate.get());

        return true;
    }

    public boolean deleteMediaItem(Long requestedId) {
        Optional<MediaItemModel> mediaItemToDelete = mediaItemRepository.findById(requestedId);
        if(mediaItemToDelete.isEmpty()) {
            return false;
        }
        mediaItemRepository.delete(mediaItemToDelete.get());

        return true;
    }

}
