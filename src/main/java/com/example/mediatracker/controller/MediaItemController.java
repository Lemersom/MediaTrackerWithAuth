package com.example.mediatracker.controller;

import com.example.mediatracker.dto.MediaItemRecordDto;
import com.example.mediatracker.enums.MediaStatus;
import com.example.mediatracker.model.MediaItemModel;
import com.example.mediatracker.service.MediaItemService;
import com.example.mediatracker.specification.MediaItemSpecification;
import jakarta.validation.Valid;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/media-item")
public class MediaItemController {

    private final MediaItemService mediaItemService;

    public MediaItemController(MediaItemService mediaItemService) {
        this.mediaItemService = mediaItemService;
    }

    @GetMapping
    public ResponseEntity<List<MediaItemModel>> findAll(@RequestParam(required = false) String title,
                                                        @RequestParam(required = false) Integer rating,
                                                        @RequestParam(required = false) MediaStatus status,
                                                        @RequestParam(required = false) Long mediaTypeId,
                                                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(name = "size", defaultValue = "20") Integer size) {

        Specification<MediaItemModel> spec = Specification.where(null);

        if(title != null && !title.isEmpty()) {
            spec = spec.and(MediaItemSpecification.hasTitleContaining(title));
        }
        if(rating != null) {
            spec = spec.and(MediaItemSpecification.hasRating(rating));
        }
        if(status != null) {
            spec = spec.and(MediaItemSpecification.hasStatus(status));
        }
        if(mediaTypeId != null) {
            spec = spec.and(MediaItemSpecification.hasMediaTypeId(mediaTypeId));
        }

        List<MediaItemModel> mediaItemListPage = mediaItemService.findAllMediaItem(spec, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(mediaItemListPage);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestedId) {
        Optional<MediaItemModel> mediaItem = mediaItemService.findMediaItemById(requestedId);
        if(mediaItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mediaItem.get());
    }

    @PostMapping
    public ResponseEntity<Void> saveMediaItem(@RequestBody MediaItemRecordDto mediaItemRecordDto,
                                         UriComponentsBuilder ucb) {
        MediaItemModel savedMediaItem = mediaItemService.saveMediaItem(mediaItemRecordDto);

        URI locationOfNewMediaItem = ucb
                .path("media-item/{id}")
                .buildAndExpand(savedMediaItem.getId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(locationOfNewMediaItem).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateMediaItem(@PathVariable Long requestedId,
                                           @RequestBody @Valid MediaItemRecordDto mediaItemRecordDto) {
        if(mediaItemService.updateMediaItem(requestedId, mediaItemRecordDto)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteMediaItem(@PathVariable Long requestedId) {
        if(mediaItemService.deleteMediaItem(requestedId)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
