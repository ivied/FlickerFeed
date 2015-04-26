package ru.ivied.flickrfeed.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.mime.TypedByteArray;
import ru.ivied.flickrfeed.R;
import ru.ivied.flickrfeed.events.FlickrFeedEvent;
import ru.ivied.flickrfeed.gson.FlickrData;
import ru.ivied.flickrfeed.gson.FlickrPhoto;
import ru.ivied.flickrfeed.network.FlickrService;


public class FeedFragment extends Fragment {


    public static final String ENDPOINT = "https://www.flickr.com";

    @InjectView(R.id.cardList)
    RecyclerView cardList;


    private FeedAdapter feedAdapter;
    private List<FlickrPhoto> photos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.inject(this, view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);
        feedAdapter = new FeedAdapter(getActivity(), photos);
        cardList.setAdapter(feedAdapter);
        return view;
    }


    public void onEvent(FlickrFeedEvent event) {
        photos.addAll(event.getData().items);
        feedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        File cacheDirectory = new File(getActivity().getApplication().getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                com.squareup.okhttp.Response response = chain.proceed(request);
                return buildResponse(response);
            }
        });
        client.setCache(cache);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(client))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json;versions=1");
                        if (isNetworkAvailable()) {
                            int maxAge = 60; // read from cache for 1 minute
                            request.addHeader("Cache-Control", "public, max-age=" + maxAge);
                        } else {
                            long maxStale = DateUtils.WEEK_IN_MILLIS / 1000; // tolerate 4-weeks stale
                            request.addHeader("Cache-Control",
                                    "public, only-if-cached, max-stale=" + maxStale);
                        }
                    }
                })
                .build();

        FlickrService flickr = restAdapter.create(FlickrService.class);
        flickr.getFeed(callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private Response buildResponse(Response okResponse) {
        return new Response.Builder()
                .request(okResponse.request())
                .code(okResponse.code())
                .message(okResponse.message())
                .body(okResponse.body())
                .headers(getHeaders(okResponse.headers()))
                .protocol(okResponse.protocol())
                .handshake(okResponse.handshake())
                .build();
    }

    private Headers getHeaders(Headers headers) {
        Headers.Builder retrofitHeaders = new Headers.Builder();
        int headerCount = headers.names().size();
        for(int i = 0; i < headerCount; i++){
            if(i == 7 || i ==8){
                retrofitHeaders.add("Cache-Control", "public, max-age=99999999");
                   continue;
            }
            retrofitHeaders.add(headers.name(i), headers.value(i));
        }

        return retrofitHeaders.build();
    }

    private Callback<retrofit.client.Response> callback = new Callback<retrofit.client.Response>() {
        @Override
        public void success(retrofit.client.Response photoList, retrofit.client.Response response) {
            String string = new String(((TypedByteArray) response.getBody()).getBytes());
            string = string.substring("jsonFlickrFeed(".length(), string.length() - 1);
            EventBus.getDefault().post(new FlickrFeedEvent(new Gson().fromJson(string, FlickrData.class)));
        }

        @Override
        public void failure(RetrofitError error) {
            Log.i("omg", "zomg");
        }
    };
}
