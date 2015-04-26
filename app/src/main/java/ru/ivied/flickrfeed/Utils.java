package ru.ivied.flickrfeed;

import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ru.ivied.flickrfeed.gson.FlickrPhoto;

public class Utils {

    public static String getDescription(String description) {
        int descriptionStart =  description.indexOf("<p>", description.indexOf("<p>", description.indexOf("<p>") + 1) + 1);
        if(descriptionStart >= 0) {
            String allDescription = description.substring(descriptionStart);
            return allDescription.substring(allDescription.indexOf("<p>"), allDescription.indexOf("</p>"));
        }else {
            return null;
        }
    }

    public static String getAuthor(String published, String description) {
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



    public static ArrayList<FlickrPhoto> getNewElemnts(final List<FlickrPhoto> photos, List<FlickrPhoto> items) {
        return Lists.newArrayList(Iterables.filter(items, new Predicate<FlickrPhoto>() {
            @Override
            public boolean apply(final FlickrPhoto input) {
                return !Iterables.tryFind(photos, new Predicate<FlickrPhoto>() {
                    @Override
                    public boolean apply(FlickrPhoto oldPhoto) {
                        return input.description.equalsIgnoreCase(oldPhoto.description);
                    }
                }).isPresent();
            }
        }));
    }

}
