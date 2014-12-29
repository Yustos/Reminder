package ru.yt.reminderreader.services;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.yt.reminderreader.domain.Record;

/**
 * Created by Yustos on 26.12.2014.
 */
public class ServiceBase {
    protected class ReadDataTask extends AsyncTask<String, Void, String> {
        private final OnDataReceiver _receiver;
        public ReadDataTask(OnDataReceiver receiver)
        {
            _receiver = receiver;
        }

        public String ReadJson(String url) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                } else {
                    Log.d("JSON", "Failed to download file");
                }
            } catch (Exception e) {
                Log.d("readJSONFeed", e.getLocalizedMessage());
            }
            return stringBuilder.toString();
        }

        protected String doInBackground(String... urls) {
            return ReadJson(urls[0]);
        }

        protected void onPostExecute(String result) {
            _receiver.onDataReceived(result);
        }
    }

    protected class SaveDataTask extends AsyncTask<String, Void, String> {
        private final String _data;
        private final OnDataReceiver _receiver;
        public SaveDataTask(String data, OnDataReceiver receiver)
        {
            _data = data;
            _receiver = receiver;
        }

        public String ReadJson(String url) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            try {
                StringEntity se = new StringEntity(_data);
                httpPost.setEntity(se);

                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                } else {
                    Log.d("JSON", "Failed to download file");
                }
            } catch (Exception e) {
                Log.d("readJSONFeed", e.getLocalizedMessage());
            }
            return stringBuilder.toString();
        }

        protected String doInBackground(String... urls) {
            return ReadJson(urls[0]);
        }

        protected void onPostExecute(String result) {
            _receiver.onDataReceived(result);
        }
    }

    protected class DeleteDataTask extends AsyncTask<String, Void, String> {
        private final OnDataReceiver _receiver;
        public DeleteDataTask(OnDataReceiver receiver)
        {
            _receiver = receiver;
        }

        public String ReadJson(String url) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDel = new HttpDelete(url);

            //httpPost.setHeader("Accept", "application/json");
            //httpPost.setHeader("Content-type", "application/json");

            try {
                //StringEntity se = new StringEntity(_data);
                //httpPost.setEntity(se);

                HttpResponse response = httpClient.execute(httpDel);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                } else {
                    Log.d("JSON", "Failed to download file");
                }
            } catch (Exception e) {
                Log.d("readJSONFeed", e.getLocalizedMessage());
            }
            return stringBuilder.toString();
        }

        protected String doInBackground(String... urls) {
            return ReadJson(urls[0]);
        }

        protected void onPostExecute(String result) {
            _receiver.onDataReceived(result);
        }
    }
}
