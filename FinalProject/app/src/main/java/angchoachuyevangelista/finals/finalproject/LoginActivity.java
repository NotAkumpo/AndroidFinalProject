package angchoachuyevangelista.finals.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import io.realm.Realm;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {

    Button adminButton;
    Button signinButton;
    SharedPreferences prefs;
    EditText usernameInputL;
    EditText passwordInputL;
    CheckBox rememberMe;
    Button clearButton;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        checkPermissions();

        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinUser();
            }
        });

        adminButton = findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdmin();
            }
        });

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();

        usernameInputL = findViewById(R.id.usernameInputL);
        passwordInputL = findViewById(R.id.passwordInputL);
        rememberMe = findViewById(R.id.rememberMe);

        boolean remembered = prefs.getBoolean("remembered", false);
        String currentUuid = prefs.getString("uuid", null);

        if (remembered){
            User rememberedUser = realm.where(User.class)
                    .equalTo("uuid", currentUuid)
                    .findFirst();
            String rememberedName = rememberedUser.getName();
            String rememberedPassword = rememberedUser.getPassword();

            usernameInputL.setText(rememberedName);
            passwordInputL.setText(rememberedPassword);
        }
        else {}

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {}
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();

    }


    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }


    public void openAdmin(){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void signinUser(){
        String attemptedUsername = usernameInputL.getText().toString();
        String attemptedPassword = passwordInputL.getText().toString();
        User existingUser = realm.where(User.class)
                .equalTo("name", attemptedUsername)
                .findFirst();

        if (existingUser == null){
            Toast toast = Toast.makeText(this, "No User found", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            String existingPassword = existingUser.getPassword();
            if (attemptedPassword.equals(existingPassword)){

                SharedPreferences.Editor edit = prefs.edit();
                String uuid = existingUser.getUuid();
                edit.putString("uuid", uuid);

                if (rememberMe.isChecked()){
                    edit.putBoolean("remembered", true);
                    edit.apply();
                }
                else {
                    usernameInputL.setText("");
                    passwordInputL.setText("");
                    edit.putBoolean("remembered", false);
                    edit.apply();
                }
                Intent intent = new Intent(this, ProfessorListActivity.class);
                startActivity(intent);
            }
            else {
                Toast toast = Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void clearData(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();

        Toast toast = Toast.makeText(this, "Preferences cleared", Toast.LENGTH_LONG);
        toast.show();
    }

    public void onDestroy(){
        super.onDestroy();

        if(!realm.isClosed()){
            realm.close();
        }
    }

}