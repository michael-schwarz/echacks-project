package net.m_schwarz.journe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.m_schwarz.journe.Comm.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        Preferences preferences = new Preferences(this);

        if(!preferences.isLoggedIn()){
            Intent intent = new Intent(this,RegisterActivity.class);
            finish();
            startActivity(intent);
            return;
        }

        AsyncTask<Integer,Void,User> getUserDetailsTask = new AsyncTask<Integer,Void,User>() {
            @Override
            protected User doInBackground(Integer... params) {
                try {
                    return User.get(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User user) {
                gotUserDetails(user);
            }
        };

        getUserDetailsTask.execute(12);

        GeoImage[] values = new GeoImage[10];
        for(int i =0;i < 10;i++){
            values[i] = new GeoImage();
        }


        ListView lv = (ListView) findViewById(R.id.listview);
        ArrayAdapter<GeoImage> adapter = new ImageAdapter(this, values);

        lv.setAdapter(adapter);

    }

    private void gotUserDetails(User user) {
        TextView points = (TextView) findViewById(R.id.points);
        points.setText("Points: " + user.points);

        TextView email = (TextView) findViewById(R.id.email);
        email.setText(user.email);
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap= BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Toast.makeText(this,"" + bitmap.getByteCount(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar  if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_store) {
            // Handle the camera action
        }

        if(id == R.id.nav_logout){
            (new Preferences(this)).logOut();
            Intent intent = new Intent(this,RegisterActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
