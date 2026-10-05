package com.company.fieldattendance.data.api;

import android.content.Context;
import com.company.fieldattendance.BuildConfig;
import com.company.fieldattendance.data.local.SessionManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
        retrofit = null;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            if (appContext != null) {
                httpClient.addInterceptor(new PresentationInterceptor(appContext));
            }

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                if (appContext != null) {
                    SessionManager sessionManager = new SessionManager(appContext);
                    String token = sessionManager.getToken();
                    if (token != null && !token.isEmpty() && !original.headers().names().contains("Authorization")) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                }
                return chain.proceed(requestBuilder.build());
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
