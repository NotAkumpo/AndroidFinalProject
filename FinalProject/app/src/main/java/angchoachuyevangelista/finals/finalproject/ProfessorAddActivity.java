package angchoachuyevangelista.finals.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;

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
import io.realm.Sort;

public class ProfessorAddActivity extends AppCompatActivity {

    Button cancelButtonPA;
    Button saveButtonPA;
    EditText firstnameInputPA;
    EditText lastnameInputPA;
    EditText classInputPA;
    SharedPreferences prefs;
    Realm realm;
    ImageView professorImagePA;
    private String path;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professor_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        firstnameInputPA = findViewById(R.id.firstnameInputPA);
        lastnameInputPA = findViewById(R.id.lastnameInputPA);
        classInputPA = findViewById(R.id.classInputPA);

        cancelButtonPA = findViewById(R.id.cancelButtonPA);
        saveButtonPA = findViewById(R.id.saveButtonPA);
        professorImagePA = findViewById(R.id.professorImagePA);

        uuid = prefs.getString("uuid", null);

        realm = Realm.getDefaultInstance();

        professorImagePA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button();
            }
        });

        saveButtonPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstnameInputPA.getText().toString();
                String lastName = lastnameInputPA.getText().toString();
                String classTeaching = classInputPA.getText().toString();
                Professor existingProf = realm.where(Professor.class)
                        .equalTo("firstName", firstName)
                        .equalTo("lastName", lastName)
                        .equalTo("classTeaching", classTeaching)
                        .findFirst();
                if ((firstName == null || firstName.isEmpty()) || (lastName == null || lastName.isEmpty()) || (classTeaching == null || classTeaching.isEmpty())) {
                    blank();
                } else if (existingProf != null){
                    exists();
                } else {
                    addProfessor();
                }
            }
        });
        cancelButtonPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }

    public void addProfessor(){
        String text = firstnameInputPA.getText().toString();
        String text2 = lastnameInputPA.getText().toString();
        String text3 = classInputPA.getText().toString();

        Professor newProfessor =  new Professor();
        newProfessor.setFirstName(text);
        newProfessor.setLastName(text2);
        newProfessor.setClassTeaching(text3);
        newProfessor.setPath(path+".jpeg");
        newProfessor.setAdderUuid(uuid);

        long count = 0;

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newProfessor);
            realm.commitTransaction();

            count = realm.where(Professor.class).count();

            Toast t = Toast.makeText(this, "New Professor saved. Total: "+count, Toast.LENGTH_LONG);
            t.show();
        }

        catch(Exception e){
            Toast t = Toast.makeText(this, "Error saving", Toast.LENGTH_LONG);
            t.show();
        }

        finish();
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
                            .into(professorImagePA);
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