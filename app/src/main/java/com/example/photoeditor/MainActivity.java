package com.example.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
    final private static String strSDCardPathName = Environment.getExternalStorageDirectory() + "/temp_picture" + "/";
    private Uri outputFileUri;

    //ID приложения для shared preferences
    private static final String appID = "PhotoEditor";
    //код для запроса разрешений
    private static final int REQUEST_PERMISSIONS = 111;

    //Код возвращаемый активити выбора файлов
    private static final int REQUEST_PICK_IMAGE = 112;

    //Код возвращаемый активити после создания фото
    private static final int REQUEST_IMAGE_CAPTURE = 113;

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
        createFolder();

        final Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        final Button btnSelectImage = findViewById(R.id.btnSelectImage);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Переход в режим камеры*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = createImageFile();
                    outputFileUri = Uri.fromFile(photoFile);

                    SharedPreferences myPrefs = getSharedPreferences(appID, 0);
                    myPrefs.edit().putString("path", photoFile.getAbsolutePath()).apply();
                    //Сделать фото
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }else {
                    Toast.makeText(MainActivity.this, "Произошла ошибка с приложением камеры", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

//                Intent pickIntent = new Intent(Intent.ACTION_PICK);
////                Intent intent = new Intent(Intent.ACTION_PICK);
////                intent.setType("image/*");
////                startActivityForResult(intent, REQUEST_PICK_IMAGE);
//                pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                startActivityForResult(intent, REQUEST_PICK_IMAGE);

//                Intent actIntent = new Intent(MainActivity.this, EditImageActivity.class);
//                actIntent.putExtra("imageUri", outputFileUri.toString());
//                startActivity(actIntent);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            if (outputFileUri == null){
                SharedPreferences myprefs = getSharedPreferences(appID, 0);
                String path = myprefs.getString("path", "");
                if(path.length() < 1){
                    recreate();
                    return;
                }
                outputFileUri = Uri.parse("file://" + path);
            }

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outputFileUri));
            Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
            intent.putExtra("imageUri", outputFileUri.toString());
            startActivity(intent);
        }else if(data == null){
            recreate();
            return;
        }else if (requestCode == REQUEST_PICK_IMAGE){
            outputFileUri = data.getData();
            Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
            intent.putExtra("imageUri", outputFileUri.toString());
            startActivity(intent);
        }

//        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Загрузка", "Это займет некоторое время", true);


    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
//        File storageDir = new File(strSDCardPathName);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return new File(strSDCardPathName + imageFileName);

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

}
