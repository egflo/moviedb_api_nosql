package com.moviedb_api.models;

import java.util.List;

public class Cast {

    String castId;
    String name;

    String category;

    List<String> characters;

    String photo;


    public Cast() {
    }

    public Cast(String castId, String name, String category, List<String> characters, String photo) {
        this.castId = castId;
        this.name = name;
        this.category = category;
        this.characters = characters;
        this.photo = photo;
    }

    public String getCastId() {
        return castId;
    }

    public void setCastId(String castId) {
        this.castId = castId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
