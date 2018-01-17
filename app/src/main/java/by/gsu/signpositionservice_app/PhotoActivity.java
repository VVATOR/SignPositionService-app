package by.gsu.signpositionservice_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import by.gsu.SignPositionService.models.Point;
import by.gsu.SignPositionService.models.Sign;
import by.gsu.client.Client;
import by.gsu.client.ISignPositionClient;

public class PhotoActivity extends AppCompatActivity {
    private ISignPositionClient client = new Client();
    private ImageView iv;
    private Button buttonSave;
    private Button buttonCancel;
    private EditText editFileName;
    private EditText editDescription;
    private Location location;
    private Intent intent;
    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            intent = new Intent(PhotoActivity.this, MainActivity.class);
            startActivity(intent);

        }
    };
    private TextView tvInfo;
    private ProgressBar progressBarForList;
    private Sign newSign;
    private StringBuilder sb = new StringBuilder("Location");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        progressBarForList = (ProgressBar) findViewById(R.id.progressBarForList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, sb.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        location = (Location) getIntent().getParcelableExtra(Location.class.getCanonicalName());
        iv = (ImageView) findViewById(R.id.photoImageView);
        editFileName = (EditText) findViewById(R.id.editFileName);
        editDescription = (EditText) findViewById(R.id.editDescription);

        Intent data = getIntent();

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        iv.setImageBitmap(bitmap);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();

                Bitmap bitmap1 = drawable.getBitmap();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Point point = new Point(location.getLatitude(), location.getLongitude());

                String fname = "".equals(editFileName.getText()) ? System.currentTimeMillis() + ".jpg" : editFileName.getText() + ".jpg";
                String description = "" + editDescription.getText();
                newSign = new Sign(331, fname, description, point, encoded);

                new AsyncTask<Integer, Integer, String>() {
                    private boolean status = true;

                    @SuppressLint("WrongConstant")
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressBarForList.setVisibility(View.VISIBLE);
                        tvInfo.setVisibility(View.VISIBLE);
                        tvInfo.setText("Please wait...");
                        buttonSave.setEnabled(false);
                        buttonSave.setText("please wait");
                    }

                    @Override
                    protected String doInBackground(Integer... arg) {

                        Log.e("start async load data", "");
                        List<Sign> list = new ArrayList();

                        try {
                            client.methodPostSign(newSign);
                            status = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            status = false;
                            Log.e("error client", "" + e.getMessage());
                        }
                        return "";
                    }

                    @SuppressLint("WrongConstant")
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);


                        sb = new StringBuilder("sucessfull");
                        progressBarForList.setVisibility(View.INVISIBLE);
                        tvInfo.setVisibility(View.INVISIBLE);
                        tvInfo.setText("");
                        if (status) {
                            gotoMain();
                        } else {
                            sb = new StringBuilder("error");
                            buttonSave.setEnabled(true);
                            buttonSave.setText("try again");
                        }
                        Log.i("end async load data", "");
                    }

                }.execute();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(listener);
    }

    public void gotoMain() {
        intent = new Intent(PhotoActivity.this, MainActivity.class);
        startActivity(intent);
    }
}



