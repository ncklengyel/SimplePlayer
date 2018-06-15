package com.example.nckle.myapplication;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static com.example.nckle.myapplication.Utils.readAll;

public class GetTask extends AsyncTask<String, Void, Object> {

    private Exception exception;

    protected Object doInBackground(String... params) {
        String url = params[0];
        String command = params[1];
        try {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json.get(command);
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
                is.close();
            }
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(String url) {
        // TODO: do something here
    }
}
