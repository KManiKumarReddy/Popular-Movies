package com.kmanikumarreddy.popularmoviesstage1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "Popular Movies";
    final static String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    GridView mainGrid;
    ArrayList<Movie> movieList;
    String sortPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkConnection()) {
            return;
        }
        mainGrid = (GridView) findViewById(R.id.topMovieGrid);
        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Movie", movie);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortPreference = sharedPreferences.getString("sorting_preference", "popular");
        String params[] = {sortPreference};
        new MainSync().execute(params);
    }

    boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(getString(R.string.network_alert_title));
            dialog.setMessage(getString(R.string.network_alert_message));
            dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sortPreference.equals(sharedPreferences.getString("sorting_preference", "popular"))) {
            sortPreference = sharedPreferences.getString("sorting_preference", "popular");
            if (!checkConnection()) {
                return;
            }
            String params[] = {sortPreference};
            new MainSync().execute(params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movieList", movieList);
        super.onSaveInstanceState(outState);

    }

    private void URLResult(String webAddress, ArrayList<Movie> _List) {
        try {
            URL url = new URL(webAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String results = IOUtils.toString(inputStream);
            jsonParser(results, _List);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void jsonParser(String s, ArrayList<Movie> movies) {
        try {
            JSONObject mainObject = new JSONObject(s);

            JSONArray resultsArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie indexMovie = new Movie();
                indexMovie.setBackdropPath(indexObject.getString("backdrop_path"));
                indexMovie.setId(indexObject.getInt("id"));
                indexMovie.setOriginalTitle(indexObject.getString("original_title"));
                indexMovie.setOverview(indexObject.getString("overview"));
                indexMovie.setReleaseDate(indexObject.getString("release_date"));
                indexMovie.setPoster_path(indexObject.getString("poster_path"));
                indexMovie.setPopularity(indexObject.getDouble("popularity"));
                indexMovie.setTitle(indexObject.getString("title"));
                indexMovie.setVoteAverage(indexObject.getInt("vote_average"));
                indexMovie.setVoteCount(indexObject.getInt("vote_count"));

                movies.add(indexMovie); // Add each item to the list
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Error", e);
        }
    }

    public class MainSync extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("Loading...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String webAddress;
            webAddress = "http://api.themoviedb.org/3/movie/" + params[0] + "?api_key="
                    + API_KEY;
            movieList = new ArrayList<>();
            URLResult(webAddress, movieList);
            return null;
        }

        @Override
        protected void onPostExecute(Void  s) {
            super.onPostExecute(s);
            mainGrid.setAdapter(new CustomGridAdapter(MainActivity.this, movieList));
            dialog.cancel();
        }
    }

    public class CustomGridAdapter extends BaseAdapter {
        Context context;
        ArrayList<Movie> movieList;

        CustomGridAdapter(Context context, ArrayList<Movie> movieDbList) {
            this.context = context;
            this.movieList = movieDbList;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return movieList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Movie getItem(int position) {
            return movieList.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 123456000 + position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(context).load("https://image.tmdb.org/t/p/w500"
                    + getItem(position).getPosterPath())
                    .placeholder(R.drawable.place_holder)
                    .into(imageView);

            return imageView;
        }
    }
}
