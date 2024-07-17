package angchoachuyevangelista.finals.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ReviewAdapter extends RealmRecyclerViewAdapter<Review, ReviewAdapter.ViewHolder> {

    SharedPreferences prefs;
    String currentUuid;
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView reviewerLabel;
        TextView ratingLabel;
        ImageButton deleteButton;
        ImageButton editButton;
        ImageButton searchButton;
        ImageView reviewerImage;
        AlertDialog.Builder builder;

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
        prefs = activity.getSharedPreferences("myPrefs", MODE_PRIVATE);
        currentUuid = prefs.getString("uuid", null);

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

        File getImageDir = activity.getExternalCacheDir();
        File file = new File(getImageDir, r.getPath());

        String rating;
        if (r.getOverallRating() != null) {
            rating = r.getOverallRating().toString();
        } else {
            rating = "-";
        }
        String ratingPhrase = "Overall Rating: " + rating + " /10";
        holder.ratingLabel.setText(ratingPhrase);

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.reviewerImage);
        }
        else {
            holder.reviewerImage.setImageResource(R.drawable.profile_pic);
        }

        if(r.getAdderUuid().equals(currentUuid)){
        }
        else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
        }

        holder.builder = new AlertDialog.Builder(activity);
        holder.deleteButton.setTag(r);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.builder.setTitle("Caution: ")
                        .setMessage("Are you sure you want to delete this review?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.delete((Review) v.getTag());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();


            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {activity.editReview(r.getUuid());}
        });

        holder.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openReview(r.getUuid());
            }
        });

    }



}
