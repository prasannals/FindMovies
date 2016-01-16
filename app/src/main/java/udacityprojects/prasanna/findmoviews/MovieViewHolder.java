package udacityprojects.prasanna.findmoviews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * View holder for displaying movies
 */
public class MovieViewHolder extends RecyclerView.ViewHolder{
    private ImageView mImageView;

    public MovieViewHolder(View itemView) {
        super(itemView);
        mImageView = (ImageView) this.itemView.findViewById(R.id.reyclerElementImageView);
    }

    public ImageView getImageView() {
        return mImageView;
    }
}
