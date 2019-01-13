package com.example.sallamy.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView resultTextView;
    EditText cityName;
    String city; // by default will be Cairo in xml

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = (TextView) findViewById(R.id.result);
        cityName = (EditText) findViewById(R.id.cityTextview);


    }

    // onClick method on Button
    public void findWeather(View view) {
        // get city name
        city = cityName.getText().toString();
//DownloadTask
        DownloadTask task = new DownloadTask();

        // to hide the keyboard when click on button to see the result
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

// to encode URl
        try {
            String encodedCityName = URLEncoder.encode(city, "UTF-8");
            task.execute("https://samples.openweathermap.org/data/2.5/weather?q="+encodedCityName+",uk&appid=b6907d289e10d714a6e88b30761fae22");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }


    }

    // DownloadTask class
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        // connection method
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    // read the next char
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "Error in link ", Toast.LENGTH_LONG);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
            return null;
        }

        // background method run after doInBackground method
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String weatherinfo;

            try {
                String message = "";
                // create JSON object
                JSONObject jsonObject = new JSONObject(result);
// extract weather object from JSONObject
                weatherinfo = jsonObject.getString("weather");

// creating JSON arr to extract the weather only ..
                JSONArray array = new JSONArray(weatherinfo);

                for (int i = 0; i < array.length(); i++) {
                    //creating new objects part by part
                    JSONObject jsonPart = array.getJSONObject(i);

                    // we need extract main and description
                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    // appear in screen
                    if (main != "" && description != "") {
                        message += main + ": " + description + "\r\n";
                    }

                }
                if (message != "") {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }


        }
    }

}

