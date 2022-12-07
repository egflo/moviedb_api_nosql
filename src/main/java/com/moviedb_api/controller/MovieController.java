package com.moviedb_api.controller;

import com.moviedb_api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieService service;


    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction
    ) {

        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }


        return new ResponseEntity<>(service.findAll(PageRequest.of(
                page.orElse(0),
                limit.orElse(10),
                Sort.by(sortDirection, sortBy.orElse("year"))
        )), HttpStatus.OK);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<?> search(
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction,
            @PathVariable String title
    ) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        return new ResponseEntity<>(service.findByTitle(title, PageRequest.of(
                page.orElse(0),
                limit.orElse(25),
                Sort.by(sortDirection, sortBy.orElse("id"))

        )), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(

            @PathVariable String id
    ) {
        return new ResponseEntity<>(service.findByMovieId(id), HttpStatus.OK);
    }

    @GetMapping("/cast/{castId}")
    public ResponseEntity<?> getByCastId(
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction,
            @PathVariable String castId
    ) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        return new ResponseEntity<>(service.findMovieByCastId(castId
                , PageRequest.of(
                        page.orElse(0),
                        limit.orElse(10),
                        Sort.by(sortDirection, sortBy.orElse("ratings.numVotes"))
                )), HttpStatus.OK);

    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<?> searchByTitle(
            @PathVariable String title
    ) {
        return new ResponseEntity<>(service.findMoviesByTitle(title), HttpStatus.OK);
    }

    @GetMapping("/recommend/{id}")
    public ResponseEntity<?> recommendation(
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction,
            @PathVariable String id
    ) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        return new ResponseEntity<>(service.recommendMovies(id, PageRequest.of(
                page.orElse(0),
                limit.orElse(25),
                Sort.by(sortDirection, sortBy.orElse("id"))

        )), HttpStatus.OK);
    }


    @GetMapping("suggest/{id}")
    public ResponseEntity<?> getSuggestionsByMovieId(

            @PathVariable String id
    ) {
        return new ResponseEntity<>(service.getMovieSuggestions(id), HttpStatus.OK);
    }


}
