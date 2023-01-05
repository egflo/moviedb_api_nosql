package com.moviedb_api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.moviedb_api.DTO.SentimentRequest;
import com.moviedb_api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieService service;


    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestHeader HttpHeaders headers,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction
    ) {


        if(headers.get("Authorization") != null) {
            String token = headers.get("authorization").get(0).split(" ")[1].trim();
            System.out.println("token: " + token);
        }


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
            @PathVariable String title,
            @RequestParam Optional<String> genres,
            @RequestParam Optional<String> tags
    ) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        HashMap<String,String[]> filters = new HashMap<>();
        if (genres.isPresent()) {
            filters.put("genres", genres.get().split("_"));
        }
        if (tags.isPresent()) {
            filters.put("tags", tags.get().split("_"));
        }

        System.out.println("title: " + title);
        System.out.println("filters: " + filters);

        if (genres.isPresent() || tags.isPresent()) {
            return new ResponseEntity<>(service.findMoviesByCriteria(
                    title,
                    filters,
                    PageRequest.of(
                            page.orElse(0),
                            limit.orElse(10),
                            Sort.by(sortDirection, sortBy.orElse("year"))
                    )
            ), HttpStatus.OK);
        }

        return new ResponseEntity<>(service.findByTitle(title, PageRequest.of(
                page.orElse(0),
                limit.orElse(25),
                Sort.by(sortDirection, sortBy.orElse("id"))

        )), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @RequestHeader HttpHeaders headers,
            @PathVariable String id
    ) {
        System.out.println(headers);

        if(headers.get("Authorization") != null) {
            String token = headers.get("authorization").get(0).split(" ")[1].trim();
            System.out.println("token: " + token);

            DecodedJWT jwt = JWT.decode(token);
            String subject = jwt.getSubject();
            System.out.println("subject: " + subject);


            return new ResponseEntity<>(service.findByMovieIdWithUser(id, subject), HttpStatus.OK);

        }

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
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(service.getMovieSuggestions(id, PageRequest.of(
                page.orElse(0),
                limit.orElse(25),
                Sort.by(Sort.Direction.DESC, sortBy.orElse("id"))
        )), HttpStatus.OK);
    }


    @GetMapping("/tag/{id}")
    public ResponseEntity<?> findByTag(
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> direction,
            @PathVariable Integer id
    ) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction.isPresent()) {
            if (direction.get() == 1) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        return new ResponseEntity<>(service.getMoviesByTagId(id, PageRequest.of(
                page.orElse(0),
                limit.orElse(25),
                Sort.by(sortDirection, sortBy.orElse("id"))

        )), HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<?> like(
            @RequestHeader("Authorization") String token,
            @RequestBody SentimentRequest request
    ) {
        System.out.println("token: " + token);
        System.out.println("request: " + request);

        DecodedJWT jwt = JWT.decode(token.split(" ")[1].trim());
        String subject = jwt.getSubject();
        request.setUserId(subject);
        return new ResponseEntity<>(service.rate(request), HttpStatus.OK);
    }



}
