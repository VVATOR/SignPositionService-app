package by.gsu.signpositionservice_app;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import by.gsu.SignPositionService.models.Sign;
import by.gsu.client.Client;

import by.gsu.client.ISignPositionClient;
import by.gsu.signpositionservice_app.exceptions.LocationException;
import by.gsu.signpositionservice_app.utils.LocationUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Action.PHOTO.ordinal());
        }
    };
    private LocationUtils locationUtils = new LocationUtils(this);
    private Location location;
    private String locationInfo = "";
    private ImageButton imageButton;
    private ISignPositionClient client = new Client();
    private Intent intent;
    private ImageView iv;
    private TextView helloTextView;
    private Sign newSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        try {
            location = locationUtils.getLocation();
            locationInfo = ("Current location: Longitude:" + location.getLatitude() + "; Latitude:" + location.getLatitude() + ";");
        } catch (LocationException e) {
            locationInfo = (e.getMessage());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, locationInfo, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        iv = (ImageView) findViewById(R.id.imageView2);
        helloTextView = (TextView) findViewById(R.id.helloTextView);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(listener);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Action.PHOTO.ordinal());
        } else if (id == R.id.nav_gallery) {
            intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Action action = Action.values()[requestCode];
        switch (action) {
            case PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    intent = new Intent(MainActivity.this, PhotoActivity.class);
                    intent.putExtras(data);
                    intent.putExtra(Location.class.getCanonicalName(), location);
                    startActivity(intent);
                }
        }
    }

    enum Action {
        PHOTO;
    }
}
