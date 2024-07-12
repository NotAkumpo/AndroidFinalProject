package angchoachuyevangelista.finals.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class AdminActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Button clearButton;
    Button addButton;
    Realm realm;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.recyclerView);

        clearButton = findViewById(R.id.clearButton2);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUsers();
            }
        });

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();
        RealmResults<User> list = realm.where(User.class).findAll();

        UserAdapter adapter = new UserAdapter(this, list, true);
        recyclerView.setAdapter(adapter);

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    public void clearUsers(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void openEdit(String uuid){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("uuidE", uuid);
        edit.apply();

        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public void openRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void delete(User u)
    {

        if (u.isValid())
        {

            realm.beginTransaction();
            u.deleteFromRealm();
            realm.commitTransaction();
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