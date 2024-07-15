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

public class ProfessorAdapter extends RealmRecyclerViewAdapter<Professor, ProfessorAdapter.ViewHolder> {

    SharedPreferences prefs;
    String currentUuid;
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView profnameLabel;
        TextView classLabel;
        ImageButton searchButton;
        ImageButton deleteButton;
        ImageButton editButton;
        ImageView professorImage;
        AlertDialog.Builder builder;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            profnameLabel = itemView.findViewById(R.id.profnameLabelPL);
            classLabel = itemView.findViewById(R.id.classLabelPL);

            searchButton = itemView.findViewById(R.id.searchButtonPL);
            deleteButton = itemView.findViewById(R.id.deleteButtonPL);
            editButton = itemView.findViewById(R.id.editButtonPL);

            professorImage = itemView.findViewById(R.id.professorImagePL);
        }

    }


    ProfessorListActivity activity;

    public ProfessorAdapter(ProfessorListActivity activity, @Nullable OrderedRealmCollection<Professor> data, boolean autoUpdate){
        super(data, autoUpdate);

        this.activity = activity;
        prefs = activity.getSharedPreferences("myPrefs", MODE_PRIVATE);
        currentUuid = prefs.getString("uuid", null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = activity.getLayoutInflater().inflate(R.layout.professor_layout, parent, false);

        ProfessorAdapter.ViewHolder vh = new ProfessorAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

        Professor p = getItem(position);

        String name = p.getFirstName() + " " + p.getLastName();

        holder.profnameLabel.setText(name);
        holder.classLabel.setText(p.getClassTeaching());

        File getImageDir = activity.getExternalCacheDir();
        File file = new File(getImageDir, p.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.professorImage);
        }
        else {
            holder.professorImage.setImageResource(R.drawable.profile_pic);
        }

        if(p.getAdderUuid().equals(currentUuid)){
        }
        else{
            holder.deleteButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
        }

        holder.builder = new AlertDialog.Builder(activity);
        holder.deleteButton.setTag(p);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.builder.setTitle("Caution: ")
                        .setMessage("Are you sure you want to delete this professor?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.delete((Professor) v.getTag());
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
            public void onClick(View v) {
                activity.openEdit(p.getUuid());
            }
        });

        holder.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openReviews(p.getUuid());
            }
        });

    }




}

