package com.moviedb_api.service;

import com.moviedb_api.models.Sentiment;
import com.moviedb_api.repository.SentimentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class SentimentService {

    @Autowired
    private SentimentRepository repository;

    public Page<Sentiment> findAll(Pageable pageable) {
        Page<Sentiment> result = repository.findAll(pageable);
        return result;
    }

    public Page<Sentiment> findByObjectId(String id, Pageable pageable) {
        return repository.getSentimentByObjectId(new ObjectId(id), pageable);
    }

    public Page<Sentiment> findByUserId(String id, Pageable pageable) {
        return repository.getSentimentByUserId(id, pageable);
    }

    public Page<Sentiment> findByCreatedAfter(String date, Pageable pageable) {
        return repository.getSentimentByCreatedAfter(new Date(date), pageable);
    }


    public Optional<Sentiment> findByUserIdAndObjectId(String userId, String objectId) {
        return repository.getSentimentByUserIdAndObjectId(userId, new ObjectId(objectId));
    }

    public Sentiment save(Sentiment sentiment) {
        return repository.save(sentiment);
    }

    public void delete(String id) {
        repository.deleteById(new ObjectId(id));
    }

    public Sentiment get(String id) {
        return repository.findById(new ObjectId(id)).get();
    }
}
