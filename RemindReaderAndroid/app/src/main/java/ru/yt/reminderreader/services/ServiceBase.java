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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

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

    public String callService(String actionUrl, HttpRequestBase request) throws Exception {
        String fullUrl = _serviceBaseUrl
                + (_serviceBaseUrl.endsWith("/")? "" : "/")
                + actionUrl;

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

        HttpClient httpClient = new DefaultHttpClient(params);
        httpClient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);

        try {
            URI uri = new URI(fullUrl);
            request.setURI(uri);

            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, HTTP.UTF_8);
            } else {
                String errorMessage = String.format("Failed to call service. Code: %s", statusCode);
                Log.d("ServiceBase", errorMessage);
                throw new Exception(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = e.getLocalizedMessage();
            Log.d("ServiceBase", errorMessage);
            throw e;
        }
    }

    protected HttpRequestBase createPostRequest(String data)
    {
        HttpPost httpPost = new HttpPost();

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try {
            StringEntity se = new StringEntity(data, HTTP.UTF_8);
            httpPost.setEntity(se);
        }
        catch (UnsupportedEncodingException ex)
        {
            Log.d("SaveDataTask", ex.getLocalizedMessage());

        }
        return httpPost;
    }

    protected abstract class CallServiceTaskBase extends AsyncTask<String, Void, String> {
        private final DataReceiver _receiver;
        private String Failure = null;

        public CallServiceTaskBase(DataReceiver receiver)
        {
            _receiver = receiver;
        }

        protected abstract HttpRequestBase PrepareRequest();

        @Override
        protected String doInBackground(String... urls) {
            try {
                return callService(urls[0], PrepareRequest());
            }
            catch (Exception e)
            {
                Failure = e.getLocalizedMessage();
                return null;
            }
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
            return createPostRequest(_data);
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
