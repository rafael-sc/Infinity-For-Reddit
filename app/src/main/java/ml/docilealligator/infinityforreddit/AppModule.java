package ml.docilealligator.infinityforreddit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ml.docilealligator.infinityforreddit.CustomTheme.CustomThemeWrapper;
import ml.docilealligator.infinityforreddit.Utils.CustomThemeSharedPreferencesUtils;
import ml.docilealligator.infinityforreddit.Utils.RedditUtils;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
class AppModule {
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Named("oauth")
    @Singleton
    Retrofit provideOauthRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(RedditUtils.OAUTH_API_BASE_URI)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Named("oauth_without_authenticator")
    @Singleton
    Retrofit provideOauthWithoutAuthenticatorRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RedditUtils.OAUTH_API_BASE_URI)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Named("no_oauth")
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RedditUtils.API_BASE_URI)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Named("upload_media")
    @Singleton
    Retrofit provideUploadMediaRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RedditUtils.API_UPLOAD_MEDIA_URI)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Named("upload_video")
    @Singleton
    Retrofit provideUploadVideoRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RedditUtils.API_UPLOAD_VIDEO_URI)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(@Named("no_oauth") Retrofit retrofit, RedditDataRoomDatabase accountRoomDatabase) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.authenticator(new AccessTokenAuthenticator(retrofit, accountRoomDatabase))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS));
                //.addInterceptor(new Okhttp3DebugInterceptor(mApplication));
        return okHttpClientBuilder.build();
    }

    @Provides
    @Singleton
    RedditDataRoomDatabase provideRedditDataRoomDatabase() {
        return RedditDataRoomDatabase.getDatabase(mApplication);
    }

    @Provides
    @Named("default")
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    @Provides
    @Named("theme")
    @Singleton
    SharedPreferences provideThemeSharedPreferences() {
        return mApplication.getSharedPreferences(CustomThemeSharedPreferencesUtils.THEME_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    CustomThemeWrapper provideCustomThemeWrapper(@Named("theme") SharedPreferences themeSharedPreferences) {
        return new CustomThemeWrapper(themeSharedPreferences);
    }
}
