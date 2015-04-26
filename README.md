# FlickerFeed

```
1. Create a simple application that loads photos from Flickr feed (public REST 
API https://www.flickr.com/services/feeds/photos_public.gne?format=json)
and shows them as a list or grid view. 
2. Every list must contain: picture thumbnail, date published 
(with respect to user date time settings) and description.
3. Tapping on list Item should open web page corresponding to “link” JSON field.  
4. Application must provide ability to refresh content.  
5. Application must be compatible with Android 4.0 and above. 
6. Application must gracefully handle device screen orientation changes 
(generally -configuration changes) and network connectivity unavailability.
7. Ability to create optimal Material Ul layouts and to use dependency injection 
pattern will be taken into consideration. Delivery: Build-able, working source code
	  
```
1. Make as list
2. Done
3. Done(additional tap on author open author page)
4. Done (scroll to refresh)
5. Done
6. Done
7. Used cards and RecyclerView. 
  Make dialog and reafresh page action in Material Design. 
  Used dependency injection (ButterKnife)
  I had expirience, in some number of other projects,
  with unsing Dagger and Dagger2(Dependency injection libs) for Android Testing and Espresso
