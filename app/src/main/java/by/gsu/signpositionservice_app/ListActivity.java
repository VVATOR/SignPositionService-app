package by.gsu.signpositionservice_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.gsu.SignPositionService.models.Sign;
import by.gsu.client.Client;
import by.gsu.client.ISignPositionClient;
import by.gsu.signpositionservice_app.lazylist.LazyAdapter;


public class ListActivity extends Activity {
    private ISignPositionClient client = new Client();
    private List<Sign> store = new ArrayList<Sign>();
    private ListView list;
    private LazyAdapter adapter;

    private Intent intent;
    public OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
    private TextView tvInfo;
    private ProgressBar progressBarForList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_list);
        intent = getIntent();
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        progressBarForList = (ProgressBar) findViewById(R.id.progressBarForList);

        list = (ListView) findViewById(R.id.list);

        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(listener);

        new AsyncTask<Activity, Integer, List<Sign>>() {
            @SuppressLint("WrongConstant")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarForList.setVisibility(View.VISIBLE);
                tvInfo.setVisibility(View.VISIBLE);
                tvInfo.setText("Please wait...");
            }

            @Override
            protected List<Sign> doInBackground(Activity... arg) {
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

            @SuppressLint("WrongConstant")
            @Override
            protected void onPostExecute(List<Sign> result) {
                super.onPostExecute(result);

                loadList();
                progressBarForList.setVisibility(View.INVISIBLE);
                tvInfo.setVisibility(View.INVISIBLE);
                tvInfo.setText("");

                Log.i("end async load data", "");
            }
        }.execute(this);
    }

    public void loadList() {
        adapter = new LazyAdapter(this, store);
        list.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }

}