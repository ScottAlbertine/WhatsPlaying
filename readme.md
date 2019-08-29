This is a quick and simple app I put together to show what's playing on a given Chromecast on the local network. It's mostly designed for Chromecast Audios or Google Homes, so I don't have to keep asking "Hey Google, what's playing?".

When you open the app, it begins an mDNS scan for Chromecasts, and displays them in a list. When you tap on an entry in the list, it jumps you to a landscape screen that shows you what's playing on that Chromecast.

This includes:
* song title (which does marquee scrolling)
* artist name (also with marquee scrolling)
* current volume (works even when nothing is playing)
* progress bar for the current song (or video)
* icon for the currently running app
* album art (or video thumbnail) if available

Not all apps populate all the above information. The information extracted is optimized for Spotify (which is 90% of what I use it for). Most apps use a similar enough metadata encoding scheme: you'll even get video thumbnails on certain apps instead of album art.

I designed this app to run on a $40 wifi-only phone (LG K9) that's always plugged in. Your mileage may vary with other handsets. If you run into problems, please submit a pull request, I'd be happy to have more compatibility. The app automatically turns the screen on when it detects a new app start to play on the selected Chromecast, then relinquishes that wakelock when it detects that no app is running. Thus, it won't keep the screen on, saving power and avoiding burn-in, as well as being unobtrusive at night.

While Java is my main language, Android is very much not my main platform. I've probably done some very stupid things in here, if you see them and have a better way to do them, please send me a pull request.

I build, run, and debug this using Intellij's Android Studio plugin for Idea, though you can probably run and deploy it using plain Gradle command line tricks if you know them. It doesn't require any special local dependencies or build configuration, everything is pulled from Maven and Gradle central repos.

I'd like to give a big shout-out to Vitaly Litvak for the Chromecast API I'm using, as Google's built-in one doesn't allow you to see what's playing without automatically closing the current Chromecast app, and having it open yours. 
