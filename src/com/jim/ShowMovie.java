package com.jim;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;


public class ShowMovie extends Activity
{
    String base_url = "http://www.omdbapi.com/";
    String movie_title;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_movie);

        // Get the movie title we are supposed to show
        Intent intent = getIntent();
        movie_title = intent.getStringExtra("title");
        String url = buildQuery(movie_title);
        new ReadMovieFullJsonTask().execute(url);
    }

    public void saveMovie(View view){
        Context context = getApplicationContext();
        SharedPreferences prefs = getSharedPreferences("db", 0);
        Set<String> movies = prefs.getStringSet("movies", new HashSet<String>());
        Set<String> movie = new HashSet<String>(movies);
        Log.d("movietitle", movie_title);
        movie.add(movie_title);
        prefs.edit().putStringSet("movies", movie).commit();
        CharSequence text = "Movie saved!";
        Toast toast = Toast.makeText(context, text, 500);
        toast.show();
    }

    public String buildQuery(String query ){
        //URL encode the query from the user
        try {
            String encoded_query = URLEncoder.encode(query, "UTF-8");
            return String.format(base_url + "?t=%s&plot=full&r=json&tomatoes=true",
                    encoded_query);
        } catch (UnsupportedEncodingException e) {
            return "";
            // TODO create a Toast that says that the user can't type.
        }
    }

    public String getJSON(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                    Log.d("JSON", line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download");
            }
        } catch (Exception e) {
            Log.d("readJsonFEED", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    private class ReadMovieFullJsonTask extends AsyncTask <String, Void, String> {
        protected String doInBackground(String... urls) {
            return getJSON(urls[0]);
        }

        protected void onPostExecute(String result) {
            Context context = getApplicationContext();
            try{
                JSONObject jsonObject = new JSONObject(result);
                TextView title = (TextView) findViewById(R.id.movie_title);
                TextView year = (TextView) findViewById(R.id.movie_year);
                TextView genre = (TextView) findViewById(R.id.movie_genre);
                TextView director = (TextView) findViewById(R.id.movie_director);
                TextView actors = (TextView) findViewById(R.id.movie_actors);
                TextView language = (TextView) findViewById(R.id.movie_language);

                title.setText(jsonObject.getString("Title"));
                year.setText(jsonObject.getString("Year"));
                genre.setText(jsonObject.getString("Genre"));
                director.setText(jsonObject.getString("Director"));
                actors.setText(jsonObject.getString("Actors"));
                language.setText(jsonObject.getString("Language"));



            } catch (Exception e){
                Log.d("READMOVIE", e.getLocalizedMessage());
            }
        }
    }
}
