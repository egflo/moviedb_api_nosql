package com.moviedb_api.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moviedb_api.DAO.MovieDAO;
import com.moviedb_api.DTO.Response;
import com.moviedb_api.DTO.SentimentRequest;
import com.moviedb_api.models.Bookmark;
import com.moviedb_api.models.Movie;
import com.moviedb_api.models.Sentiment;
import com.moviedb_api.models.Suggestion;
import com.moviedb_api.repository.BookmarkRepository;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.SuggestionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Service
public class MovieService {

    @Autowired
    MovieDAO movieDAO;

    @Autowired
    private MovieRepository repository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private SentimentService service;


    @Value("${background_api.key}")
    String API_KEY;

    public int background(String movie_id)  {
        int row_ratings, row_movie = 0;

        try {

            Movie movie = repository.getMovieByMovieId(movie_id).get();

            String movie_background = movie.getBackground();

            if (movie_background != null && movie_background.length() > 0) {
                //return 0;
            }

            // Create a neat value object to hold the URL
            URL url = new URL("https://webservice.fanart.tv/v3/movies/" + movie_id + "?api_key=" + API_KEY);

            System.out.println(url.toString());

            // Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Now it's "open", we can set the request method, headers etc.
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // This line makes the request
            InputStream responseStream = connection.getInputStream();

            // Manually converting the response body InputStream to String
            String content = IOUtils.toString(responseStream, "UTF-8");

            // Finally we have the response
            JsonObject json = new JsonParser().parse(content).getAsJsonObject();
            JsonArray data = json.get("moviebackground").getAsJsonArray();
            JsonArray posters = json.get("movieposter").getAsJsonArray();

            if(data.size() != 0) {
                JsonObject object = data.get(0).getAsJsonObject();
                JsonObject poster = posters.get(0).getAsJsonObject();

                JsonElement poster_url = poster.get("url");
                JsonElement background_url = object.get("url");


                System.out.println("URL " + background_url.getAsString());
                System.out.println("Poster " + poster_url.getAsString());

                movie.setBackground(background_url.getAsString());
                movie.setPoster(poster_url.getAsString());

                repository.save(movie);

            }

            else {
                return 0;
            }




            //connection.commit();

        }
        catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            //System.out.println(exceptionAsString);
            System.out.println("ERROR MOVIE ID " + movie_id);

        }

        return row_movie;

    }

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
        //background(id);

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


    public Page<Movie> getMovieSuggestions(String movieId, Pageable pageable) {
        Page<Suggestion> suggestions =
                suggestionRepository.findSuggestionByMovieId(movieId, pageable);

        List<Movie> movies = new ArrayList<>();

        for(Suggestion suggest : suggestions.getContent()) {

            Optional<Movie> movie = repository.getMovieByMovieId(suggest.getSugMovieId());
            if (movie.isPresent()) {
                movies.add(movie.get());
            }
        }

        return new PageImpl<>(movies, pageable, suggestions.getTotalElements());
    }

    public Page<Movie> getMoviesByTagId(Integer tagId, Pageable pageable) {
        return repository.getMovieByKeywordsByTagId(tagId, pageable);
    }

    public Page<Movie> findMoviesByCriteria(String title, HashMap<String, String[]> criteria, Pageable pageable) {
        return movieDAO.findMovieByParams(title, criteria, pageable);
    }

    public Optional<Movie> findByMovieIdWithUser(String id, String userId) {
        background(id);

        Optional<Movie> movie = repository.getMovieByMovieId(id);

        System.out.println("USER ID " + userId);
        Optional<Bookmark> bookmark = bookmarkRepository.getBookmarkByMovie_IdAndUserId(new ObjectId(movie.get().getId()), userId);
        if (bookmark.isPresent() && movie.isPresent()) {
            System.out.println("BOOKMARK " + bookmark.get());
            //Inject bookmark into movie
            //movie.get().setBookmark(bookmark.get());

            //return movie;
        }


        return repository.getMovieByMovieId(id);
    }

    public Response rate(SentimentRequest request) {

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

