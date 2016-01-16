package udacityprojects.prasanna.findmoviews;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import udacityprojects.prasanna.findmoviews.model.DBDirectDataAccess;
import udacityprojects.prasanna.findmoviews.model.FetchTrailersNReviews;
import udacityprojects.prasanna.findmoviews.model.MovieDBContract;
import udacityprojects.prasanna.findmoviews.model.MovieDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieModel;
import udacityprojects.prasanna.findmoviews.model.Review;

/**
 * Created by Prasanna Lakkur Subramanyam on 1/13/2016.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String DETAILS_FRAG_INDEX_KEY = "DETAILS_FRAG_INDEX_KEY";
    private static final String MOVIE_PARCEL = "MOVIE_PARCEL";
    private static final String FAV_NOT_ANYMORE = "udacityprojects.prasanna.findmoviews_FAV_NOT_ANYMORE";
    private String trailersLink;
    private String reviewsLink;
    private String api_key;
    private MovieModel movieModel;
    private MovieDataAccess movieDataAccess;
    private FetchTrailersNReviews fetchTrailersNReviews;
    private List<String> trailerLinks;
    private int position;
    private LinearLayout detailsBaseLayout;
    private boolean inFavorites;
    private View fragmentMovieDetailsView;
    private Button favoriteButton;
    private OnFavClickCustomAction customAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        movieDataAccess = new DBDirectDataAccess(getContext());

        Bundle args = getArguments();
        position = args.getInt(DETAILS_FRAG_INDEX_KEY, -1);

        //if position is -1 then we don't have to fetch anything. Just use the available data and display stuff
        if (position == -1) {
            movieModel = args.getParcelable(MOVIE_PARCEL);
        } else {
            //movieModel is incomplete. We need to fetch the trailers and reviews
            movieModel = movieDataAccess.getMovieModel(position, false);
            Resources resources = getContext().getResources();
            trailersLink = resources.getString(R.string.videos_endpoint_link);
            reviewsLink = resources.getString(R.string.reviews_endpoint_link);
            api_key = resources.getString(R.string.api_key);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentMovieDetailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        detailsBaseLayout = (LinearLayout) fragmentMovieDetailsView.findViewById(R.id.movieDetailsBaseLinearLayout);

        ((TextView) fragmentMovieDetailsView.findViewById(R.id.detailsMovieName)).setText(movieModel.getTitle());
        ((TextView) fragmentMovieDetailsView.findViewById(R.id.detailsReleaseDateText)).setText(movieModel.getReleaseDate());
        ((TextView) fragmentMovieDetailsView.findViewById(R.id.detailsRatingText)).setText(movieModel.getVoteAverage() + "");
        ((TextView) fragmentMovieDetailsView.findViewById(R.id.detailsMovieOverview)).setText(movieModel.getOverview());
        ImageView imageView = (ImageView) fragmentMovieDetailsView.findViewById(R.id.detailsMoviePoster);
        Picasso.with(getContext()).load(movieModel.getPosterPath()).into(imageView);

        if (position != -1) {
            Cursor alreadyExists = getContext().getContentResolver().query(MovieDBContract.FavoritesTable.CONTENT_URI, null,
                    MovieDBContract.FavoritesTable.MOVIE_ID + " = ?", new String[]{movieModel.getMovieId() + ""}, null);

            favoriteButton = ((Button) fragmentMovieDetailsView.findViewById(R.id.detailsAddToFavButton));
            if (alreadyExists != null && alreadyExists.moveToFirst()) {
                inFavorites = true;
                favoriteButton.setText("Delete from favorites");
                alreadyExists.close();
            }

            String trailerGet = String.format(trailersLink, movieModel.getMovieId(), api_key);
            String reviewGet = String.format(reviewsLink, movieModel.getMovieId(), api_key);

            fetchTrailersNReviews = new FetchTrailersNReviews(new FetchTrailersNReviews.Callbacks() {
                @Override
                public void onCallback(List<String> result) {
                    if (getContext() == null)
                        return;

                    try {
                        JSONObject trailerObject = new JSONObject(result.get(0));
                        JSONObject reviewObject = new JSONObject(result.get(1));

                        movieModel.setTrailerLinks(trailerObject);
                        movieModel.setReviews(reviewObject);

                        favoriteButton.setOnClickListener(new FavButtonClickListener(inFavorites));

                        generateTrailersNReviews();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            if(isConnectedToInternet())
                fetchTrailersNReviews.execute(trailerGet, reviewGet);

        } else {
            Button button = ((Button) fragmentMovieDetailsView.findViewById(R.id.detailsAddToFavButton));
            button.setText("Delete from favorites");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().getContentResolver().delete(MovieDBContract.FavoritesTable.CONTENT_URI,
                            MovieDBContract.FavoritesTable.MOVIE_ID + " = ?", new String[]{movieModel.getMovieId() + ""});
                    Toast.makeText(getContext(), "Deleted from favorites", Toast.LENGTH_SHORT).show();

                    if (customAction == null) {
                        //no custom action provided. Take default action.
                        Intent intent = new Intent();
                        intent.putExtra(FAV_NOT_ANYMORE, true);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        customAction.onFavClickCustomAction(MovieDetailsFragment.this);
                    }
                }
            });

            generateTrailersNReviews();

        }

        return fragmentMovieDetailsView;
    }

    private void generateTrailersNReviews() {
        trailerLinks = movieModel.getYoutubeTrailerLinks();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        String trail = "Trailer ";
        if (trailerLinks != null) {
            for (int i = 0; i < trailerLinks.size(); i++) {
                View v = layoutInflater.inflate(R.layout.trailer_element, detailsBaseLayout, false);

                ((TextView) v.findViewById(R.id.trailerTitle)).setText(trail + (i + 1));

                ((ImageView) v.findViewById(R.id.playTrailerButton)).setOnClickListener(new TrailerClickListener(trailerLinks.get(i)));

                detailsBaseLayout.addView(v);
            }
        } else {
            Log.i("MovieDetailsFragment", "trailerList was null");
        }


        layoutInflater.inflate(R.layout.reviews_title, detailsBaseLayout);

        List<Review> reviewList = movieModel.getReviewList();

        if (reviewList != null) {
            for (Review r : reviewList) {
                View v = layoutInflater.inflate(R.layout.review_element, detailsBaseLayout, false);

                ((TextView) v.findViewById(R.id.reviewerName)).setText(r.getAuthor());
                ((TextView) v.findViewById(R.id.reviewContents)).setText(r.getContent());

                detailsBaseLayout.addView(v);
            }
        } else {
            Log.i("MovieDetailsFragment", "reviewList was null");
        }
    }


    /**
     * @param index the POSITION of the clicked view IN THE RECYLER VIEW
     * @return
     */
    public static MovieDetailsFragment getInstance(int index) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DETAILS_FRAG_INDEX_KEY, index);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static MovieDetailsFragment getInstance(MovieModel movieModel) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_PARCEL, movieModel);
        fragment.setArguments(bundle);
        //fragment.movieModel = movieModel; Can I just do this instead? :\
        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareMovieMenuItem:
                if (movieModel.getTrailerLinks() != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, movieModel.toString());
                    startActivity(Intent.createChooser(intent, "Share to"));
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }


    private class TrailerClickListener implements View.OnClickListener {
        private String link;

        public TrailerClickListener(String link) {
            this.link = link;
        }

        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }


    private class FavButtonClickListener implements View.OnClickListener{
        private boolean isFavorite;

        public FavButtonClickListener(boolean isFavorite){
            this.isFavorite = isFavorite;
        }

        @Override
        public void onClick(View v) {
            if (isFavorite) {
                getContext().getContentResolver().delete(MovieDBContract.FavoritesTable.CONTENT_URI,
                        MovieDBContract.FavoritesTable.MOVIE_ID + " = ?", new String[]{movieModel.getMovieId() + ""});
                Toast.makeText(getContext(), "Deleted from favorites", Toast.LENGTH_SHORT).show();
                favoriteButton.setText("Add to favorites");
                isFavorite = false;
            } else {
                ContentValues values = movieModel.getMovieContentValues();
                getContext().getContentResolver().insert(MovieDBContract.FavoritesTable.CONTENT_URI, values);
                Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                favoriteButton.setText("Delete from favorites");
                isFavorite = true;
            }
        }
    }

    /**
     * Used to specify what should happen when the favorite button is pressed
     */
    public interface OnFavClickCustomAction{
        /**
         * Remember - the button will always be "Delete from favorites" is this is used only in favorites view. So, think only about implementing that
         * @param movieDetailsFragment - the MovieDetailsFragment hosting the button
         */
        void onFavClickCustomAction(MovieDetailsFragment movieDetailsFragment);
    }

    public void setCustomAction(OnFavClickCustomAction customAction){
        this.customAction = customAction;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.isConnectedOrConnecting();
        }

        return false;
    }

}
