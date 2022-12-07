package com.moviedb_api.service;


import com.moviedb_api.models.Movie;
import com.moviedb_api.models.Suggestion;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.SuggestionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    @Autowired
    private SuggestionRepository suggestionRepository;


    public Page<Movie> findAll(Pageable pageable) {
        Page<Movie> movies = repository.findAll(pageable);
        return movies;
    }

    public Page<Movie> findByTitle(String title, Pageable pageable) {
        return repository.getMovieByTitleContainingIgnoreCase(title, pageable);
    }

    public Optional<Movie> findById(String id) {
        return repository.findById(new ObjectId(id));
    }

    public Optional<Movie> findByMovieId(String id) {
        return repository.getMovieByMovieId(id);
    }

    public Page<Movie> findMovieByCastId(String castId, Pageable pageable) {
        return repository.findMovieByCastId(castId, pageable);
    }

    public List<Movie> findMoviesByTitle(String title) {
        return repository.findMovieByTitleContainingIgnoreCase(title);
    }

    public Page<Movie> recommendMovies(String movieId, Pageable pageable) {
        Optional<Movie> movie = repository.getMovieByMovieId(movieId);
        if (movie.isPresent()) {
            List<String> genres = movie.get().getGenres();
            return repository.findMovieByGenresContains(genres.get(0), pageable);
        }
        return null;
    }


    public Page<Movie> getMovieSuggestions(String movieId) {
        List<Suggestion> sugs = suggestionRepository.findSuggestionByMovieId(movieId);

        List<Movie> movies = new ArrayList<>();

        for(Suggestion sug : sugs) {

            Optional<Movie> movie = repository.getMovieByMovieId(sug.getSugMovieId());
            if (movie.isPresent()) {
                movies.add(movie.get());
            }
        }

        Page page = new PageImpl(movies);


        return page;
    }
 }

