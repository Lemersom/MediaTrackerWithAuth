package com.example.mediatracker.controller;

import com.example.mediatracker.dto.MediaTypeRecordDto;
import com.example.mediatracker.model.MediaTypeModel;
import com.example.mediatracker.service.MediaTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/media-type")
public class MediaTypeController {

    private final MediaTypeService mediaTypeService;

    public MediaTypeController(MediaTypeService mediaTypeService) {
        this.mediaTypeService = mediaTypeService;
    }

    @GetMapping
    public ResponseEntity<List<MediaTypeModel>> findAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(name = "size", defaultValue = "20") Integer size) {
        List<MediaTypeModel> mediaTypeListPage = mediaTypeService.findAllMediaType(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(mediaTypeListPage);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestedId) {
        Optional<MediaTypeModel> mediaType = mediaTypeService.findMediaTypeById(requestedId);
        if(mediaType.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mediaType.get());
    }

    @PostMapping
    public ResponseEntity<Void> saveMediaType(@RequestBody MediaTypeRecordDto mediaTypeRecordDto,
                                              UriComponentsBuilder ucb) {
        MediaTypeModel savedMediaType = mediaTypeService.saveMediaType(mediaTypeRecordDto);

        URI locationOfNewMediaType = ucb
                .path("media-type/{id}")
                .buildAndExpand(savedMediaType.getId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(locationOfNewMediaType).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateMediaType(@PathVariable Long requestedId,
                                                @RequestBody @Valid MediaTypeRecordDto mediaTypeRecordDto) {
        if(mediaTypeService.updateMediaType(requestedId, mediaTypeRecordDto)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteMediaType(@PathVariable Long requestedId) {
        if(mediaTypeService.deleteMediaType(requestedId)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
