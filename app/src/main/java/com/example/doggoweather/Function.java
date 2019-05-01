package com.example.doggoweather;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Function {
    public static boolean isConnected(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String executeGet(String weatherConnection) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(weatherConnection);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            InputStream is;
            int connectionStatus = connection.getResponseCode();
            if (connectionStatus != HttpURLConnection.HTTP_OK) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            String connectionLine;
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            while ((connectionLine = rd.readLine()) != null) {
                response.append(connectionLine);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
