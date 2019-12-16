package com.example.photoeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.photoeditor.Adapter.ViewPagerAdapter;
import com.example.photoeditor.interfaces.EditImageFragmentListener;
import com.example.photoeditor.interfaces.FiltersListFragmentListener;
import com.example.photoeditor.utils.BitmapUtils;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;

public class EditImageActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {
    final private static String strSDCardPathName = Environment.getExternalStorageDirectory() + "/temp_picture" + "/";


    //Код возвращаемый активити выбора файлов
    private static final int REQUEST_PICK_IMAGE = 112;

    //Код возвращаемый активити после создания фото
    private static final int REQUEST_IMAGE_CAPTURE = 113;


    ImageView img_preveiw;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightness_final = 0;
    float saturation_final = 1.0f;
    float contrast_final = 1.0f;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    private void loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_4444, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_4444, true);
        img_preveiw.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightness_final = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        img_preveiw.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturation_final = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        img_preveiw.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrast_final = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        img_preveiw.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness_final));
        myFilter.addSubFilter(new SaturationSubfilter(saturation_final));
        myFilter.addSubFilter(new ContrastSubFilter(contrast_final));

        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preveiw.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if(editImageFragment != null){
            editImageFragment.resetControls();
        }

        brightness_final = 0;
        saturation_final = 1.0f;
        contrast_final = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_open){
            openImageFromGallery();
            return true;
        }

        if(id == R.id.action_save){
            saveImageToGallery();
            return true;
        }

        if(id == R.id.action_close){

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {
        final String path = BitmapUtils.insertImage(getContentResolver(), finalBitmap, System.currentTimeMillis() + "profile.jpg", null);

        if(!TextUtils.isEmpty(path)){
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Изображение сохранено в галлерею", Snackbar.LENGTH_LONG)
                    .setAction("Открыть", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openImage(path);
                        }
                    });
            snackbar.show();
        } else{
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Изображение невозможно сохранить", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RESULT_OK && requestCode == REQUEST_PICK_IMAGE){
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            originalBitmap.recycle();
            finalBitmap.recycle();
            filteredBitmap.recycle();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            img_preveiw.setImageBitmap(originalBitmap);
            bitmap.recycle();

            filtersListFragment.displayThumbnail(originalBitmap);
        }
    }
}
