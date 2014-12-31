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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Created by Yustos on 26.12.2014.
 */
public class ServiceBase {
    private final String _serviceBaseUrl;

    public ServiceBase(String serviceBaseUrl)
    {
        _serviceBaseUrl = serviceBaseUrl;
    }

    protected abstract class CallServiceTaskBase extends AsyncTask<String, Void, String> {
        private final DataReceiver _receiver;
        private String Failure = null;

        public CallServiceTaskBase(DataReceiver receiver)
        {
            _receiver = receiver;
        }

        protected abstract HttpRequestBase PrepareRequest();

        public String CallService(String url) {
            String fullUrl = _serviceBaseUrl
                    + (_serviceBaseUrl.endsWith("/")? "" : "/")
                    + url;

            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();

            HttpRequestBase request = PrepareRequest();
            if (Failure != null)
            {
                return null;
            }

            try {
                URI uri = new URI(fullUrl);
                request.setURI(uri);

                HttpResponse response = httpClient.execute(request);
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
                    String errorMessage = String.format("Failed to call service. Code: %s", statusCode);
                    Log.d("ServiceBase", errorMessage);
                    Failure = errorMessage;
                }
            } catch (Exception e) {
                String errorMessage = e.getLocalizedMessage();
                Log.d("ServiceBase", errorMessage);
                Failure = errorMessage;
            }
            return stringBuilder.toString();
        }

        @Override
        protected String doInBackground(String... urls) {
            return CallService(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (Failure != null)
            {
                _receiver.onFailure(Failure);
            }
            else
            {
                _receiver.onDataReceived(result);
            }
        }
    }

    protected class ReadDataTask extends CallServiceTaskBase {
        public ReadDataTask(DataReceiver receiver)
        {
            super(receiver);
        }

        @Override
        protected HttpRequestBase PrepareRequest()
        {
            return new HttpGet();
        }
    }

    protected class SaveDataTask extends CallServiceTaskBase {
        private final String _data;
        public SaveDataTask(String data, DataReceiver receiver)
        {
            super(receiver);
            _data = data;
        }

        @Override
        protected HttpRequestBase PrepareRequest()
        {
            HttpPost httpPost = new HttpPost();

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            try {
                StringEntity se = new StringEntity(_data);
                httpPost.setEntity(se);
            }
            catch (UnsupportedEncodingException ex)
            {
                Log.d("SaveDataTask", ex.getLocalizedMessage());

            }
            return httpPost;
        }
    }

    protected class DeleteDataTask extends CallServiceTaskBase {
        public DeleteDataTask(DataReceiver receiver)
        {
            super(receiver);
        }

        @Override
        protected HttpRequestBase PrepareRequest()
        {
            return new HttpDelete();
        }
    }
}
