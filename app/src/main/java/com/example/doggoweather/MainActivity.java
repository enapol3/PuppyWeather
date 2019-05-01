package com.example.doggoweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

//Main Activity method for my app.
public class MainActivity extends AppCompatActivity {
    TextView typeCity, cityName, detailsForCity, temperatureField, update, DogSafety, DogTimer;
    ProgressBar load;
    String city = "Champaign, US";
    RelativeLayout Background;

    String OPEN_WEATHER_API = "81b310dc7bf0f573e22321a3518e3211";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        DogSafety = findViewById(R.id.DogSafety);
        DogTimer = findViewById(R.id.DogTimer);
        Background = findViewById(R.id.Background);
        load = findViewById(R.id.loader);
        typeCity = findViewById(R.id.typeCity);
        cityName = findViewById(R.id.city_name);
        update = findViewById(R.id.update);
        detailsForCity = findViewById(R.id.details_For_City);
        temperatureField = findViewById(R.id.current_temperature);

        taskLoadUp(city);

        //Allows the user to type in the city they are want to look at.
        typeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change City");
                final EditText input = new EditText(MainActivity.this);
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

    }
    //Checks to see if the connection is correct.
    public void taskLoadUp(String query) {
        if (Function.isConnected(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    //Sets the type of data to gather from the OPEN_WEATHER_API
    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load.setVisibility(View.VISIBLE);
        }
        //Finds weather connectivity
        protected String doInBackground(String...args) {
            String xml = Function.executeGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=imperial&appid=" + OPEN_WEATHER_API);
            return xml;
        }
        //JSON data to coding data.
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                // Temperature warnings!
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();
                    double tempet = main.getDouble("temp");
                    if (tempet > 60.0) {
                        DogSafety.setText("Have fun outside!");
                        Background.setBackgroundColor(Color.parseColor("#1B5E20"));
                        DogTimer.setText("Be careful of hot weather!");
                    }
                    if (tempet <= 60.0 && tempet >= 50.0) {
                        DogSafety.setText("No Evidence of Risk; Have fun outside!");
                        Background.setBackgroundColor(Color.parseColor("#7986CB"));
                        DogTimer.setText("Come in whenever you like!");
                    }
                    if (tempet < 50.0 && tempet >= 45.0) {
                        DogSafety.setText("Risk is Unlikely; Have fun outside, but be careful!");
                        Background.setBackgroundColor(Color.parseColor("#5C6BC0"));
                        DogTimer.setText("Come in whenever you like: Recommend 1 hour!");
                    }
                    if (tempet < 45.0 && tempet >= 30.0) {
                        DogSafety.setText("Uncomfortable Depending on breed. Keep an eye on your pet.");
                        Background.setBackgroundColor(Color.parseColor("#3F51B5"));
                        DogTimer.setText("30 Minutes!");
                    }
                    if (tempet < 30.0 && tempet >= 25.0) {
                        DogSafety.setText("Unsafe depending on breed. Keep an eye on your pet.");
                        Background.setBackgroundColor(Color.parseColor("#3949AB"));
                        DogTimer.setText("20 - 30 minutes!");
                    }
                    if (tempet < 25.0 && tempet >= 15.0) {
                        DogSafety.setText("Dangerous weather developing. Limit time outside!");
                        Background.setBackgroundColor(Color.parseColor("#303F9F"));
                        DogTimer.setText("10 - 20 minutes!");
                    }
                    if (tempet < 15.0 && tempet >= 10.0) {
                        DogSafety.setText("Dangerous weather! Limit time outside to prevent frostbite!");
                        Background.setBackgroundColor(Color.parseColor("#283593"));
                        DogTimer.setText("Less than 10 minutes!");
                    }
                    if (tempet < 10) {
                        DogSafety.setText("Potentially life-threatening cold. Avoid prolonged outdoor activity");
                        Background.setBackgroundColor(Color.parseColor("#B71C1C"));
                        DogTimer.setText("Go out, do your business, come in!");
                    }
                    //Sets the text to use for the text view.
                    cityName.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsForCity.setText(details.getString("description").toUpperCase(Locale.US));
                    temperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°F");
                    update.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    //Sets what weather icons you should use for the image in the middle.
                    load.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Check City", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

