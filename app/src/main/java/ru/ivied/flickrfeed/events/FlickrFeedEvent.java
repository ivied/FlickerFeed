package ru.ivied.flickrfeed.events;

import ru.ivied.flickrfeed.gson.FlickrData;

public class FlickrFeedEvent {
    private FlickrData data;

    public FlickrFeedEvent(FlickrData data) {
        this.data = data;
    }

    public FlickrData getData() {
        return data;
    }
}
