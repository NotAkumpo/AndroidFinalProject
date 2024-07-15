package angchoachuyevangelista.finals.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ProfessorListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Button addProfButton;
    Button logoutButton;
    ImageView userImage;
    TextView usernameLabel;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professor_list);

        recyclerView = findViewById(R.id.recyclerViewPL);

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        realm = Realm.getDefaultInstance();

        String uuid = prefs.getString("uuid", null);

        User currentUser = realm.where(User.class)
                .equalTo("uuid", uuid)
                .findFirst();

        usernameLabel = findViewById(R.id.usernameLabelPL);
        usernameLabel.setText(currentUser.getName());

        userImage = findViewById(R.id.userImagePL);
        File getImageDir = getExternalCacheDir();
        File file = new File(getImageDir, currentUser.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(userImage);
        }
        else {
            userImage.setImageResource(R.drawable.profile_pic);
        }

        addProfButton = findViewById(R.id.addProfButtonPL);
        addProfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddProf();
            }
        });

        logoutButton = findViewById(R.id.logoutButtonPL);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();
        RealmResults<Professor> list = realm.where(Professor.class).findAll();

        ProfessorAdapter adapter = new ProfessorAdapter(this, list, true);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openAddProf(){
        Intent intent = new Intent(this, ProfessorAddActivity.class);
        startActivity(intent);
    }

    public void logout(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("uuid", null);
        edit.putBoolean("remembered", false);
        edit.apply();

        finish();
    }

    public void openEdit(String uuid){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("profUuid", uuid);
        edit.apply();

        Intent intent = new Intent(this, ProfessorEditActivity.class);
        startActivity(intent);
    }

    public void openReviews(String uuid){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("profUuid", uuid);
        edit.apply();

        Intent intent = new Intent(this, ReviewListActivity.class);
        startActivity(intent);
    }

    public void delete(Professor p){
        if (p.isValid())
        {

            realm.beginTransaction();
            p.deleteFromRealm();
            realm.commitTransaction();

            //Make a method here to delete all reviews as well
        }
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