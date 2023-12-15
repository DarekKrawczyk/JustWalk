package com.example.justwalk;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadURL {
    public String retrieveURL(String url) throws IOException {
        String urlData = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try{
            URL getURL = new URL(url);
            httpURLConnection = (HttpURLConnection)getURL.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();
            String line = "";

            while((line = bufferReader.readLine()) != null){
                sb.append(line);
            }

            urlData = sb.toString();
            bufferReader.close();
        } catch(Exception e){
            Log.d("Exception", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return urlData;
    }
}
