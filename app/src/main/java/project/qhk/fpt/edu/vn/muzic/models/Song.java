package project.qhk.fpt.edu.vn.muzic.models;

import io.realm.RealmObject;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;

/**
 * Created by WindzLord on 12/1/2016.
 */

public class Song extends RealmObject {

    public static String GENRE_ID = "genreID";

    private String genreID;
    private String name;
    private String artist;
    private String imageLink;
    private String imagePicture;

    public static Song create(String genreID, MediaFeed.Feed.Entry entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        if (entry.getName().length() > 100) song.name = song.name.substring(0, 100);
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        return song;
    }

    public static Song create(String genreID, Song entry) {
        Song song = new Song();
        song.genreID = genreID;
        song.name = entry.getName();
        song.artist = entry.getArtist();
        song.imageLink = entry.getImageLink();
        return song;
    }

    public String getGenreID() {
        return genreID;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getImagePicture() {
        return imagePicture;
    }

    public void setImagePicture(String imagePicture) {
        this.imagePicture = imagePicture;
    }
}
