package app.biblipad.functions;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Kashyap on 10/8/2015.
 */

public class JSONfunctions {



    // function get json from url
    // by making HTTP POST or GET mehtod
    public static JSONObject makenewHttpRequest(Context context, String url, JSONObject params) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        Static_Catelog.log("url:"+url+" params:"+params.toString());
        // Making HTTP request
        try {

            // check for request method

            // request method is POST
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(url);
            StringEntity paramss =new StringEntity(params.toString());
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(paramss);
            HttpResponse response = httpClient.execute(request);

            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            System.out.print(sb.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        if(!json.startsWith("{"))
        {
            json="{}";
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }


    public static JSONObject getJSONfromURL( String url) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;
//        Static_Catelog.log("url:"+url+" params:");
        // Download JSON data from URL
         try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
          if(!result.startsWith("{"))
        {
            result="{}";
        }
        try {

            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null&&cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
