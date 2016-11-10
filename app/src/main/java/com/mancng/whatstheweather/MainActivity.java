package com.mancng.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    EditText txtCityName;
    TextView viewWeahter;

    public void btnSearch (View view) {

        // Hide the phone keyboard after the button is clicked
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(txtCityName.getWindowToken(), 0);

        try {

                // Take out the space entered in editText
                String encodedCityName = URLEncoder.encode(txtCityName.getText().toString(), "UTF-8");

                DownloadTask task = new DownloadTask();
                task.execute("http://api.openweathermap.org/data/2.5/forecast/city?q=" + encodedCityName + "&APPID=3a06c1d4a65de6ffe64a5820d79996c4");

        } catch (Exception e) {

            Log.i("Blank entry", "blank!!");

        }

    }

    public class DownloadTask extends AsyncTask <String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Log.e("Exception", "doInBackground error");

            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                String message = "";

                // Parse the entire JSON String
                JSONObject rootObject = new JSONObject(result);
                Log.i("root", rootObject.toString());

                // Get the name from city object
                String cityName = rootObject.getJSONObject("city").getString("name");

                Log.i("confirm city names", cityName +" and " + txtCityName.getText().toString());

                Log.i("type", "cityName type: " + cityName.getClass().getName() + "txtCityName type: " + txtCityName.getClass().getName());

                // Compare cityName from JSON vs. entered TextView
                if (cityName.toString() == txtCityName.getText().toString()) {

                    throw new Exception();

                } else {

                    Log.i("city", cityName);

                    // Get the weather info from JSON. Info happens to be inside the "list" array
                    JSONArray listJsonArray = rootObject.getJSONArray("list");

                    // Loop through the list array and parse the object content inside the array
                    for (int i = 0; i < listJsonArray.length(); i++) {

                        // Parse the weather objects
                        JSONObject listObject = listJsonArray.getJSONObject(i);

                        Object weatherObject = listObject.getString("weather");

                        String weatherInfo = weatherObject.toString();

                        JSONArray weatherArray = new JSONArray(weatherInfo);

                        JSONObject weatherPart = weatherArray.getJSONObject(0);

                        String main = "";
                        String description = "";

                        main = weatherPart.getString("main");
                        description = weatherPart.getString("description");

                        Log.i("main", weatherPart.getString("main"));
                        Log.i("description", weatherPart.getString("description"));

                        if (main != "" && description != "") {

                            message = "City: " + cityName + "\r\n" + "Weather: " + description + "\r\n";

                        }

                    }

                    if (message != "") {

                        viewWeahter.setText(message);

                    } else if (result == "") {

                        {

                            throw new Exception();

                        }

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "error on JSON", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                viewWeahter.setText("");
                Toast.makeText(getApplicationContext(), "Please enter a valid city", Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCityName = (EditText) findViewById(R.id.txtCityName);
        viewWeahter = (TextView) findViewById(R.id.viewWeather);

    }

}
