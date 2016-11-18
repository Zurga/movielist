package com.jim;

public class MovieItem{
    private String title;
    private String year;
    private String director;
    private String actors;
    private String description;
    private String poster;

    public MovieItem(String title, String year, String director, String actors,
            String description, String poster) {
        this.title = title;
        this.year = title;
        this.director = title;
        this.actors = title;
        this.description = title;
        this.poster = title;
    }

    //Getters
    public String getTitle() {
        return this.title;
    }

    public String getYear() {
        return this.year;
    }

    public String getDirector() {
        return this.director;
    }

    public String getActors() {
        return this.actors;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPoster() {
        return this.poster;
    }

    // Setters
    public void setTitle(String value) {
        this.title = value;
    }

    public void setYear(String value) {
        this.year = value;
    }

    public void setDirector(String value) {
        this.director = value;
    }

    public void setActors(String value) {
        this.actors = value;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setPoster(String value) {
        this.poster = value;
    }
}
