package udacityprojects.prasanna.findmoviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import udacityprojects.prasanna.findmoviews.model.DBDirectDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieModel;

/**
 * Adapter class for displaying the search results (popularity sort and rating sort)
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private static final String LOG_TAG = "FindMovies_MovieAdapter";
    private Context mContext;
    private MovieListFragment mMovieListFragment;

    private MovieDataAccess mDBDirectDataAccess;

    /**
     *
     * @param context the current context
     * @param fragment the fragment which contains the adapter view using this adapter
     */
    public MovieAdapter(Context context , MovieListFragment fragment){
        mContext = context;
        mDBDirectDataAccess = new DBDirectDataAccess(context);
        mMovieListFragment = fragment;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_element, parent, false);
        view.setOnClickListener(mMovieListFragment.new OnMoviesClickListener());
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieModel movieModel = mDBDirectDataAccess.getMovieModel(position, false);
        ImageView imageView = holder.getImageView();

        if(movieModel != null)
            Picasso.with(mContext).load(movieModel.getPosterPath()).into(imageView);
        else
            Log.d(LOG_TAG, "Movie Model was null");
    }

    @Override
    public int getItemCount() {
        return mDBDirectDataAccess.getNumInsertedInSession();
    }

    public MovieDataAccess getDBDirectDataAccess() {
        return mDBDirectDataAccess;
    }
}
