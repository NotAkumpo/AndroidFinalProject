package angchoachuyevangelista.finals.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditActivity extends AppCompatActivity {


    Button saveButtonE;
    Button cancelButtonE;
    EditText usernameInputE;
    EditText passwordInputE;
    EditText confirmInputE;
    SharedPreferences prefs;
    ImageView imageViewE;
    private String path;
    Realm realm;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        checkPermissions();

        usernameInputE = findViewById(R.id.usernameInputE);
        passwordInputE = findViewById(R.id.passwordInputE);
        confirmInputE = findViewById(R.id.confirmInputE);

        saveButtonE = findViewById(R.id.saveButtonE);
        saveButtonE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        cancelButtonE = findViewById(R.id.cancelButtonE);
        cancelButtonE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEdit();
            }
        });

        imageViewE = findViewById(R.id.imageViewE);
        imageViewE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("uuidE", null);

        realm = Realm.getDefaultInstance();

        currentUser = realm.where(User.class)
                .equalTo("uuid", uuid)
                .findFirst();

        usernameInputE.setText(currentUser.getName());
        passwordInputE.setText(currentUser.getPassword());
        confirmInputE.setText(currentUser.getPassword());

        File getImageDir = getExternalCacheDir();
        File file = new File(getImageDir, currentUser.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageViewE);
        }
        else {
            imageViewE.setImageResource(R.mipmap.ic_launcher);
        }

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


    public void saveUser(){
        String username = usernameInputE.getText().toString();
        String password = passwordInputE.getText().toString();
        String confirm = confirmInputE.getText().toString();

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
                    if(exists != null && !exists.getName().equals(currentUser.getName())) {
                        Toast toast = Toast.makeText(this, "User already exists", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {

                        realm.beginTransaction();

                        currentUser.setName(username);
                        currentUser.setPassword(password);

                        if(path != null){
                            currentUser.setPath(path + ".jpeg");
                        }

                        realm.copyToRealmOrUpdate(currentUser);
                        realm.commitTransaction();

                        Toast toast = Toast.makeText(this, "Changes saved", Toast.LENGTH_LONG);
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

    public void cancelEdit(){
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
                            .into(imageViewE);

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