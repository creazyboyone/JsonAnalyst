package com.example.lenovo.jsonanalyst;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    TextView good;
    TextView id;
    TextView time;
    TextView eng;
    TextView cn;
    TextView editor;
    RelativeLayout relativeLayout;
    public final String url = "http://open.iciba.com/dsapi/";
    String[] jsonReault;
    Bitmap b;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
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
    private void findId(){
        good = (TextView)findViewById(R.id.good_tv);
        id = (TextView)findViewById(R.id.id_tv);
        time = (TextView)findViewById(R.id.time_tv);
        eng = (TextView)findViewById(R.id.english_tv);
        cn = (TextView)findViewById(R.id.chinese_tv);
        editor = (TextView)findViewById(R.id.editor_tv);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
    }
    Runnable error = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(
                    MainActivity.this,
                    R.string.NoInternet,
                    Toast.LENGTH_LONG).show();
        }
    };
    Runnable runableUI = new Runnable() {
        @Override
        public void run() {
            good.setText("Good : "+jsonReault[4]);
            id.setText("ID:"+jsonReault[0]);
            time.setText("时间："+jsonReault[9]);
            eng.setText(jsonReault[2]);
            cn.setText(jsonReault[3]);
            editor.setText(jsonReault[5]);
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try{
                        Internet internet = new Internet();
                        b = internet.getPhotoFromUrl(jsonReault[7]);
                        handler.post(background);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    };
    Runnable background = new Runnable() {
        @Override
        public void run() {
            BitmapDrawable bd = new BitmapDrawable(b);
            relativeLayout.setBackgroundDrawable(bd);
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
            case R.id.reading_item:
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if(isInternet(MainActivity.this)){
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(jsonReault[1]);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            }else {
                                handler.post(error);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            handler.post(error);
                        }

                    }
                });
                t.start();
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
    public boolean isInternet(Activity activity){
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
