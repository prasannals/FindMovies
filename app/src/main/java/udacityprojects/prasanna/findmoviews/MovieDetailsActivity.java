package udacityprojects.prasanna.findmoviews;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import udacityprojects.prasanna.findmoviews.model.MovieModel;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String DETAILS_FRAG_TAG = "udacityprojects.prasanna.findmoviews_DETAILS_FRAG_TAG";
    private static final String DETAILS_FAV_FRAG_TAG = "udacityprojects.prasanna.findmoviews_DETAILS_FAV_FRAG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle extras = getIntent().getExtras();
        int index = extras.getInt(MovieListFragment.EXTRA_POSITION_KEY, -1);

        //if favorites are being displayed, there'll be no EXTRA_POSITION_KEY entry.
        // if index == -1 then fav being displayed
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (index == -1) {
            //fav being displayed. There is a parcelable to send!
            MovieModel movieModel = extras.getParcelable(MovieListFragment.MOVIE_PARCELABLE_KEY);

            if (fragmentManager.findFragmentByTag(DETAILS_FAV_FRAG_TAG) == null)
                fragmentManager.beginTransaction().add(R.id.movieDetailsFragmentContainer,
                        MovieDetailsFragment.getInstance(movieModel), DETAILS_FAV_FRAG_TAG).commit();
        } else {
            //to avoid duplicate fragments being added
            if (fragmentManager.findFragmentByTag(DETAILS_FRAG_TAG) == null)
                fragmentManager.beginTransaction().add(R.id.movieDetailsFragmentContainer,
                        MovieDetailsFragment.getInstance(index), DETAILS_FRAG_TAG).commit();
        }
    }
}
