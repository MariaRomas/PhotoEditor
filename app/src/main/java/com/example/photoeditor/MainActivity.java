package com.example.photoeditor;

import android.content.Intent;
import android.net.Uri;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtView = findViewById(R.id.text);
        txtView.setText(strSDCardPathName);
        createFolder();

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Переход в режим камеры*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) { }

                    outputFileUri = Uri.fromFile(photoFile);
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(takePictureIntent, 1);

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT);
                }
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
}
