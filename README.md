# Uttam

<img src="https://raw.githubusercontent.com/libhide/uttam/develop/art/branding.jpg" alt="Uttam branding">

Uttam is a small Android wallpaper app that gently refreshes your home screen with a photograph from Unsplash every day.

## Features

- Fetch a new wallpaper on demand
- Refresh daily in the background when connected to a network
- Optionally set the home-screen wallpaper automatically
- Receive a rich notification when a new wallpaper is available
- Preview, save, share, or set a wallpaper manually
- Credit and link back to the Unsplash photographer

## Story

Read [the story behind Uttam](https://www.ratik.in/writing/uttam).

## Development

### Requirements

- JDK 17
- Android SDK with API 37
- An [Unsplash API](https://unsplash.com/developers) client ID

### Setup

1. Clone the repository.
2. Add your Unsplash client ID to `local.properties`:

   ```properties
   client_id=<UNSPLASH_CLIENT_ID>
   ```

3. Create the local signing-properties file required by the Gradle configuration:

   ```shell
   touch keystore.properties
   ```

4. Build the debug app:

   ```shell
   ./gradlew :app:assembleDebug
   ```

Run the project checks with:

```shell
./gradlew spotlessCheck test :app:lintDebug
```

## Technology

Uttam is written in Kotlin and uses Jetpack Compose, Hilt, WorkManager, Retrofit, OkHttp, Coil, and Kotlin coroutines.

The app supports Android 7.0 and newer and currently targets Android API 37.

## Maintenance

Uttam is a personal project maintained primarily for sentimental reasons. Unsplash API constraints mean it is not intended to scale into a commercial wallpaper service, but the app remains in active use and receives focused reliability and compatibility updates.

## Thanks

Thanks to [Arun](https://twitter.com/voidmaindev) and [Chetan](https://twitter.com/chetsachdeva) for their help while Uttam evolved from its original legacy implementation into the app it is today.
