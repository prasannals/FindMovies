package udacityprojects.prasanna.findmoviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import udacityprojects.prasanna.findmoviews.model.DBDirectDataAccess;
import udacityprojects.prasanna.findmoviews.model.MovieModel;

/**
 * Created by Prasanna Lakkur Subramanyam on 1/12/2016.
 */
public class MovieListFragment extends Fragment {

    public static final String EXTRA_POSITION_KEY = "udacityprojects.prasanna.findmoviews_EXTRA_POSITION_KEY";
    public static final int MOVIE_DETAILS_REQUEST_CODE = 1;
    public static final String MOVIE_PARCELABLE_KEY = "udacityprojects.prasanna.findmoviews_MOVIE_PARCELABLE_KEY";
    private static final int MOVIE_DETAILS_FAV_REQ_CODE = 2;
    private static final String CREDITS_FRAG_TAG = "udacityprojects.prasanna.findmoviews_CREDITS_FRAG_TAG";
    private static final String DETAILS_FRAG_TAG = "udacityprojects.prasanna.findmoviews_TWO_PANE_DETAILS";
    private static final String DETAILS_FAV_FRAG_TAG = "udacityprojects.prasanna.findmoviews_TWO_PANE_FAV_DETAILS";
    private static final String LAST_POSITION = "udacityprojects.prasanna.findmoviews_LAST_POSITION";
    private static final String PREV_WAS_FAVORITES = "udacityprojects.prasanna.findmoviews_PREV_WAS_FAVORITES";
    private static final String PREV_WAS_POPULAR_SORT = "udacityprojects.prasanna.findmoviews_PREV_WAS_POPULAR_SORT";
    private static final String PREV_NUM_IN_SESSION = "udacityprojects.prasanna.findmoviews_PREV_NUM_IN_SESSION";

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private FetchUrlContents mFetchUrlContents;
    private GridLayoutManager mLayoutManager;
    private boolean isPopularitySort = true;
    private String ratingSort, popularitySort;
    private EndlessScrollListener mEndlessScrollListener;
    private FavoritesAdapter mFavoritesAdapter;
    private boolean wasDisplayingFavs;
    private View baseView;
    private boolean prevWasFavorites;
    private int prevPosition;
    private int numElementInSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ratingSort = getContext().getResources().getString(R.string.sort_by_rating, getContext().getResources().getString(R.string.api_key), 1);
        popularitySort = getContext().getResources().getString(R.string.sort_by_popularity, getContext().getResources().getString(R.string.api_key), 1);
//        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            prevPosition = savedInstanceState.getInt(LAST_POSITION);

            numElementInSession = savedInstanceState.getInt(PREV_NUM_IN_SESSION);
            prevWasFavorites = savedInstanceState.getBoolean(PREV_WAS_FAVORITES);
            isPopularitySort = savedInstanceState.getBoolean(PREV_WAS_POPULAR_SORT);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLayoutManager != null)
            outState.putInt(LAST_POSITION, mLayoutManager.findFirstVisibleItemPosition());

        outState.putBoolean(PREV_WAS_FAVORITES, wasDisplayingFavs);
        outState.putBoolean(PREV_WAS_POPULAR_SORT, isPopularitySort);
        if(mAdapter != null) {
            //rating or popularity sort being displayed.
            outState.putInt(PREV_NUM_IN_SESSION, mAdapter.getDBDirectDataAccess().getNumInsertedInSession());
        }else{
            outState.putInt(PREV_NUM_IN_SESSION, 0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByPopularityMenuItem:
                if (isPopularitySort == false || wasDisplayingFavs == true) {
                    if (getActivity().findViewById(R.id.movieDetailsFragmentContainer) != null) {
                        //removing the right pane in the two pane layout
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        if (manager.findFragmentByTag(DETAILS_FRAG_TAG) != null) {
                            manager.beginTransaction().remove(manager.findFragmentByTag(DETAILS_FRAG_TAG)).commit();
                        }
                        if (manager.findFragmentByTag(DETAILS_FAV_FRAG_TAG) != null) {
                            manager.beginTransaction().remove(manager.findFragmentByTag(DETAILS_FAV_FRAG_TAG)).commit();
                        }
                    }
                    loadMoviesIntoViews(popularitySort, true);
                    isPopularitySort = true;
                }
                return true;

            case R.id.sortByRatingMenuItem:
                if (isPopularitySort == true || wasDisplayingFavs == true) {
                    if (getActivity().findViewById(R.id.movieDetailsFragmentContainer) != null) {
                        //removing the right pane in the two pane layout
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        if (manager.findFragmentByTag(DETAILS_FRAG_TAG) != null) {
                            manager.beginTransaction().remove(manager.findFragmentByTag(DETAILS_FRAG_TAG)).commit();
                        }
                        if (manager.findFragmentByTag(DETAILS_FAV_FRAG_TAG) != null) {
                            manager.beginTransaction().remove(manager.findFragmentByTag(DETAILS_FAV_FRAG_TAG)).commit();
                        }
                    }
                    loadMoviesIntoViews(ratingSort, true);
                    isPopularitySort = false;
                }
                return true;

            case R.id.showFavoritesMenuItem:
                displayFavorites();
                return true;

            case R.id.creditsMenuItem:
                new CreditsDialog().show(getActivity().getSupportFragmentManager(), CREDITS_FRAG_TAG);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void displayFavorites() {
        if (wasDisplayingFavs == false) {
            if (getActivity().findViewById(R.id.movieDetailsFragmentContainer) != null) {
                //removing the right pane in the two pane layout
                FragmentManager manager = getActivity().getSupportFragmentManager();

                if (manager.findFragmentByTag(DETAILS_FRAG_TAG) != null) {
                    manager.beginTransaction().remove(manager.findFragmentByTag(DETAILS_FRAG_TAG)).commit();
                }
            }
            mFavoritesAdapter = new FavoritesAdapter(getContext(), this);
            if (mRecyclerView == null) {
                //happens when viewing offline
                mRecyclerView = (RecyclerView) baseView.findViewById(R.id.movieListRecyclerView);
                mLayoutManager = new GridLayoutManager(getContext(), 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
            mRecyclerView.setAdapter(mFavoritesAdapter);
            mAdapter = null;
            wasDisplayingFavs = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        baseView = view;

        if (prevWasFavorites) {
            displayFavorites();
        } else {
            if (isConnectedToInternet()) {
                mFavoritesAdapter = null;
                mRecyclerView = (RecyclerView) view.findViewById(R.id.movieListRecyclerView);

                mLayoutManager = new GridLayoutManager(getContext(), 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new MovieAdapter(getContext(), this);
                mRecyclerView.setAdapter(mAdapter);


                if (isPopularitySort) {
                    loadMoviesIntoViews(popularitySort, false);
                } else {
                    loadMoviesIntoViews(ratingSort, false);
                }
            } else {
                Toast.makeText(getContext(), "Not connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }


    private void loadMoviesIntoViews(String query, final boolean fromMenu) {
        if (isConnectedToInternet()) {
            if (wasDisplayingFavs) {
                mAdapter = new MovieAdapter(getContext(), this);
                mRecyclerView.setAdapter(mAdapter);
                wasDisplayingFavs = false;
            }


            if (fromMenu || numElementInSession == 0) {
                //if its from the menu or if the numElementInSession is 0 (which would imply we're running the app for the first time
                //and don't have any data in the database yet), clear the db, fetch the contents and refresh the recycler view
                mAdapter.getDBDirectDataAccess().clearDB(false);

                mFetchUrlContents = new FetchUrlContents(getContext(), new FetchUrlContents.Callbacks() {
                    @Override
                    public void onCallback(String result) {
                        mAdapter.getDBDirectDataAccess().insertMovies(result);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mFetchUrlContents.execute(query);
            }else{
                //if we've reached here, it means that fromMenu == false and numElementInSession != 0 (means that there is already data in
                //the db), set the number of elements in db, refresh the adapter and scroll to the prevPosition
                mAdapter.getDBDirectDataAccess().setNumInsertedInSession(numElementInSession);
                mAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(prevPosition);
            }
            mEndlessScrollListener = new EndlessScrollListener(mLayoutManager, query.substring(0, query.length() - 1));

            mRecyclerView.setOnScrollListener(mEndlessScrollListener);


        } else {
            Toast.makeText(getContext(), "Not connected to the Internet", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.isConnectedOrConnecting();
        }

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MOVIE_DETAILS_REQUEST_CODE:
                //no action required right now. add something later if required
                break;

            case MOVIE_DETAILS_FAV_REQ_CODE:
                //refreshing data
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mFavoritesAdapter.refreshData();
                        mFavoritesAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private final GridLayoutManager mGridLayoutManager;

        private final int threshold = 6;
        private int pages;
        private int numberOfItems = 20;
        private String mQuery;
        private FetchUrlContents fetchUrlContents;

        private boolean loading;

        public EndlessScrollListener(GridLayoutManager gridLayoutManager, String query) {
            mGridLayoutManager = gridLayoutManager;
            pages = 1;
            mQuery = query;

        }

        public void setQuery(String Query) {
            mQuery = Query;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int lastVisibleItemPosition = mGridLayoutManager.findLastVisibleItemPosition();

            //20 views are fetched every time. on reaching the 14th view among those 20, load more
            //totalViews = (20 * pages)
            //  if(totalViews - lastVisibleItemPosition <= threshold) loadMore();

            int totalViews = (numberOfItems * pages);

            if (!loading && (totalViews - lastVisibleItemPosition <= threshold)) {
                pages++;
                loading = true;

                fetchUrlContents = new FetchUrlContents(getContext(), new FetchUrlContents.Callbacks() {
                    @Override
                    public void onCallback(String result) {
                        mAdapter.getDBDirectDataAccess().insertMovies(result);
                        mAdapter.notifyDataSetChanged();
                        loading = false;
                    }
                });
                fetchUrlContents.execute(mQuery + pages);
            }

        }
    }


    public class OnMoviesClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            if (getActivity().findViewById(R.id.movieDetailsFragmentContainer) == null) {
                //no two pane
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(EXTRA_POSITION_KEY, mRecyclerView.getChildLayoutPosition(v));
                startActivityForResult(intent, MOVIE_DETAILS_REQUEST_CODE);
            } else {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                if (manager.findFragmentByTag(DETAILS_FRAG_TAG) == null) {
                    manager.beginTransaction().add(R.id.movieDetailsFragmentContainer,
                            MovieDetailsFragment.getInstance(mRecyclerView.getChildLayoutPosition(v)), DETAILS_FRAG_TAG).commit();
                } else {
                    manager.beginTransaction().replace(R.id.movieDetailsFragmentContainer,
                            MovieDetailsFragment.getInstance(mRecyclerView.getChildLayoutPosition(v)), DETAILS_FRAG_TAG).commit();
                }
            }
        }
    }

    public class OnFavoritesClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (getActivity().findViewById(R.id.movieDetailsFragmentContainer) == null) {
                Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                intent.putExtra(MovieListFragment.MOVIE_PARCELABLE_KEY, mFavoritesAdapter.getMovieAt(mRecyclerView.getChildLayoutPosition(v)));
                startActivityForResult(intent, MOVIE_DETAILS_FAV_REQ_CODE);
            } else {
                MovieDetailsFragment fragment = MovieDetailsFragment.getInstance(
                        mFavoritesAdapter.getMovieAt(mRecyclerView.getChildLayoutPosition(v)));
                fragment.setCustomAction(new MovieDetailsFragment.OnFavClickCustomAction() {
                    @Override
                    public void onFavClickCustomAction(MovieDetailsFragment movieDetailsFragment) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(movieDetailsFragment).commit();
                        mFavoritesAdapter.refreshData();
                        mFavoritesAdapter.notifyDataSetChanged();
                    }
                });

                FragmentManager manager = getActivity().getSupportFragmentManager();
                if (manager.findFragmentByTag(DETAILS_FRAG_TAG) == null && manager.findFragmentByTag(DETAILS_FAV_FRAG_TAG) == null) {
                    manager.beginTransaction().add(R.id.movieDetailsFragmentContainer,
                            fragment, DETAILS_FAV_FRAG_TAG).commit();
                } else {
                    manager.beginTransaction().replace(R.id.movieDetailsFragmentContainer,
                            fragment, DETAILS_FAV_FRAG_TAG).commit();
                }
            }
        }
    }

}
