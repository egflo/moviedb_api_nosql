package com.moviedb_api.models;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "suggestions")
public class Tag {

    @Field("tag_id")
    private Integer tagId;

    private String name;



    public Tag() {
    }

    public Tag(Integer id, String name) {
        this.tagId = id;
        this.name = name;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer id) {
        this.tagId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
