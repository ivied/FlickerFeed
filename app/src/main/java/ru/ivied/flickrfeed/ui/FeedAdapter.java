package ru.ivied.flickrfeed.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

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
        String description = photo.description;
        String authorDate = getAuthor(photo.published, description);
        String firstOffer = getDescription(description);
        if(firstOffer != null){
            photoViewHolder.description.setText(Html.fromHtml(firstOffer));
        }else {
            photoViewHolder.description.setVisibility(View.GONE);
        }

        photoViewHolder.title.loadData(authorDate, "text/html; charset=utf-8", null);
        photoViewHolder.title.setBackgroundColor(Color.parseColor("#66FFFFFF"));
        Picasso.with(context).load(photo.media.m).into(photoViewHolder.photo);
    }

    private String getDescription(String description) {
        int descriptionStart =  description.indexOf("<p>", description.indexOf("<p>", description.indexOf("<p>") + 1) + 1);
        if(descriptionStart >= 0) {
            String allDescription = description.substring(descriptionStart);
            return allDescription.substring(allDescription.indexOf("<p>"), allDescription.indexOf("</p>"));
        }else {
            return null;
        }
    }

    private String getAuthor(String published, String description) {
        int authorEnd = description.indexOf("</p>") + "</p>".length();
        String author = description.substring(0, authorEnd);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
        String outputText = null;
        try {
            Date parsed = inputFormat.parse(published);
            outputText = outputFormat.format(parsed);
        } catch (ParseException e) {
            Log.e("FeedAdapter", "Cant parse date");
        }

        return new StringBuilder(author).insert("<p>".length() + 1, outputText + " ").toString();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }



    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.photoView)
        ImageView photo;
        @InjectView(R.id.descriptionTextView)
        TextView description;
        @InjectView(R.id.titleTextView)
        WebView title;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
