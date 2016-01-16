package udacityprojects.prasanna.findmoviews;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MovieListActivity extends AppCompatActivity {

    private static final String MOVIE_LIST_FRAG_TAG = "udacityprojects.prasanna.findmoviews_MOVIE_LIST_FRAG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);


        FragmentManager manager = getSupportFragmentManager();

        Fragment fragment = manager.findFragmentByTag(MOVIE_LIST_FRAG_TAG);

        if (fragment == null) {
            manager.beginTransaction().add(R.id.movieListFragmentParent, new MovieListFragment(), MOVIE_LIST_FRAG_TAG).commit();
        }

    }


    private boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.isConnectedOrConnecting();
        }

        return false;
    }
}
