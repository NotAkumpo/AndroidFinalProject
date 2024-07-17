package angchoachuyevangelista.finals.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReviewListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView profnameLabel;
    TextView classLabel;
    TextView ratingLabel;
    ImageView userImage;
    TextView usernameLabel;
    Button addButton;
    Button returnButton;
    ImageView profImage;
    SharedPreferences prefs;
    Realm realm;
    User currentUser;
    Professor currentProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_list);

        recyclerView = findViewById(R.id.recyclerViewRL);

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        realm = Realm.getDefaultInstance();

        String userUuid = prefs.getString("uuid", null);

        currentUser = realm.where(User.class)
                .equalTo("uuid", userUuid)
                .findFirst();

        String profUuid = prefs.getString("profUuid", null);

        currentProf = realm.where(Professor.class)
                .equalTo("uuid", profUuid)
                .findFirst();

        profnameLabel = findViewById(R.id.profnameLabelRL);
        String profName = currentProf.getFirstName()+ " " + currentProf.getLastName();
        profnameLabel.setText(profName);

        classLabel = findViewById(R.id.classLabelRL);
        classLabel.setText(currentProf.getClassTeaching());

        ratingLabel = findViewById(R.id.ratingLabelRL);
        String rating;
        if (currentProf.getOverallRating() != null) {
            rating = currentProf.getOverallRating().toString();
        } else {
            rating = "-";
        }
        String ratingPhrase = "Overall Rating: " + rating + " /10";
        ratingLabel.setText(ratingPhrase);

        File getImageDir = getExternalCacheDir();
        profImage = findViewById(R.id.professorImageRL);

        File file = new File(getImageDir, currentProf.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(profImage);
        }
        else {
            profImage.setImageResource(R.drawable.profile_pic);
        }

        usernameLabel = findViewById(R.id.usernameLabelRL);
        usernameLabel.setText(currentUser.getName());

        userImage = findViewById(R.id.userImageRL);
        File file2 = new File(getImageDir, currentUser.getPath());

        if (file2.exists()) {
            Picasso.get()
                    .load(file2)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(userImage);
        }
        else {
            userImage.setImageResource(R.drawable.profile_pic);
        }

        addButton = findViewById(R.id.addReviewButtonRL);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
            }
        });

        returnButton = findViewById(R.id.returnButtonRL);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();
        RealmResults<Review> list = realm.where(Review.class).equalTo("professorUuid", profUuid).findAll();

        ReviewAdapter adapter = new ReviewAdapter(this, list, true);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addReview()
    {
        Intent intent = new Intent(this, ReviewAddActivity.class);
        startActivity(intent);
    }

    public void delete(Review r){
        if (r.isValid())
        {

            realm.beginTransaction();
            r.deleteFromRealm();
            realm.commitTransaction();

            //Make a method here to delete all reviews as well
        }
    }

    public void editReview(String adderUuid, String reviewUuid)
    {
        if(!currentUser.getUuid().equals(adderUuid))
        {
            denyEdit();
        }
        else {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("reviewUuid", reviewUuid);
            edit.apply();

            Intent intent = new Intent(this, ReviewEditActivity.class);
            startActivity(intent);
        }
    }

    public void denyEdit()
    {
        Toast toast = Toast.makeText(this, "Cannot edit other user's review", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void openReview(String uuid)
    {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("reviewUuid", uuid);
        edit.apply();

        Intent intent = new Intent(this, ReviewDetailActivity.class);
        startActivity(intent);
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }


}