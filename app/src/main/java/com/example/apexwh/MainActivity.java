package com.example.apexwh;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apexwh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DB.onStart(getBaseContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

//        ((NavigationView) root.findViewById(R.id.nav_view));



        Intent intent = getIntent();

        navController.popBackStack();

        Bundle bundle = new Bundle();
        bundle.putString("id", intent.getStringExtra("id"));
        bundle.putString("name", intent.getStringExtra("name"));
        bundle.putString("warehouses", intent.getStringExtra("warehouses"));

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvSettings)).setText(intent.getStringExtra("name"));

        navController.navigate(R.id.nav_home, bundle);

//
//
//        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        int rc2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (rc == PackageManager.PERMISSION_GRANTED &&
//            rc2 == PackageManager.PERMISSION_GRANTED) {
//            //createCameraSource(autoFocus, useFlash);
//        } else {
//            requestCameraPermission();
//            requestExternalStoragePermission();
//        }

    }

    private void requestCameraPermission() {
//        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

//        final Activity thisActivity = this;
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(thisActivity, permissions,
//                        RC_HANDLE_CAMERA_PERM);
//            }
//        };

//        findViewById(R.id.topLayout).setOnClickListener(listener);
//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                        Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
    }

    private void requestExternalStoragePermission() {
//        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST);
            return;
        }

//        final Activity thisActivity = this;
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(thisActivity, permissions,
//                        RC_HANDLE_CAMERA_PERM);
//            }
//        };

//        findViewById(R.id.topLayout).setOnClickListener(listener);
//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                        Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}