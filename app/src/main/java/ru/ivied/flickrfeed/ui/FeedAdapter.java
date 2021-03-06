package ru.ivied.flickrfeed.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ivied.flickrfeed.R;
import ru.ivied.flickrfeed.Utils;
import ru.ivied.flickrfeed.gson.FlickrPhoto;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PhotoViewHolder>{

    private List<FlickrPhoto> photos;
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


    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int position) {
        final FlickrPhoto photo = photos.get(position);
        String description = photo.description;
        String authorDate = Utils.getAuthor(photo.published, description);
        String firstOffer = Utils.getDescription(description);
        if(firstOffer != null){
            photoViewHolder.description.setText(Html.fromHtml(firstOffer));
            photoViewHolder.description.setVisibility(View.VISIBLE);
        }else {
            photoViewHolder.description.setVisibility(View.GONE);
        }
        photoViewHolder.title.loadData(authorDate, "text/html; charset=utf-8", null);
        photoViewHolder.title.setBackgroundColor(context.getResources().getColor(R.color.invisible_white));
        Picasso.with(context).load(photo.media.m).fit().centerCrop().into(photoViewHolder.photo);
        View.OnClickListener photoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(photo.link));
                context.startActivity(browserIntent);
            }
        };
        photoViewHolder.description.setOnClickListener(photoClickListener);
        photoViewHolder.photo.setOnClickListener(photoClickListener);
        photoViewHolder.card.setOnClickListener(photoClickListener);
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
        @InjectView(R.id.cardView)
        CardView card;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
