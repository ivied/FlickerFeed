package ru.ivied.flickrfeed.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
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
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        FlickrService flickr = restAdapter.create(FlickrService.class);
        flickr.getFeed(callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
