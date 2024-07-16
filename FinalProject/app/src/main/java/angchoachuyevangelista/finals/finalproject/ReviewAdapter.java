package angchoachuyevangelista.finals.finalproject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ReviewAdapter extends RealmRecyclerViewAdapter<Review, ReviewAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView reviewerLabel;
        TextView ratingLabel;
        ImageButton deleteButton;
        ImageButton editButton;
        ImageButton searchButton;
        ImageView reviewerImage;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            reviewerLabel = itemView.findViewById(R.id.reviewernameLabelREL);
            ratingLabel = itemView.findViewById(R.id.ratingLabelREL);

            deleteButton = itemView.findViewById(R.id.deleteButtonREL);
            editButton = itemView.findViewById(R.id.editButtonREL);
            searchButton = itemView.findViewById(R.id.searchButtonREL);

            reviewerImage = itemView.findViewById(R.id.reviewerImageREL);
        }

    }

    ReviewListActivity activity;

    public ReviewAdapter(ReviewListActivity activity, @Nullable OrderedRealmCollection<Review> data, boolean autoUpdate){
        super(data, autoUpdate);

        this.activity = activity;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = activity.getLayoutInflater().inflate(R.layout.review_layout, parent, false);

        ReviewAdapter.ViewHolder vh = new ReviewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position){

        Review r = getItem(position);

        holder.reviewerLabel.setText(r.getAdderUsername());

    }



}
