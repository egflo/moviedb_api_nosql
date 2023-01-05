package com.moviedb_api.service;


import com.moviedb_api.DTO.BookmarkRequest;
import com.moviedb_api.DTO.Response;
import com.moviedb_api.models.Bookmark;
import com.moviedb_api.models.Movie;
import com.moviedb_api.models.Review;
import com.moviedb_api.repository.BookmarkRepository;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository repository;

    @Autowired
    private MovieRepository movieRepository;

    public Page<Bookmark> findAllByUserId(String id, Pageable pageable) {
        return repository.getBookmarkByUserId(id, pageable);
    }

    public Page<Bookmark> findAll(Pageable pageable) {
        Page<Bookmark> result = repository.findAll(pageable);
        return result;
    }



    public Page<Bookmark> findByMovieId(String id, Pageable pageable) {
        return repository.findBookmarkByMovie_Id(id, pageable);
    }

    public Page<Bookmark> findByUserId(String id, Pageable pageable) {
        return repository.getBookmarkByUserId(id, pageable);
    }

    public Page<Bookmark> findByCreatedAfter(String date, Pageable pageable) {
        return repository.findBookmarkByCreatedAfter(date, pageable);
    }

    public Bookmark save(Bookmark bookmark) {
        return repository.save(bookmark);
    }

    public void deleteById(String id) {
        repository.deleteById(new ObjectId(id));
    }

    public void deleteByMovieId(String id) {
        repository.deleteBookmarkByMovie_Id(id);
    }

    public void deleteByUserId(String id) {
        repository.deleteBookmarkByUserId(id);
    }

    public Response getByMovieIdAndUserId(String movie_id, String user_id) {
        Response response = new Response();
        Optional<Bookmark> bookmark = repository.getBookmarkByMovie_IdAndUserId(new ObjectId(movie_id), user_id);
        if (bookmark.isPresent()) {
            response.setData(bookmark.get());
            response.setMessage("Bookmark found");
            response.setSuccess(true);
            response.setStatus(HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("Bookmark not found");
            response.setSuccess(false);
        }
        return response;
    }

    public Response get(String id, String userId) {
        Optional<Bookmark> result = repository.getBookmarkByMovie_IdAndUserId(new ObjectId(id), userId);

        Response response = new Response();
        if (result.isPresent()) {
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setMessage("Bookmark found");
            response.setData(result.get());
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setSuccess(false);
            response.setMessage("Bookmark not found");
        }
        return response;
    }

    public Response delete(String id) {
        Response response = new Response();
        try {
            repository.deleteById(new ObjectId(id));
            response.setMessage("Bookmark deleted successfully");
            response.setSuccess(true);
            response.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Bookmark not found");
            response.setSuccess(false);
            response.setStatus(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    public Response create(BookmarkRequest request) {
        Response response = new Response();
        try {
            Bookmark bookmark = new Bookmark();

            System.out.println(request.getMovieId());
            System.out.println(request.getUserId());
            System.out.println(request.getCreated());


            Optional<Movie> movie = movieRepository.getMovieById(new ObjectId(request.getMovieId()));
            if (movie.isPresent()) {
                bookmark.setMovie(movie.get());
                bookmark.setUserId(request.getUserId());
                bookmark.setCreated(request.getCreated());
                response.setMessage("Bookmark created successfully");
                response.setStatus(HttpStatus.OK);
                response.setData(repository.save(bookmark));
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setMessage("Movie not found");
                response.setStatus(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setMessage("Bookmark not added");
            response.setStatus(HttpStatus.NOT_FOUND);
            System.out.println(e);
        }
        return response;
    }

}
