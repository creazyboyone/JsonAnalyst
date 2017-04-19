package com.example.lenovo.jsonanalyst;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    ListView listView;
    public final String url = "http://open.iciba.com/dsapi/";
    String[] jsonReault;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.main_lv);
        handler = new Handler();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Internet internet = new Internet();
                    String json = internet.getJsonFromUrl(url);
                    jsonReault = internet.startAnalystJson(json);
                    handler.post(runableUI);


                }catch (Exception e){
                    e.printStackTrace();
                    handler.post(error);
                }

            }
        });
        t.start();

    }
    Runnable error = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(
                    MainActivity.this,
                    "Please confirm you have connect the Internet!",
                    Toast.LENGTH_SHORT).show();
        }
    };
    Runnable runableUI = new Runnable() {
        @Override
        public void run() {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    jsonReault
            );
            listView.setAdapter(adapter);
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.renew_item:
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Internet internet = new Internet();
                            String json = internet.getJsonFromUrl(url);
                            jsonReault = internet.startAnalystJson(json);
                            handler.post(runableUI);


                        }catch (Exception e){
                            e.printStackTrace();
                            handler.post(error);
                        }

                    }
                });
                th.start();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());
        }
        return super.onKeyDown(keyCode,event);

    }
}
