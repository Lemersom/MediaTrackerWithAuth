package com.example.mediatracker.specification;

import com.example.mediatracker.enums.MediaStatus;
import com.example.mediatracker.model.MediaItemModel;
import org.springframework.data.jpa.domain.Specification;

public class MediaItemSpecification {

    public static Specification<MediaItemModel> hasTitleContaining(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<MediaItemModel> hasRating(Integer rating) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("rating"), rating);
    }

    public static Specification<MediaItemModel> hasStatus(MediaStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<MediaItemModel> hasMediaTypeId(Long mediaTypeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("mediaType").get("id"), mediaTypeId);
    }

}
