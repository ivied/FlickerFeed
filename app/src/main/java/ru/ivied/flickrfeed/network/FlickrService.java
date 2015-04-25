package ru.ivied.flickrfeed.network;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

public interface FlickrService {
    @GET("/services/feeds/photos_public.gne?format=json")
    void getFeed(Callback<Response> callback);
}