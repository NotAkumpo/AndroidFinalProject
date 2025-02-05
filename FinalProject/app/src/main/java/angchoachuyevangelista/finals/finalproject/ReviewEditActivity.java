package angchoachuyevangelista.finals.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ReviewEditActivity extends AppCompatActivity {

    TextView profnameLabelRE;
    TextView classLabelRE;
    ImageView professorImageRE;
    EditText reviewInputRE;
    EditText ratingInputRE;
    Button saveButtonRE;
    Button cancelButtonRE;
    SharedPreferences prefs;
    Realm realm;
    Review currentReview;
    Professor currentProf;
    Double oldRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        String reviewUuid = prefs.getString("reviewUuid", null);
        String profUuid = prefs.getString("profUuid", null);

        realm = Realm.getDefaultInstance();

        currentReview = realm.where(Review.class)
                .equalTo("uuid", reviewUuid)
                .findFirst();

        currentProf = realm.where(Professor.class)
                .equalTo("uuid", profUuid)
                .findFirst();

        profnameLabelRE = findViewById(R.id.profnameLabelRE);
        profnameLabelRE.setText(currentReview.getProfessorName());

        classLabelRE = findViewById(R.id.classLabelRE);
        classLabelRE.setText(currentReview.getProfessorClass());

        File getImageDir = getExternalCacheDir();
        professorImageRE = findViewById(R.id.professorImageRE);

        File file = new File(getImageDir, currentProf.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(professorImageRE);
        }
        else {
            professorImageRE.setImageResource(R.drawable.profile_pic);
        }

        reviewInputRE = findViewById(R.id.reviewInputRE);
        reviewInputRE.setText(currentReview.getAssessment());

        ratingInputRE = findViewById((R.id.ratingInputRE));
        ratingInputRE.setText(Double.toString(currentReview.getOverallRating()));

        oldRating = currentReview.getOverallRating();

        saveButtonRE = findViewById(R.id.saveButtonRE);
        saveButtonRE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }
        });

        cancelButtonRE = findViewById(R.id.cancelButtonRE);
        cancelButtonRE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });


    }

    public void saveEdit()
    {
        String newReview = reviewInputRE.getText().toString();
        String newRating = ratingInputRE.getText().toString();

        if(newReview.isEmpty() || newRating.isEmpty()){
            blank();
        }
        else
        {
            try {
                Double overallRating = Double.parseDouble(newRating);
                if(overallRating <= 10 && overallRating >= 0) {
                    overallRating = Math.round(overallRating * 10.0) / 10.0;
                    realm.beginTransaction();
                    currentReview.setAssessment(newReview);
                    currentReview.setOverallRating(overallRating);
                    currentProf.editRating(oldRating, overallRating);
                    realm.commitTransaction();

                    Toast toast = Toast.makeText(this, "Review edited", Toast.LENGTH_LONG);
                    toast.show();

                    finish();
                }
                else {
                    Toast toast = Toast.makeText(this, "Max rating is 10 and min rating is 0.", Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (Exception e) {
                Toast t = Toast.makeText(this, "Please input a valid overall rating.", Toast.LENGTH_LONG);
                t.show();
            }
        }
    }
    public void blank(){
        Toast toast = Toast.makeText(this, "Field/s must not be left blank", Toast.LENGTH_LONG);
        toast.show();
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