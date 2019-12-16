package com.example.photoeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.photoeditor.Adapter.ViewPagerAdapter;
import com.example.photoeditor.interfaces.EditImageFragmentListener;
import com.example.photoeditor.interfaces.FiltersListFragmentListener;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;

public class EditImageActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    ImageView img_preveiw;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightness_final = 0;
    float saturation_final = 1.0f;
    float constraint_final = 1.0f;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolBar);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        img_preveiw = findViewById(R.id.image_preview);
        tabLayout =   findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        coordinatorLayout = findViewById(R.id.coordinator);

        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
//        setContentView(R.layout.activity_edit_image);
//        Uri imageUri = Uri.parse(intent.getStringExtra("imageUri"));
//        ImageView imgEl = findViewById(R.id.image_preview);
//
//        File image = new File(imageUri.getPath());
//        Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
//
//        imgEl.setImageBitmap(myBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);
    }

    @Override
    public void onBrightnessChanged(int brightness) {

    }

    @Override
    public void onSaturationChanged(float saturation) {

    }

    @Override
    public void onConstraintChanged(float constraint) {

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {

    }

    @Override
    public void onFilterSelected(Filter filter) {

    }
}
