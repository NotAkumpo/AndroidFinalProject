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

public class ReviewAddActivity extends AppCompatActivity {

    TextView profnameLabelRA;
    TextView classLabelRA;
    ImageView profImageRA;
    EditText reviewInputRA;
    EditText ratingInputRA;
    Button saveButtonRA;
    Button cancelButtonRA;
    SharedPreferences prefs;
    Realm realm;
    User currentUser;
    Professor currentProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        reviewInputRA = findViewById(R.id.reviewInputRA);
        ratingInputRA = findViewById(R.id.ratingInputRA);

        realm = Realm.getDefaultInstance();

        String userUuid = prefs.getString("uuid", null);

        currentUser = realm.where(User.class)
                .equalTo("uuid", userUuid)
                .findFirst();

        String profUuid = prefs.getString("profUuid", null);

        currentProf = realm.where(Professor.class)
                .equalTo("uuid", profUuid)
                .findFirst();

        profnameLabelRA = findViewById(R.id.profnameLabelRA);
        String profName = currentProf.getFirstName()+ " " + currentProf.getLastName();
        profnameLabelRA.setText(profName);

        classLabelRA = findViewById(R.id.classLabelRA);
        classLabelRA.setText(currentProf.getClassTeaching());

        File getImageDir = getExternalCacheDir();
        profImageRA = findViewById(R.id.professorImageRA);

        File file = new File(getImageDir, currentProf.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(profImageRA);
        }
        else {
            profImageRA.setImageResource(R.drawable.profile_pic);
        }

        saveButtonRA = findViewById(R.id.saveButtonRA);
        saveButtonRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewInput = reviewInputRA.getText().toString();
                String ratingInput = ratingInputRA.getText().toString();

                if (( reviewInput.isEmpty() || ratingInput.isEmpty())){
                    blank();
                } else {
                    saveReview();
                }
            }
        });

        cancelButtonRA = findViewById(R.id.cancelButtonRA);
        cancelButtonRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { goBack();}
        });
    }

    public void blank(){
        Toast toast = Toast.makeText(this, "Field/s must not be left blank", Toast.LENGTH_LONG);
        toast.show();
    }

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }

    public void goBack(){
        finish();
    }

    public void saveReview(){
        String assessment = reviewInputRA.getText().toString();
        String adderUuid = currentUser.getUuid();
        String profUuid = currentProf.getUuid();
        String adderUsername = currentUser.getName();
        Double overallRating = Double.parseDouble(ratingInputRA.getText().toString());
        String profName = currentProf.getFirstName()+ " " + currentProf.getLastName();
        String profClass = currentProf.getClassTeaching();
        String path = currentProf.getPath();

        Review newReview =  new Review();
        newReview.setAssessment(assessment);
        newReview.setAdderUuid(adderUuid);
        newReview.setProfessorUuid(profUuid);
        newReview.setAdderUsername(adderUsername);
        newReview.setOverallRating(overallRating);
        newReview.setProfessorName(profName);
        newReview.setProfessorClass(profClass);
        newReview.setPath(path);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newReview);
            realm.commitTransaction();

            Toast t = Toast.makeText(this, "Review for professor saved.", Toast.LENGTH_LONG);
            t.show();
        }

        catch(Exception e){
            Toast t = Toast.makeText(this, "Error saving", Toast.LENGTH_LONG);
            t.show();
        }

        finish();
    }
}