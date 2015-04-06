# AdMobBanner #

The difference with this component is that you must have the Google AdMob SDK library added to your build path for this to work. Get this from AdMob's site. Also, you must supply your AdMob Id to the component.

```
// Declaring an admob instance

private AdMobBanner ad;


// Instantiate the admob banner, and give it your admob ID

ad = new AdMobBanner(this, "admobIDgoeshere");


// Start loading the ad. The ad will stop loading when your app loses focus.

ad.startAd();


// If you want to manually stop the ad from loading

ad.stopLoadingAd();


// If you are testing ads for a new app you are developing (sometimes 
// admob won't serve ads until the app is actually in the market). 
// Check your logcat for the ID string to use.

ad.addTestDevice("YourDevicesIDstring");

```