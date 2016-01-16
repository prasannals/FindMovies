package udacityprojects.prasanna.findmoviews.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Prasanna Lakkur Subramanyam on 1/12/2016.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public MoviesDBHelper(Context context) {
        super(context, MovieDBContract.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String columns = " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MovieDBContract.FavoritesTable.POSTER_PATH
                + " TEXT, " + MovieDBContract.FavoritesTable.OVERVIEW + " TEXT, " + MovieDBContract.FavoritesTable.RELEASE_DATE + " TEXT, "
                + MovieDBContract.FavoritesTable.MOVIE_ID + " INTEGER, " + MovieDBContract.FavoritesTable.TITLE + " TEXT, "
                + MovieDBContract.FavoritesTable.VOTE_AVERAGE + " REAL, " + MovieDBContract.FavoritesTable.TRAILERS + " TEXT, "
                + MovieDBContract.FavoritesTable.REVIEWS + " TEXT )";

        String createFavTableStatement = "CREATE TABLE " + MovieDBContract.FavoritesTable.TABLE_NAME + columns ;

        String createSessionTableStatement = "CREATE TABLE " + MovieDBContract.CurrentSessionDataTable.TABLE_NAME + columns;

        db.execSQL(createFavTableStatement);
        db.execSQL(createSessionTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
