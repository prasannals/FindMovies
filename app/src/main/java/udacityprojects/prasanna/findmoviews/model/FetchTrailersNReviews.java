package udacityprojects.prasanna.findmoviews.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.BaseColumns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Prasanna Lakkur Subramanyam on 1/14/2016.
 */
public class FetchTrailersNReviews extends AsyncTask<String, Void, List<String>>{
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Callbacks mCallbacks;

    public FetchTrailersNReviews(Callbacks callbacks){
        mCallbacks = callbacks;
    }

    public String getURLContents(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }


    @Override
    protected List<String> doInBackground(String... params) {
        List<String> result = new ArrayList<>();

        try {
            result.add( getURLContents(params[0]) );
            result.add( getURLContents(params[1]) );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        mCallbacks.onCallback(strings);
    }

    public interface Callbacks {
        void onCallback(List<String> result);
    }
}
