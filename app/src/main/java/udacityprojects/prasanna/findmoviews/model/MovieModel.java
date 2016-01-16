package udacityprojects.prasanna.findmoviews.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import udacityprojects.prasanna.findmoviews.R;

/**
 * Represents an entry in the table of FavoritesTable or CurrentSessionDataTable
 */
public class MovieModel implements Parcelable{

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String YOUTUBE_BASE_LINK = "https://www.youtube.com/watch?v=";

    //data fields
    private String posterPath;
    private String overview;
    private String releaseDate;
    private int movieId;
    private String title;
    private double voteAverage;
    private JSONArray trailerLinks;
    private JSONArray reviews;

    public MovieModel(){}



    //necessary logic for the implementation of Parcelable interface
    protected MovieModel(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        movieId = in.readInt();
        title = in.readString();
        voteAverage = in.readDouble();
        try {
            trailerLinks = new JSONArray(in.readString());
            reviews = new JSONArray(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeString(trailerLinks.toString());
        dest.writeString(reviews.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Returns a ContentValues object that contains the data in the current MovieModel
     * @return a ContentValues object that contains the data in the current MovieModel which corresponds to the FavoritesTable or CurrentSessionTable columns
     */
    public ContentValues getMovieContentValues(){
        ContentValues values = new ContentValues();
        values.put(MovieDBContract.CurrentSessionDataTable.POSTER_PATH, posterPath);
        values.put(MovieDBContract.CurrentSessionDataTable.OVERVIEW, overview);
        values.put(MovieDBContract.CurrentSessionDataTable.RELEASE_DATE, releaseDate);
        values.put(MovieDBContract.CurrentSessionDataTable.MOVIE_ID, movieId);
        values.put(MovieDBContract.CurrentSessionDataTable.TITLE, title);
        values.put(MovieDBContract.CurrentSessionDataTable.VOTE_AVERAGE, voteAverage);
        values.put(MovieDBContract.CurrentSessionDataTable.TRAILERS, trailerLinks.toString());
        values.put(MovieDBContract.CurrentSessionDataTable.REVIEWS, reviews.toString());

        return values;
    }


    //getters and setters

    /**
     *
     * @return a full link which when queried returns the poster image
     */
    public String getPosterPath() {
        return POSTER_BASE_URL + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public JSONArray getTrailerLinks() {
        return trailerLinks;
    }

    public void setTrailerLinks(JSONObject trailerLinks) {
        try {
            this.trailerLinks = trailerLinks.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTrailerLinks(JSONArray trailerLinks) {
        this.trailerLinks = trailerLinks;
    }

    public void setReviews(JSONArray reviews) {
        this.reviews = reviews;
    }

    public JSONArray getReviews() {
        return reviews;
    }

    public void setReviews(JSONObject reviews) {
        try {
            this.reviews = reviews.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return A List of String where each element is a YouTube URL for a trailer of the current movie. null if there was an exception.
     */
    public List<String> getYoutubeTrailerLinks(){
        List<String> list = new ArrayList<String>();

        for(int i = 0; i < trailerLinks.length(); i++){
            try {
                JSONObject trailer = trailerLinks.getJSONObject(i);
                if(trailer.getString("site").equals("YouTube")){
                    list.add( YOUTUBE_BASE_LINK + trailer.getString("key") );
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return list;
    }


    /**
     *
     * @return List of Review objects where each element represents a review for the movie. Can be empty, can be null. Handle these situations properly.
     */
    public List<Review> getReviewList(){
        List<Review> reviewList = new ArrayList<>();

        for(int i = 0; i < reviews.length(); i++){
            try {
                JSONObject review = reviews.getJSONObject(i);

                Review r = new Review();
                r.setAuthor(review.getString("author"));
                r.setContent(review.getString("content"));
                reviewList.add(r);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

        return reviewList;
    }

    /**
     *
     * @return String containing the movie name + overview + trailer link
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(title);
        builder.append("\n" + overview + "\n\n");

        List<String> trailers = getYoutubeTrailerLinks();

        if(trailers != null && trailers.size() > 0){
            builder.append(trailers.get(0));
        }

        return builder.toString();
    }



}
