package ru.ivied.flickrfeed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.ivied.flickrfeed.network.FlickrService;


public class FeedActivityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        /*OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                chain.
                return null;
            }
        })
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();*/
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://www.flickr.com")
                //.setClient(new OkClient(client))
                .build();
        FlickrService flickr = restAdapter.create(FlickrService.class);
        flickr.getFeed(new Callback<Response>() {
            @Override
            public void success(Response photoList, Response response) {
                Log.i("omg", "zomg");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("omg", "zomg");
            }
        });
    }
}
