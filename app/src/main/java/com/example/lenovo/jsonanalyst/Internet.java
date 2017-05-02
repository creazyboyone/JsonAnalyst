package com.example.lenovo.jsonanalyst;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.qiujuer.genius.blur.StackBlur;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * Created by lenovo on 2017/4/19.
 */

public class Internet {




    public String[] startAnalystJson(String json) {
        try {
            JSONTokener jsonTokener = new JSONTokener(json);
            JSONObject returnJson = (JSONObject) jsonTokener.nextValue();
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

            String[] str = {sid + "", BGMUrl, content, note, love + "", translation, picture, picture2, caption, dateline};
            return str;

        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
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

    public String getJsonFromUrl(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connect = (HttpURLConnection)url.openConnection();
        connect.setConnectTimeout(10*1000);//10*1000毫秒=10秒
        connect.setRequestMethod("GET");
        if(connect.getResponseCode()==200) {
            InputStream is = connect.getInputStream();
            byte[] data = readStream(is);
            return new String(data);
        }else {
            System.out.println("return ResponseCode="+connect.getResponseCode());
        }
        return "error";
    }

    public Bitmap getPhotoFromUrl(String path)throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(10*1000);
        InputStream inputStream = conn.getInputStream();
        byte[] data = readStream(inputStream);
        Bitmap bm = BitmapFactory.decodeByteArray(data,0,data.length);
        Bitmap bitmap = StackBlur.blurNativelyPixels(bm,17,false);
        return bitmap;
    }


}
