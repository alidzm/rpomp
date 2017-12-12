package com.example.user.currencyconverter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    String access_key = "?access_key=96ce3a7643ee687976e47edd484f00d1";
    ArrayList currencyList = null;
    LoadData loadData = new LoadData();
    LoadCourse loadCourse;
    Spinner spinnerCurrencyFrom, spinnerCurrencyTo;
    MainActivity that = this;
    String currencyFrom, currencyTo;
    EditText inputAmount;
    TextView resultAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData.execute();
        inputAmount = findViewById(R.id.inputAmount);
        resultAmount = findViewById(R.id.resultAmount);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputAmount.getText().toString().isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setTitle("Error!")
                            .setMessage("Amount is empty. Please enter the amount.")
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog errorAlert = alertBuilder.create();
                    errorAlert.show();
                } else {
                    loadCourse = new LoadCourse();
                    loadCourse.execute();
                }
            }
        });
    }

    @Override
    public void processFinish(ArrayList output) {
        currencyList = output;
    }

    class LoadData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = "http://apilayer.net/api/list" + access_key;
            String content = "";
            try {
                content = getContent(path);
            } catch (IOException e) {
                Log.e("IOException", "Error: " + e.toString());
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            ArrayList<String> results = parseJSON(content);
            spinnerCurrencyFrom = findViewById(R.id.spinnerCurrencyFrom);
            spinnerCurrencyTo = findViewById(R.id.spinnerCurrencyTo);
            ArrayAdapter<String> currencyFromAdapter = new ArrayAdapter<String>(that, android.R.layout.simple_spinner_item, results);
            spinnerCurrencyFrom.setAdapter(currencyFromAdapter);
            spinnerCurrencyTo.setAdapter(currencyFromAdapter);
            spinnerCurrencyFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currencyFrom = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            spinnerCurrencyTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currencyTo = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(path);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return buf.toString();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        private ArrayList<String> parseJSON(String content) {
            ArrayList currencyList = new ArrayList<String>();
            try {
                JSONObject jObject = new JSONObject(content).getJSONObject("currencies");
                Iterator iterator = jObject.keys();

                while (iterator.hasNext()) {
                    currencyList.add(iterator.next());
                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
            return currencyList;
        }

    }

    class LoadCourse extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String path = "http://apilayer.net/api/live" + access_key + "&currencies=" + currencyFrom + "," + currencyTo + "&source=USD&format=1";
            String content = "";
            try {
                content = getContent(path);

            } catch (IOException e) {
                Log.e("IOException", "Error: " + e.toString());
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            ArrayList<Double> courseArray;
            courseArray = parseJSON(content);
            double calculatedAmount = Double.parseDouble(inputAmount.getText().toString()) * calculateCourse(courseArray);
            resultAmount.setText(Double.toString(calculatedAmount));
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(path);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return buf.toString();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        private ArrayList<Double> parseJSON(String content) {
            ArrayList<Double> infoList = new ArrayList<>();
            try {
                JSONObject jObject = new JSONObject(content).getJSONObject("quotes");
                Iterator<String> iterator = jObject.keys();

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    infoList.add(jObject.getDouble(key));
                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
            return infoList;
        }

        private Double calculateCourse(ArrayList<Double> courseArray) {
            if (courseArray.size() == 1)
                return 1.0;
            return roundDown5(courseArray.get(1)/courseArray.get(0));
        }

        private double roundDown5(double d) {
            return Math.floor(d * 1e5) / 1e5;
        }
    }
}
