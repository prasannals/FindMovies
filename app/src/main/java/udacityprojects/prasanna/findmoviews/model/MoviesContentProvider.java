package udacityprojects.prasanna.findmoviews.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


/**
 * ContentProvider for FavoritesTable and CurrentSessionsDataTable. Use MovieDBContract class for corresponding Uris
 */
public class MoviesContentProvider extends ContentProvider {

    private static final int FAVORITES_DB_CODE = 1;
    private static final int SESSION_DB_CODE = 2;
    MoviesDBHelper mMoviesDBHelper;

    UriMatcher mUriMatcher;


    public MoviesContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(MovieDBContract.AUTHORITY, MovieDBContract.FavoritesTable.TABLE_NAME, FAVORITES_DB_CODE);
        mUriMatcher.addURI(MovieDBContract.AUTHORITY, MovieDBContract.CurrentSessionDataTable.TABLE_NAME, SESSION_DB_CODE);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (mUriMatcher.match(uri)) {
            case FAVORITES_DB_CODE:
                return mMoviesDBHelper.getWritableDatabase().delete(MovieDBContract.FavoritesTable.TABLE_NAME,
                        selection, selectionArgs);

            case SESSION_DB_CODE:
                Log.d("DELETE", "In delete");
                mMoviesDBHelper.getWritableDatabase().delete("sqlite_sequence", "name = ?", new String[]{MovieDBContract.CurrentSessionDataTable.TABLE_NAME});
                Log.d("DELETE", "SEQ Deleted");
                return mMoviesDBHelper.getWritableDatabase().delete(MovieDBContract.CurrentSessionDataTable.TABLE_NAME,
                        selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unsupported URI");

        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        switch (mUriMatcher.match(uri)) {
            case FAVORITES_DB_CODE:
                id = mMoviesDBHelper.getWritableDatabase().insert(MovieDBContract.FavoritesTable.TABLE_NAME, null, values);

                return ContentUris.withAppendedId(uri, id);

            case SESSION_DB_CODE:
                id = mMoviesDBHelper.getWritableDatabase().insert(MovieDBContract.CurrentSessionDataTable.TABLE_NAME, null, values);

                return ContentUris.withAppendedId(uri, id);

            default:
                throw new UnsupportedOperationException("Unsupported URI");
        }
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case FAVORITES_DB_CODE:
                return mMoviesDBHelper.getWritableDatabase().query(MovieDBContract.FavoritesTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

            case SESSION_DB_CODE:
                return mMoviesDBHelper.getWritableDatabase().query(MovieDBContract.CurrentSessionDataTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

            default:
                throw new UnsupportedOperationException("Unsupported URI");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (mUriMatcher.match(uri)) {
            case FAVORITES_DB_CODE:
                return mMoviesDBHelper.getWritableDatabase().update(MovieDBContract.FavoritesTable.TABLE_NAME,
                        values, selection, selectionArgs);

            case SESSION_DB_CODE:
                return mMoviesDBHelper.getWritableDatabase().update(MovieDBContract.CurrentSessionDataTable.TABLE_NAME,
                        values, selection, selectionArgs);

            default:
                throw new UnsupportedOperationException("Unsupported URI");

        }
    }
}
