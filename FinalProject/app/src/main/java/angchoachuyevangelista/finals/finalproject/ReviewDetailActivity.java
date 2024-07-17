package angchoachuyevangelista.finals.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class ReviewDetailActivity extends AppCompatActivity {

    TextView userLabelRD;
    TextView profnameLabelRD;
    TextView classLabelRD;
    ImageView professorImageRD;
    TextView reviewDetailRD;
    TextView ratingDisplay;
    Button returnButtonRD;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        String reviewUuid = prefs.getString("reviewUuid", null);

        realm = Realm.getDefaultInstance();

        Review currentReview = realm.where(Review.class)
                .equalTo("uuid", reviewUuid)
                .findFirst();

        userLabelRD = findViewById(R.id.userLabelRD);
        userLabelRD.setText(currentReview.getAdderUsername() + "'s Review");

        profnameLabelRD = findViewById(R.id.profnameLabelRD);
        profnameLabelRD.setText(currentReview.getProfessorName());

        classLabelRD = findViewById(R.id.classLabelRD);
        classLabelRD.setText(currentReview.getProfessorClass());

        reviewDetailRD = findViewById(R.id.reviewDetailRD);
        reviewDetailRD.setText(currentReview.getAssessment());

        ratingDisplay = findViewById(R.id.ratingdisplayRD);
        ratingDisplay.setText(Double.toString(currentReview.getOverallRating()));

        File getImageDir = getExternalCacheDir();
        professorImageRD = findViewById(R.id.professorImageRD);

        File file = new File(getImageDir, currentReview.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(professorImageRD);
        }
        else {
            professorImageRD.setImageResource(R.drawable.profile_pic);
        }

        returnButtonRD = findViewById(R.id.returnButtonRD);
        returnButtonRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    public void goBack() { finish(); }

    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }
}