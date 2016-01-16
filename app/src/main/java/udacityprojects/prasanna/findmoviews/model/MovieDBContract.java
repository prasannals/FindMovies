package udacityprojects.prasanna.findmoviews.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Represents the contract for MoviesContentProvider.
 */
public final class MovieDBContract {

    public static final String AUTHORITY = "udacityprojects.prasanna.findmoviews";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String DB_NAME = "movies_database";

    /**
     * Provides all the entries and content uri for the FavoritesTable
     */
    public static final class FavoritesTable implements BaseColumns{
        public static final String TABLE_NAME = "favorites_table";

        public static final Uri CONTENT_URI = Uri.parse(MovieDBContract.CONTENT_URI + "/"  + MovieDBContract.FavoritesTable.TABLE_NAME);

        public static final String POSTER_PATH = "poster_path";

        public static final String OVERVIEW = "overview";

        public static final String RELEASE_DATE = "release_date";

        public static final String MOVIE_ID = "id";

        public static final String TITLE = "title";

        public static final String VOTE_AVERAGE = "vote_average";

        public static final String TRAILERS = "trailers";

        public static final String REVIEWS = "reviews";

    }


    /**
     * Provides all the entries and content uri for the CurrentSessionDataTable
     */
    public static final class CurrentSessionDataTable implements BaseColumns{
        public static final String TABLE_NAME = "current_session_data_table";

        public static final Uri CONTENT_URI = Uri.parse(MovieDBContract.CONTENT_URI + "/"  + MovieDBContract.CurrentSessionDataTable.TABLE_NAME );

        public static final String POSTER_PATH = "poster_path";

        public static final String OVERVIEW = "overview";

        public static final String RELEASE_DATE = "release_date";

        public static final String MOVIE_ID = "id";

        public static final String TITLE = "title";

        public static final String VOTE_AVERAGE = "vote_average";

        public static final String TRAILERS = "trailers";

        public static final String REVIEWS = "reviews";

    }

}
