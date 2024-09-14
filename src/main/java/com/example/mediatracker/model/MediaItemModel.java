package com.example.mediatracker.model;

import com.example.mediatracker.enums.MediaStatus;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Entity
@Table(name = "media_item")
public class MediaItemModel extends RepresentationModel<MediaItemModel> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MediaStatus status;

    @ManyToOne
    @JoinColumn(name = "media_type_id", nullable = false)
    private MediaTypeModel mediaType;

    @Column(name = "notes", length = 2000)
    private String notes;


    public MediaItemModel() {}

    public MediaItemModel(String title, Integer rating, LocalDate startDate, LocalDate finishDate, MediaStatus status, MediaTypeModel mediaType, String notes) {
        this.title = title;
        this.rating = rating;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
        this.mediaType = mediaType;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public MediaTypeModel getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaTypeModel mediaType) {
        this.mediaType = mediaType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
