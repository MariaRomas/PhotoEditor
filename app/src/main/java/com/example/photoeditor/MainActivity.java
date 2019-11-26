package com.example.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    /*Переменная хранящая путь к загружаемым фотографиям*/
    static String strSDCardPathName = Environment.getExternalStorageDirectory() + "/temp_picture" + "/";
    String mCurrentPhotoPath;
    String currentImageName;
    private Uri outputFileUri;

    //
    private static final int REQUEST_PERMISSIONS = 111;
    //Разрешения для записи и чтения
    private static final String[] PERMISSONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Существует ли камера
        if(!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            findViewById(R.id.btnTakePhoto).setVisibility(View.GONE);
        }

        TextView txtView = findViewById(R.id.text);
        txtView.setText(strSDCardPathName);
        createFolder();

        final Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        final Button btnSelectImage = findViewById(R.id.btnSelectImage);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Переход в режим камеры*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) { }

                outputFileUri = Uri.fromFile(photoFile);
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(takePictureIntent, 1);

                }

                //Toast.makeText(MainActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT);

            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                intent.putExtra("imageUri", outputFileUri.toString());
                startActivity(intent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(strSDCardPathName);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        this.mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    /*
    Создать папку на телефоне
    */
    public static void createFolder() {
        File folder = new File(strSDCardPathName);
        try {
            if (!folder.exists()) {
                folder.mkdir();
             }
        } catch (Exception ex) {
        }
    }

    @SuppressLint("NewApi")
    private boolean requirePermissions(){
        for(int i = 0; i<PERMISSONS.length; i++){
            if(checkSelfPermission(PERMISSONS[i]) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requirePermissions()){
            requestPermissions(PERMISSONS, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode == REQUEST_PERMISSIONS && grantResults.length > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
             if(requirePermissions()){
                 ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                 recreate();
             }
         }
    }

    private void init(){

    }
}
