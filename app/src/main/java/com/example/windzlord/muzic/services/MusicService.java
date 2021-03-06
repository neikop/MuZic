package com.example.windzlord.muzic.services;

import okhttp3.RequestBody;
import com.example.windzlord.muzic.models.api_models.AddToPlaylistResult;
import com.example.windzlord.muzic.models.api_models.LoginResult;
import com.example.windzlord.muzic.models.api_models.MediaFeed;
import com.example.windzlord.muzic.models.api_models.PlaylistResult;
import com.example.windzlord.muzic.models.api_models.Result;
import com.example.windzlord.muzic.models.api_models.SearchResult;
import com.example.windzlord.muzic.models.api_models.SongMp3;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by WindzLord on 11/29/2016.
 */

public interface MusicService {

    @GET("/us/rss/topSongs/limit=50/genre={id}/explicit=true/json")
    Call<MediaFeed> getMediaFeed(@Path("id") String id);

    @GET("/services/api/audio")
    Call<SongMp3> getSongMp3(@Query("search_terms") String search);

    @POST("/api/user/login")
    Call<LoginResult> getLoginResult(@Body RequestBody account);

    @GET("/search/tracks")
    Call<SearchResult> getSearchResult(@Query("client_id") String clientId, @Query("limit") int limit, @Query("q") String query);

    @POST("/api/user/register")
    Call<Result> getRegisterResult(@Body RequestBody account);

    @POST("/api/song/addToPlaylist")
    Call<AddToPlaylistResult> addToPlaylist(@Body RequestBody account);

    @PUT("/api/song/removeFromPlaylist")
    Call<Result> removeFromPlaylist(@Body RequestBody account);

    @POST("/api/playlist/syncPlaylist")
    Call<PlaylistResult> syncPlaylist(@Body RequestBody account);

    @PUT("/api/playlist/remove")
    Call<Result> removePlaylist(@Body RequestBody account);

    @POST("/api/playlist/rename")
    Call<Result> renamePlaylist(@Body RequestBody account);

    @POST("/api/playlist/getPlaylistByUser")
    Call<PlaylistResult> getPlaylistByUser(@Body RequestBody token);

}
