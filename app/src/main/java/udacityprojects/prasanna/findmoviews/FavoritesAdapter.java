package udacityprojects.prasanna.findmoviews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import udacityprojects.prasanna.findmoviews.model.DBDirectDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieModel;

/**
 * Adapter which provides data when the user wants to display the Favorite movies.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private List<MovieModel> movieList;
    private Context mContext;
    private MovieListFragment mMovieListFragment;

    public FavoritesAdapter(Context context, MovieListFragment movieListFragment){
        mContext = context;
        refreshData();
        mMovieListFragment = movieListFragment;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_element, parent, false);

        v.setOnClickListener(mMovieListFragment.new OnFavoritesClickListener() );

        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        ImageView imageView = holder.getImageView();
        Picasso.with(mContext).load(movieList.get(position).getPosterPath()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public MovieModel getMovieAt(int position){
        return movieList.get(position);
    }

    public void refreshData(){
        movieList = new DBDirectDataAccess(mContext).getAllFavoriteMovies();
    }
}
