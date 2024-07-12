package angchoachuyevangelista.finals.finalproject;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class RegisterActivity extends AppCompatActivity {

    Button saveButton;
    Button cancelButton;
    EditText usernameInputR;
    EditText passwordInputR;
    EditText confirmInput;
    ImageView imageViewR;
    private String path;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        checkPermissions();

        usernameInputR = findViewById(R.id.firstnameInputPA);
        passwordInputR = findViewById(R.id.lastnameInputPA);
        confirmInput = findViewById(R.id.classInputPA);

        saveButton = findViewById(R.id.saveButtonPA);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        cancelButton = findViewById(R.id.cancelButtonPA);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRegister();
            }
        });

        realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewR = findViewById(R.id.professorImagePA);
        imageViewR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
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


    public void registerUser(){
        String username = usernameInputR.getText().toString();
        String password = passwordInputR.getText().toString();
        String confirm = confirmInput.getText().toString();
        User exists = realm.where(User.class)
                .equalTo("name", username)
                .findFirst();

        if (username.isBlank()) {
            Toast toast = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            if (password.isBlank()) {
                Toast toast = Toast.makeText(this, "Password must not be blank", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                if (password.equals(confirm)) {
                    if(exists != null) {
                        Toast toast = Toast.makeText(this, "User already exists", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {
                        User u = new User();
                        u.setName(username);
                        u.setPassword(password);
                        u.setPath(path+".jpeg");

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(u);
                        realm.commitTransaction();

                        Toast toast = Toast.makeText(this, "New User saved. Total: " + realm.where(User.class).count(), Toast.LENGTH_LONG);
                        toast.show();

                        finish();
                    }
                }
                else {
                    Toast toast = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    public void cancelRegister(){
        finish();
    }


    public static int REQUEST_CODE_IMAGE_SCREEN = 0;
    public void openCamera(){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_SCREEN);
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    path = String.valueOf(System.currentTimeMillis());

                    File savedImage = saveFile(jpeg, path+".jpeg");

                    Picasso.get()
                            .load(savedImage)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(imageViewR);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }


    private File saveFile(byte[] jpeg, String filename) throws IOException
    {
        File getImageDir = getExternalCacheDir();

        File savedImage = new File(getImageDir, filename);


        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }


    public void onDestroy(){
        super.onDestroy();

        if(!realm.isClosed()){
            realm.close();
        }
    }

}