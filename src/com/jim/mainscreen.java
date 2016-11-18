package com.jim;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;


public class mainscreen extends Activity
{
    String base_url = "http://www.omdbapi.com/";
    String[] savedMovies;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void movieSearch(View button){
        Context context = getApplicationContext();
        EditText queryView = (EditText) findViewById(R.id.query);
        String query = queryView.getText().toString();
        if (!query.isEmpty()){
            String url = buildQuery(query);
            new ReadMovieJsonTask().execute(url);
        }
    }

    public void showSavedMovies(View view){
        Context context = getApplicationContext();
        Intent showSavedMovie = new Intent(context, ShowSavedMovies.class);
        startActivity(showSavedMovie);
    }
    public String buildQuery(String query ){
        //URL encode the query from the user
        try {
            String encoded_query = URLEncoder.encode(query, "UTF-8");
            return String.format(base_url + "?s=%s&plot=full&r=json",
                    encoded_query);
        } catch (UnsupportedEncodingException e) {
            return "";
            // TODO create a Toast that says that the user can't type.
        }
    }

    public String getJSON(String URL) {
        Context context = getApplicationContext();
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
                CharSequence text = "Downloading movie information failed.";
                Toast toast = Toast.makeText(context, text, 500);
                toast.show();
                Log.d("JSON", "Failed to download");
            }
        } catch (Exception e) {
            Log.d("readJsonFEED", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    private class ReadMovieJsonTask extends AsyncTask <String, Void, String> {
        protected String doInBackground(String... urls) {
            return getJSON(urls[0]);
        }

        protected void onPostExecute(String result) {
            Context context = getApplicationContext();
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray movies = jsonObject.getJSONArray("Search");

                // A hashmap to hold all the movies and their respected values.
                List<HashMap<String, String>> movies_map = new ArrayList<HashMap<String, String>>();
                String[] from = new String[] {"title"};
                int[] to = {R.id.list_title};

                for (int i=0; i < movies.length(); i++) {
                    HashMap<String, String> movie_map = new HashMap<String, String>();
                    JSONObject movie = movies.getJSONObject(i);
                    movie_map.put("title", movie.getString("Title"));
                    movies_map.add(movie_map);
                }
                // Insert it into the ListView.
                SimpleAdapter adapter = new SimpleAdapter(context, movies_map,
                        R.layout.movie_list_item, from, to);

                ListView resultList = (ListView) findViewById(R.id.result_list);
                resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        Context context = getApplicationContext();
                        TextView title = (TextView) view.findViewById(R.id.list_title);
                        String movieTitle = title.getText().toString();
                        Log.d("SNTOHESNTHOESNUTH", title.getText().toString());

                        Intent showMovie = new Intent(context, ShowMovie.class);
                        showMovie.putExtra("title", movieTitle);
                        startActivity(showMovie);
                    }
                });
                resultList.setAdapter(adapter);

            } catch (Exception e){
                Log.d("READMOVIE", e.getLocalizedMessage());
            }
        }
    }
}
