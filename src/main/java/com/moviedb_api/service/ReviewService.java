package com.moviedb_api.service;


import com.moviedb_api.DTO.Response;
import com.moviedb_api.DTO.SentimentRequest;
import com.moviedb_api.models.Review;
import com.moviedb_api.models.Sentiment;
import com.moviedb_api.repository.ReviewRepository;
import com.moviedb_api.repository.SentimentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private SentimentService service;



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



    public Response rateReview(SentimentRequest request) {

        Response response = new Response();
        System.out.println(request);

        Optional<Sentiment> sentiment = service.findByUserIdAndObjectId(request.getUserId(), request.getObjectId());
        if (sentiment.isPresent()) {
            Sentiment sentiment1 = sentiment.get();
            sentiment1.setStatus(request.getStatus());
            service.save(sentiment1);

            response.setMessage("Updated existing sentiment");
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            return response;
        }

        Sentiment sentiment1 = new Sentiment();
        sentiment1.setObjectId(new ObjectId(request.getObjectId()));
        sentiment1.setUserId(request.getUserId());
        sentiment1.setStatus(request.getStatus());

        if (request.getCreated() != null) {
            sentiment1.setCreated(new Date(request.getCreated()));
        }
        else {
            sentiment1.setCreated(new Date());
        }
        service.save(sentiment1);

        response.setMessage("Created new sentiment");
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK);
        return response;
    }

}
