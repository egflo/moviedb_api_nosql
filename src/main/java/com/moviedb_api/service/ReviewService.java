package com.moviedb_api.service;


import com.moviedb_api.models.CastDetail;
import com.moviedb_api.models.Review;
import com.moviedb_api.repository.CastRepository;
import com.moviedb_api.repository.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;


    public Page<Review> findAll(Pageable pageable) {
        Page<Review> cast = repository.findAll(pageable);
        return cast;
    }

    public Page<Review> findByTitle(String name, Pageable pageable) {
        return repository.getReviewByTitleContaining(name, pageable);
    }

    public Optional<Review> findById(String id) {
        return repository.findById(new ObjectId(id));
    }

    public Page<Review> findByMovieId(String id, Pageable pageable) {
        return repository.findReviewByMovieId(id, pageable);
    }
}
