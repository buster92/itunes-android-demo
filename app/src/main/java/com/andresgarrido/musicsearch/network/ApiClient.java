package com.andresgarrido.musicsearch.network;

import androidx.annotation.NonNull;

import com.andresgarrido.musicsearch.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ApiClient {

	private static Retrofit client;

	@NonNull
	static Retrofit getClient() {
		if (client == null) {

			try {

				HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
				loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

				OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
						.connectTimeout(60, TimeUnit.SECONDS)
						.readTimeout(60, TimeUnit.SECONDS)
						.writeTimeout(60, TimeUnit.SECONDS)
						.addNetworkInterceptor(loggingInterceptor);

				Gson gson = new GsonBuilder()
						.setDateFormat("yyyy-MM-dd HH:mm:ss")
						.create();

				client = new Retrofit.Builder()
						.baseUrl(BuildConfig.BASE_URL)
						.addConverterFactory(GsonConverterFactory.create(gson))
						.client(httpClient.build())
						.build();

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		return client;
	}
}

