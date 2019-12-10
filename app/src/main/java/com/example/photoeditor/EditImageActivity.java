package com.example.photoeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class EditImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        Uri imageUri = Uri.parse(intent.getStringExtra("imageUri"));
        ImageView imgEl = findViewById(R.id.image_preview);

        File image = new File(imageUri.getPath());
        Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

        imgEl.setImageBitmap(myBitmap);
    }
}
