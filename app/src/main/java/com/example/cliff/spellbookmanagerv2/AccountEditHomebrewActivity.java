package com.example.cliff.spellbookmanagerv2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class AccountEditHomebrewActivity extends AppCompatActivity {
    Button button;
    Intent intent;
    int homebrewId;
    Spinner schoolSpinner;
    String spellSchool;
    HashMap homebrewInfoHashMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create_homebrew);
        intent = getIntent();
        homebrewId = intent.getIntExtra("homebrewId", -1);
        button = findViewById(R.id.create_homebrew_activity_submit);

        schoolSpinner = findViewById(R.id.create_homebrew_school_spinner);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positon, long id) {
                spellSchool = adapterView.getItemAtPosition(positon).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spellSchool = adapterView.getItemAtPosition(schoolSpinner.getCount()).toString();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spell_schools, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(adapter);

        callApiGetHomebrewById callApiGetHomebrewById = new callApiGetHomebrewById();
        callApiGetHomebrewById.execute("getHomebrewById", homebrewId+"");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homebrewInfoHashMap = processHomebrewInformation();
                callApiEditHomebrew call = new callApiEditHomebrew();
                call.execute("editHomebrew",
                        homebrewId+"",
                        homebrewInfoHashMap.get("spellName").toString(),
                        homebrewInfoHashMap.get("castingTime").toString(),
                        homebrewInfoHashMap.get("components").toString(),
                        homebrewInfoHashMap.get("material").toString(),
                        homebrewInfoHashMap.get("ritual").toString(),
                        homebrewInfoHashMap.get("concentration").toString(),
                        homebrewInfoHashMap.get("description").toString(),
                        homebrewInfoHashMap.get("duration").toString(),
                        homebrewInfoHashMap.get("level").toString(),
                        homebrewInfoHashMap.get("spellRange").toString(),
                        homebrewInfoHashMap.get("school").toString(),
                        homebrewInfoHashMap.get("belongsToClasses").toString());
                Intent intent = new Intent(getBaseContext(), AccountManageHomebrewActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    private class callApiEditHomebrew extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String homebrewId = params[1];
            String spellName = params[2];
            String castingTime = params[3];
            String components = params[4];
            String material = params[5];
            String ritual = params[6];
            String concentration = params[7];
            String description = params[8];
            String duration = params[9];
            String level = params[10];
            String spellRange = params[11];
            String school = params[12];
            String belongsToClasses = params[13];

            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") +"&"+
                        URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8") +"&"+
                        URLEncoder.encode("spellName", "UTF-8") + "=" + URLEncoder.encode(spellName, "UTF-8") +"&"+
                        URLEncoder.encode("castingTime", "UTF-8") + "=" + URLEncoder.encode(castingTime, "UTF-8") +"&"+
                        URLEncoder.encode("components", "UTF-8") + "=" + URLEncoder.encode(components, "UTF-8") +"&"+
                        URLEncoder.encode("material", "UTF-8") + "=" + URLEncoder.encode(material, "UTF-8") +"&"+
                        URLEncoder.encode("ritual", "UTF-8") + "=" + URLEncoder.encode(ritual, "UTF-8") +"&"+
                        URLEncoder.encode("concentration", "UTF-8") + "=" + URLEncoder.encode(concentration, "UTF-8") +"&"+
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") +"&"+
                        URLEncoder.encode("duration", "UTF-8") + "=" + URLEncoder.encode(duration, "UTF-8") +"&"+
                        URLEncoder.encode("level", "UTF-8") + "=" + URLEncoder.encode(level, "UTF-8") +"&"+
                        URLEncoder.encode("spellRange", "UTF-8") + "=" + URLEncoder.encode(spellRange, "UTF-8") +"&"+
                        URLEncoder.encode("school", "UTF-8") + "=" + URLEncoder.encode(school, "UTF-8") +"&"+
                        URLEncoder.encode("belongsToClasses", "UTF-8") + "=" + URLEncoder.encode(belongsToClasses, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                response = result.toString();

            }
            catch (Exception e) {
                response = e.toString();
            }

            return response; //result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("onpost", result);

        }
    }

    private class callApiGetHomebrewById extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String homebrewId = params[1];

            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") +"&"+
                        URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                response = result.toString();

            }
            catch (Exception e) {
                response = e.toString();
            }

            return response; //result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.split("\\|")[0].equals("error")) {
                Log.d("onpost", result);
                loadHomebrewIntoFields(result.split("\\|"));
            } else {
                Log.d("onpost", "error" + result);
            }
        }
    }

    public void loadHomebrewIntoFields(String homebrewArray[]) {
        LinearLayout createHomebrewEditTextsContainer = findViewById(R.id.create_homebrew_edit_texts);
        ConstraintLayout classCheckBoxesContainer = findViewById(R.id.create_homebrew_class_checkboxes);

        for (int i = 0; i < createHomebrewEditTextsContainer.getChildCount(); i++) {
            if (createHomebrewEditTextsContainer.getChildAt(i) instanceof EditText) {
                ((EditText)createHomebrewEditTextsContainer.getChildAt(i)).setText(homebrewArray[i+1]);
            }
        }

        String classes[] = homebrewArray[12].split(",");
        for (int i = 0; i < classCheckBoxesContainer.getChildCount(); i++) {
            if (classCheckBoxesContainer.getChildAt(i) instanceof CheckBox) {
                for (int j = 0; j < classes.length; j++) {
                    if (((CheckBox) classCheckBoxesContainer.getChildAt(i)).getText().equals(classes[j])) {
                        ((CheckBox) classCheckBoxesContainer.getChildAt(i)).setChecked(true);
                    }
                }
            }
        }

        for (int i = 0; i < schoolSpinner.getAdapter().getCount(); i++) {
            if (schoolSpinner.getAdapter().getItem(i).toString().equalsIgnoreCase(homebrewArray[11])) {
                schoolSpinner.setSelection(i);
            }
        }
    }

    public HashMap<String, String> processHomebrewInformation() {
        String characterFieldsArray[] = {"spellName", "castingTime", "components", "material", "ritual", "concentration", "description",
                "duration", "level", "spellRange", "school", "belongsToClasses"};
        HashMap<String, String> hashMap = new HashMap<String, String>();

        LinearLayout createHomebrewEditTextsContainer = findViewById(R.id.create_homebrew_edit_texts);
        ConstraintLayout classCheckBoxesContainer = findViewById(R.id.create_homebrew_class_checkboxes);
        ArrayList<String> homebrewInformation = new ArrayList<>();
        for (int i = 0; i < createHomebrewEditTextsContainer.getChildCount(); i++) {
            if (createHomebrewEditTextsContainer.getChildAt(i) instanceof EditText) {
                homebrewInformation.add( ((EditText)createHomebrewEditTextsContainer.getChildAt(i)).getText().toString() );
            }
        }

        StringBuilder classCheckBoxStrings = new StringBuilder();
        for (int i = 0; i < classCheckBoxesContainer.getChildCount(); i++) {
            if (classCheckBoxesContainer.getChildAt(i) instanceof CheckBox) {
                if (((CheckBox) classCheckBoxesContainer.getChildAt(i)).isChecked()) {
                    classCheckBoxStrings.append(((CheckBox) classCheckBoxesContainer.getChildAt(i)).getText().toString());
                    classCheckBoxStrings.append(",");
                }
            }
        }

        homebrewInformation.add(spellSchool);
        homebrewInformation.add(classCheckBoxStrings.toString());

        for(int i = 0; i < characterFieldsArray.length ; i++) {
            hashMap.put(characterFieldsArray[i], homebrewInformation.get(i));
        }

        return hashMap;
    }
}
