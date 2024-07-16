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

public class ProfessorEditActivity extends AppCompatActivity {

    Button cancelButtonPE;
    Button saveButtonPE;
    EditText firstnameInputPE;
    EditText lastnameInputPE;
    EditText classInputPE;
    SharedPreferences prefs;
    Realm realm;
    ImageView professorImagePE;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professor_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkPermissions();

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("profUuid", null);
        firstnameInputPE = findViewById(R.id.firstnameInputPE);
        lastnameInputPE = findViewById(R.id.lastnameInputPE);
        classInputPE = findViewById(R.id.classInputPE);

        cancelButtonPE = findViewById(R.id.cancelButtonPE);
        saveButtonPE = findViewById(R.id.saveButtonPE);
        professorImagePE = findViewById(R.id.professorImagePE);


        realm = Realm.getDefaultInstance();

        Professor currentProf = realm.where(Professor.class)
                .equalTo("uuid", uuid)
                .findFirst();
        firstnameInputPE.setText(currentProf.getFirstName());
        lastnameInputPE.setText(currentProf.getLastName());
        classInputPE.setText(currentProf.getClassTeaching());

        File getImageDir = getExternalCacheDir();
        File file = new File(getImageDir, currentProf.getPath());

        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(professorImagePE);
        } else {
            professorImagePE.setImageResource(R.mipmap.ic_launcher);
        }

        professorImagePE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button();
            }
        });
        saveButtonPE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstnameInputPE.getText().toString();
                String lastName = lastnameInputPE.getText().toString();
                String classTeaching = classInputPE.getText().toString();
                Professor existingProf = realm.where(Professor.class)
                        .equalTo("firstName", firstName)
                        .equalTo("lastName", lastName)
                        .equalTo("classTeaching", classTeaching)
                        .findFirst();
                if ((firstName == null || firstName.isEmpty()) || (lastName == null || lastName.isEmpty()) || (classTeaching == null || classTeaching.isEmpty())) {
                    blank();
                } else if (existingProf != null && !existingProf.getFirstName().equals(currentProf.getFirstName()) && !existingProf.getLastName().equals(currentProf.getLastName())
                        && !existingProf.getClassTeaching().equals(currentProf.getClassTeaching())) {
                    exists();
                } else {
                    realm.beginTransaction();
                    assert currentProf != null;
                    currentProf.setFirstName(firstName);
                    currentProf.setLastName(lastName);
                    currentProf.setClassTeaching(classTeaching);

                    if(path != null) {
                        currentProf.setPath(path + ".jpeg");
                    }

                    realm.copyToRealmOrUpdate(currentProf);
                    realm.commitTransaction();

                    editProf();
                    finish();
                }
            }
        });
        cancelButtonPE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    public void checkPermissions()
    {
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
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

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }

    public void editProf(){
        Toast toast = Toast.makeText(this, "Edited", Toast.LENGTH_LONG);
        toast.show();
    }

    public void exists() {
        Toast toast = Toast.makeText(this, "Professor in this class already exists", Toast.LENGTH_LONG);
        toast.show();
    }
    public void goBack(){
        finish();
    }
    public void blank(){
        Toast toast = Toast.makeText(this, "Field/s must not be left blank", Toast.LENGTH_LONG);
        toast.show();
    }

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;
    public void button(){
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
                            .into(professorImagePE);
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

}