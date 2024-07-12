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
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProfessorListActivity extends AppCompatActivity {


    RecyclerView recyclerViewPL;
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

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();

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
            userImage.setImageResource(R.mipmap.ic_launcher);
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
        edit.apply();

        finish();
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