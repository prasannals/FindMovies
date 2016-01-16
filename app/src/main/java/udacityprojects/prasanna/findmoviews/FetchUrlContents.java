package udacityprojects.prasanna.findmoviews;


import android.content.Context;
import android.os.AsyncTask;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A general purpose class which fetches the http response of the first parameter passed in.
 */
public class FetchUrlContents extends AsyncTask<String, Void, String> {
    OkHttpClient okHttpClient = new OkHttpClient();
    Callbacks mCallbacks;
    Context mContext;

    public FetchUrlContents(Context context, Callbacks callbacks) {
        mCallbacks = callbacks;
        mContext = context;
    }

    public String getURLContents(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getURLContents(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        mCallbacks.onCallback(s);
    }

    /**
     * The onCallback(String) method will be called onPostExecute.
     */
    public interface Callbacks {
        /**
         *
         * @param result - the response from the http query
         */
        void onCallback(String result);
    }
}
