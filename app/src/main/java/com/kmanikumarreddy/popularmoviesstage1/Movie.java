package com.kmanikumarreddy.popularmoviesstage1;

import java.io.Serializable;

/**
 * Created by ManiKumarReddy on 11-08-2016.
 */
public class Movie implements Serializable {
    public enum Genre { COMEDY, HORROR, DRAMA, ROMANCE, FICTION, ACTION, DOCUMENTARY, ADVANTURE, BIOGRAPHY, OTHER; }  

    private String backdropPath;
    private int id;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private double popularity;
    private String title;
    private  int voteAverage;
    private int voteCount;
    private Genre genre;

    public  getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPoster_path(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
