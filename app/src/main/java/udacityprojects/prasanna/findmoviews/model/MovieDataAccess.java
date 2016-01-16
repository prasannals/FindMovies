package udacityprojects.prasanna.findmoviews.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Provides an layer of abstraction for accessing data for the adapter views
 */
public interface MovieDataAccess {
    udacityprojects.prasanna.findmoviews.model.MovieModel getMovieModel(int position, boolean fromFavorites);
    //boolean isMovieModelPresent(int position);
//    boolean loadMore();

    /**
     * Inserts all the movie data from the string provided
     * @param receivedData the data received from the movieDB when queries for popularity sort or rating sort. Pass in the data you receive from the query as it is. Don't modify it in any way.
     * @return true if successful, false otherwise
     */
    boolean insertMovies(String receivedData);

    /**
     * Insert a single movie into the database.
     * @param model the MovieModel corresponding to the data to be inserted
     * @param intoFavorites true if the data has to be inserted into Favorites table, false otherwise
     * @return
     */
    boolean insertMovie(MovieModel model, boolean intoFavorites);


    /**
     * returns the number of entries in the CurrentSession table
     * @return the number of entries in the CurrentSession table
     */
    int getNumInsertedInSession();

    /**
     * Deletes all the entries from the table
     * @param b true if you want to delete all the entries from favorites table, false if you want to delete it from CurrentSession table
     * @return
     */
    boolean clearDB(boolean b);

    /**
     * Returns a List of MovieModel objects which correspond to all the entries in the Favorites table
     * @return a List of MovieModel objects which correspond to all the entries in the Favorites table
     */
    List<MovieModel> getAllFavoriteMovies();

    void setNumInsertedInSession(int numInsertedInSession);

}
