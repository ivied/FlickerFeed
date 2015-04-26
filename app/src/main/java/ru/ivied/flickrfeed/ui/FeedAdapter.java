package ru.ivied.flickrfeed.ui;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ivied.flickrfeed.R;
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
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
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
        photoViewHolder.description.getSettings().setJavaScriptEnabled(true);
        photoViewHolder.description.getSettings().setDefaultTextEncodingName("utf-8");
        photoViewHolder.description.loadData(photo.description, "text/html; charset=utf-8", null);
        photoViewHolder.title.setText(photo.date_taken);
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
        TextView title;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
