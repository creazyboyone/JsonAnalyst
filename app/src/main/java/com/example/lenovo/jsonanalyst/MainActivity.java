package com.example.lenovo.jsonanalyst;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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
                    String json = getJsonFromUrl(url);
                    jsonReault = startAnalystJson(json);
                    handler.post(runableUI);


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        t.start();

    }
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
    private String getJsonFromUrl(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connect = (HttpURLConnection)url.openConnection();
        connect.setConnectTimeout(10*1000);//10*1000毫秒=10秒
        connect.setRequestMethod("GET");
        if(connect.getResponseCode()==200) {
            InputStream is = connect.getInputStream();
            byte[] data = readStream(is);
            return new String(data);
        }else {
            Toast.makeText(MainActivity.this,"Internet connect error!",Toast.LENGTH_SHORT);
        }
        return "error";
    }
    private byte[] readStream(InputStream inputStr)throws Exception{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStr.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStr.close();
        return bout.toByteArray();
    }
    private String[] startAnalystJson(String json){
        try{
            JSONTokener jsonTokener = new JSONTokener(json);
            JSONObject returnJson = (JSONObject)jsonTokener.nextValue();
            int sid = returnJson.getInt("sid");
            String BGMUrl = returnJson.getString("tts");
            String content = returnJson.getString("content");
            String note = returnJson.getString("note");
            int love = returnJson.getInt("love");
            String translation = returnJson.getString("translation");
            String picture = returnJson.getString("picture");
            String picture2 = returnJson.getString("picture2");
            String caption = returnJson.getString("caption");
            String dateline = returnJson.getString("dateline");

            String[] str = {sid+"",BGMUrl,content,note,love+"",translation,picture,picture2,caption,dateline};
            return str;
//            System.out.println(BGMUrl);
//            System.out.println(content);
//            System.out.println(note);
//            System.out.println(love);
//            System.out.println(translation);
//            System.out.println(picture);
//            System.out.println(picture2);
//            System.out.println(caption);
//            System.out.println(dateline);

        }catch (JSONException ex){
            throw new RuntimeException(ex);
        }
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
