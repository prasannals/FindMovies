package udacityprojects.prasanna.findmoviews.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import udacityprojects.prasanna.findmoviews.R;


/**
 * This class acts as a gateway between adapters and data
 */
public class DBDirectDataAccess implements MovieDataAccess {

    private Context mContext;
    private int numInsertedInFavorites;
    private int numInsertedInSession;


    public DBDirectDataAccess(Context context) {
        mContext = context;

    }

    /**
     * Returns a MovieModel object corresponding to the position provided
     * @param position position of the view on the adapter view
     * @param fromFavorites true if you want the data from the favorites db, false otherwise
     * @return
     */
    @Override
    public MovieModel getMovieModel(int position, boolean fromFavorites) {
        MovieModel movieModel = null;
        Cursor cursor;
        if (fromFavorites) {
            cursor = mContext.getContentResolver().query(MovieDBContract.FavoritesTable.CONTENT_URI,
                    null, BaseColumns._ID + " = ?", new String[]{"" + (position + 1)}, null);

            try {
                movieModel = getFromCursor(cursor, fromFavorites);
                cursor.close();
                return movieModel;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            cursor = mContext.getContentResolver().query(MovieDBContract.CurrentSessionDataTable.CONTENT_URI,
                    null, BaseColumns._ID + " = ?", new String[]{"" + (position + 1)}, null);

            try {
                movieModel = getFromCursor(cursor, fromFavorites);
                cursor.close();
                return movieModel;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

    }


    private MovieModel getFromCursor(Cursor cursor, boolean fromFavorites) throws JSONException {

        if(cursor.moveToFirst()) {
            if (fromFavorites) {
                return getFavoriteMovieModel(cursor);
            } else {
                return getSessionMovieModel(cursor);
            }
        }else {
            return null;
        }
    }

    @NonNull
    private MovieModel getFavoriteMovieModel(Cursor cursor) throws JSONException {
        MovieModel movieModel = new MovieModel();

        movieModel.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieDBContract.FavoritesTable.MOVIE_ID)));
        movieModel.setOverview(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.OVERVIEW)));
        movieModel.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.POSTER_PATH)));
        movieModel.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.RELEASE_DATE)));
        movieModel.setReviews(new JSONArray(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.REVIEWS))));
        movieModel.setTrailerLinks(new JSONArray(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.TRAILERS))));
        movieModel.setTitle(cursor.getString(cursor.getColumnIndex(MovieDBContract.FavoritesTable.TITLE)));
        movieModel.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieDBContract.FavoritesTable.VOTE_AVERAGE)));

        return movieModel;
    }

    @NonNull
    private MovieModel getSessionMovieModel(Cursor cursor) {
        MovieModel movieModel = new MovieModel();

        movieModel.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.MOVIE_ID)));
        movieModel.setOverview(cursor.getString(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.OVERVIEW)));
        movieModel.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.POSTER_PATH)));
        movieModel.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.RELEASE_DATE)));
        movieModel.setTitle(cursor.getString(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.TITLE)));
        movieModel.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieDBContract.CurrentSessionDataTable.VOTE_AVERAGE)));

        return movieModel;
    }


    /**
     * Inserts all the movie data from the string provided
     * @param receivedData the data received from the movieDB when queries for popularity sort or rating sort. Pass in the data you receive from the query as it is. Don't modify it in any way.
     * @return true if successful, false otherwise
     */
    @Override
    public boolean insertMovies(String receivedData) {
        try {
            JSONObject object = new JSONObject(receivedData);
            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieEntry = results.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(MovieDBContract.CurrentSessionDataTable.POSTER_PATH, movieEntry.getString(MovieDBContract.CurrentSessionDataTable.POSTER_PATH));
                values.put(MovieDBContract.CurrentSessionDataTable.OVERVIEW, movieEntry.getString(MovieDBContract.CurrentSessionDataTable.OVERVIEW));
                values.put(MovieDBContract.CurrentSessionDataTable.RELEASE_DATE, movieEntry.getString(MovieDBContract.CurrentSessionDataTable.RELEASE_DATE));
                int movieID = movieEntry.getInt(MovieDBContract.CurrentSessionDataTable.MOVIE_ID);
                values.put(MovieDBContract.CurrentSessionDataTable.MOVIE_ID, movieID);
                values.put(MovieDBContract.CurrentSessionDataTable.TITLE, movieEntry.getString(MovieDBContract.CurrentSessionDataTable.TITLE));
                values.put(MovieDBContract.CurrentSessionDataTable.VOTE_AVERAGE, movieEntry.getDouble(MovieDBContract.CurrentSessionDataTable.VOTE_AVERAGE));


                mContext.getContentResolver().insert(MovieDBContract.CurrentSessionDataTable.CONTENT_URI, values);

                numInsertedInSession++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Insert a single movie into the database.
     * @param model the MovieModel corresponding to the data to be inserted
     * @param intoFavorites true if the data has to be inserted into Favorites table, false otherwise
     * @return
     */
    @Override
    public boolean insertMovie(MovieModel model, boolean intoFavorites) {
        ContentValues values = new ContentValues();
        if (intoFavorites) {
            values.put(MovieDBContract.FavoritesTable.POSTER_PATH, model.getPosterPath());
            values.put(MovieDBContract.FavoritesTable.OVERVIEW, model.getOverview());
            values.put(MovieDBContract.FavoritesTable.RELEASE_DATE, model.getReleaseDate());
            values.put(MovieDBContract.FavoritesTable.MOVIE_ID, model.getMovieId());
            values.put(MovieDBContract.FavoritesTable.TITLE, model.getTitle());
            values.put(MovieDBContract.FavoritesTable.VOTE_AVERAGE, model.getVoteAverage());
            values.put(MovieDBContract.FavoritesTable.TRAILERS, model.getTrailerLinks().toString());
            values.put(MovieDBContract.FavoritesTable.REVIEWS, model.getReviews().toString());

            mContext.getContentResolver().insert(MovieDBContract.FavoritesTable.CONTENT_URI, values);
            numInsertedInFavorites++;
        } else {
            values.put(MovieDBContract.CurrentSessionDataTable.POSTER_PATH, model.getPosterPath());
            values.put(MovieDBContract.CurrentSessionDataTable.OVERVIEW, model.getOverview());
            values.put(MovieDBContract.CurrentSessionDataTable.RELEASE_DATE, model.getReleaseDate());
            values.put(MovieDBContract.CurrentSessionDataTable.MOVIE_ID, model.getMovieId());
            values.put(MovieDBContract.CurrentSessionDataTable.TITLE, model.getTitle());
            values.put(MovieDBContract.CurrentSessionDataTable.VOTE_AVERAGE, model.getVoteAverage());
            values.put(MovieDBContract.CurrentSessionDataTable.TRAILERS, model.getTrailerLinks().toString());
            values.put(MovieDBContract.CurrentSessionDataTable.REVIEWS, model.getReviews().toString());

            mContext.getContentResolver().insert(MovieDBContract.CurrentSessionDataTable.CONTENT_URI, values);
            numInsertedInSession++;
        }

        return true;
    }

    /**
     * returns the number of entries in the CurrentSession table
     * @return the number of entries in the CurrentSession table
     */
    @Override
    public int getNumInsertedInSession() {
        return numInsertedInSession;
    }

    @Override
    public void setNumInsertedInSession(int numInsertedInSession){
        this.numInsertedInSession = numInsertedInSession;
    }

    /**
     * Deletes all the entries from the table
     * @param favorites true if you want to delete all the entries from favorites table, false if you want to delete it from CurrentSession table
     * @return
     */
    @Override
    public boolean clearDB(boolean favorites){
        if(favorites){
            mContext.getContentResolver().delete(MovieDBContract.FavoritesTable.CONTENT_URI, null, null);
            return true;
        }else{
            mContext.getContentResolver().delete(MovieDBContract.CurrentSessionDataTable.CONTENT_URI, null, null);
            numInsertedInSession = 0;
            return true;
        }
    }

    /**
     * Returns a List of MovieModel objects which correspond to all the entries in the Favorites table
     * @return a List of MovieModel objects which correspond to all the entries in the Favorites table
     */
    @Override
    public List<MovieModel> getAllFavoriteMovies() {
        List<MovieModel> movieModelList = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(MovieDBContract.FavoritesTable.CONTENT_URI,
                null, null, null, null);

        if(cursor.moveToFirst()){
            do {
                try {
                    movieModelList.add( getFavoriteMovieModel(cursor) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }

        cursor.close();

        return movieModelList;
    }



}
