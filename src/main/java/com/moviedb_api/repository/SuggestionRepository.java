package com.moviedb_api.repository;


import com.moviedb_api.models.CastDetail;
import com.moviedb_api.models.Suggestion;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("SuggestionsRepository")

public interface SuggestionRepository extends MongoRepository<Suggestion, ObjectId> {

    List<Suggestion> findSuggestionByMovieId(String movieId);
}
