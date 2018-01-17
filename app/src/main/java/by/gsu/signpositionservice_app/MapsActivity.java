package by.gsu.signpositionservice_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.gsu.SignPositionService.models.Point;
import by.gsu.SignPositionService.models.Sign;
import by.gsu.client.Client;

import by.gsu.client.ISignPositionClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private final int configTiltValue = 45;
    private int configBearingValue = 0;
    private int configZoomValue = 5;
    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private ISignPositionClient client = new Client();
    private Intent intent;
    private TextView tvInfo;
    private ProgressBar progressBarForMap;
    private List<Sign> store = new ArrayList<Sign>();

    @Override
    public void onInfoWindowClick(Marker marker) {
        intent = new Intent(MapsActivity.this, ImageActivity.class);
        intent.putExtra("sign", (Sign) marker.getTag());
        startActivity(intent);

    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_gmap_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            Sign sign = (Sign) marker.getTag();
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            //tvTitle.setText(sign.getId() + "+" + marker.getId() + "-vva-" + marker.getTitle());
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            ImageView icon = ((ImageView) myContentsView.findViewById(R.id.icon));
            if (sign.getData() != null) {
                byte[] decodedString = Base64.decode(sign.getData(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                icon.setImageBitmap(decodedByte);
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        intent = getIntent();
        tvInfo = (TextView) findViewById(R.id.tvInfoMap);
        progressBarForMap = (ProgressBar) findViewById(R.id.progressBarForMap);

        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        new AsyncTask<SupportMapFragment, Integer, List<Sign>>() {
            @SuppressLint("WrongConstant")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarForMap.setVisibility(View.VISIBLE);
                tvInfo.setVisibility(View.VISIBLE);
                tvInfo.setText("Please wait...");
            }

            @Override
            protected List<Sign> doInBackground(SupportMapFragment... arg) {
                Log.e("start async load data", "");
                List<Sign> list = new ArrayList();
                try {
                    list = client.getListSigns();
                    store = list;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("error client", "" + e.getMessage());
                }
                store = list;
                return list;
            }

            @Override
            protected void onPostExecute(List<Sign> result) {
                super.onPostExecute(result);

                loadMap();
                progressBarForMap.setVisibility(View.INVISIBLE);
                tvInfo.setVisibility(View.INVISIBLE);
                tvInfo.setText("");

                Log.i("end async load data", "");
            }
        }.execute(fragment);
    }

    public void loadMap() {
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        LatLng latLng = new LatLng(-34, 151);
        for (Sign sign : store) {
            Point point = sign.getPoint();
            latLng = new LatLng(point.getX(), point.getY());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(sign.getName()).snippet("Longitude:" + sign.getPoint().getX() + "; Latitude:" + sign.getPoint().getY())).setTag(sign);
        }
        CameraPosition liberty = CameraPosition.builder().target(latLng).zoom(configZoomValue).bearing(configBearingValue).tilt(configTiltValue).build();
    }


}
