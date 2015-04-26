package ru.ivied.flickrfeed.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ivied.flickrfeed.R;
import ru.ivied.flickrfeed.Utils;
import ru.ivied.flickrfeed.gson.FlickrPhoto;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PhotoViewHolder>{

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private List<FlickrPhoto> photos;
    private int lastAnimatedPosition = -1;
    private Context context;


    public FeedAdapter(Context context, List<FlickrPhoto> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater. from(viewGroup.getContext()).inflate(R.layout.view_photo, viewGroup, false);

        return new PhotoViewHolder(itemView);
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }
        Point size = Utils.getScreenSize(context);
        int height = size.y;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(height);
            view.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(500)
                    .start();
        }
    }


    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int position) {
        runEnterAnimation(photoViewHolder.itemView, position);
        FlickrPhoto photo = photos.get(position);
        photoViewHolder.description.getSettings().setDefaultTextEncodingName("utf-8");
        String description = photo.description;
        int authorEnd = description.indexOf("</p>") + "</p>".length();
        String author = description.substring(0, authorEnd);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
        String outputText = null;
        try {
            Date parsed = inputFormat.parse(photo.published);
            outputText = outputFormat.format(parsed);
        } catch (ParseException e) {
            Log.e("FeedAdapter", "Cant parse date");
        }
        photoViewHolder.description.loadData(description.substring(description.indexOf("</p>", description.indexOf("</p>") + 1)), "text/html; charset=utf-8", null);

        photoViewHolder.title.loadData(new StringBuilder(author).insert("<p>".length() + 1, outputText + " ").toString(), "text/html; charset=utf-8", null);
        photoViewHolder.title.setBackgroundColor(Color.parseColor("#66FFFFFF"));
        Picasso.with(context).load(photo.media.m).into(photoViewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }



    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.photoView)
        ImageView photo;
        @InjectView(R.id.descriptionTextView)
        WebView description;
        @InjectView(R.id.titleTextView)
        WebView title;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
