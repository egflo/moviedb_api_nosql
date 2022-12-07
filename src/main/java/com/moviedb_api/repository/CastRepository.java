package com.moviedb_api.repository;


import com.moviedb_api.models.CastDetail;
import com.moviedb_api.models.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("CastRepository")

public interface CastRepository extends MongoRepository<CastDetail, ObjectId> {

    Page<CastDetail> getCastDetailByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<CastDetail> findCastDetailByDobAfter(String date, Pageable pageable);
    Optional<CastDetail> findCastDetailByCastId(String id);

}
