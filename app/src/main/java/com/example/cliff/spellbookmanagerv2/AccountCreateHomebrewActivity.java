package com.example.cliff.spellbookmanagerv2;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.Map;

public class AccountCreateHomebrewActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    LinearLayout createHomebrewEditTextsContainer;
    Spinner schoolSpinner;
    ConstraintLayout classCheckboxesContainer;
    Button submitHomebrew;
    ArrayList<String> homebrewInformation;
    StringBuilder classCheckBoxStrings;
    String spellSchool;
    HashMap homebrewInfoHashMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create_homebrew);
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);

        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        if (!loggedIn) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("setFragment", R.layout.fragment_all_characters);
            startActivity(intent);
            overridePendingTransition(0,0);
        }

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


        submitHomebrew = findViewById(R.id.create_homebrew_activity_submit);
        submitHomebrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homebrewInformation = new ArrayList<>();
                classCheckBoxStrings = new StringBuilder();
                createHomebrewEditTextsContainer = findViewById(R.id.create_homebrew_edit_texts);
                classCheckboxesContainer = findViewById(R.id.create_homebrew_class_checkboxes);
                homebrewInfoHashMap = processHomebrewInformation();
                callApi call = new callApi();
                call.execute("createHomebrew", homebrewInfoHashMap.get("spellName").toString(),
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
                        homebrewInfoHashMap.get("belongsToClasses").toString(),
                        homebrewInfoHashMap.get("belongsToAccountId").toString());

                Iterator iterator = homebrewInfoHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry)iterator.next();
                    Log.d("homebrewInfo", "[" + pair.getKey() + "] => " + pair.getValue());
                }
            }
        });

    }

    public HashMap<String, String> processHomebrewInformation() {
        String characterFieldsArray[] = {"spellName", "castingTime", "components", "material", "ritual", "concentration", "description",
        "duration", "level", "spellRange", "school", "belongsToClasses", "belongsToAccountId"};
        HashMap<String, String> hashMap = new HashMap<String, String>();

        for (int i = 0; i < createHomebrewEditTextsContainer.getChildCount(); i++) {
            if (createHomebrewEditTextsContainer.getChildAt(i) instanceof EditText) {
                homebrewInformation.add( ((EditText)createHomebrewEditTextsContainer.getChildAt(i)).getText().toString() );
            }
        }
        classCheckBoxStrings = new StringBuilder();
        for (int i = 0; i < classCheckboxesContainer.getChildCount(); i++) {
            if (classCheckboxesContainer.getChildAt(i) instanceof CheckBox) {
                if (((CheckBox) classCheckboxesContainer.getChildAt(i)).isChecked()) {
                    classCheckBoxStrings.append(((CheckBox) classCheckboxesContainer.getChildAt(i)).getText().toString());
                    classCheckBoxStrings.append(",");
                }
            }
        }

        homebrewInformation.add(spellSchool);
        homebrewInformation.add(classCheckBoxStrings.toString());
        homebrewInformation.add(sharedPreferences.getInt("accountId", -1)+"");

        for(int i = 0; i < characterFieldsArray.length ; i++) {
            hashMap.put(characterFieldsArray[i], homebrewInformation.get(i));
        }


        return hashMap;
    }

    private class callApi extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String spellName = params[1];
            String castingTime = params[2];
            String components = params[3];
            String material = params[4];
            String ritual = params[5];
            String concentration = params[6];
            String description = params[7];
            String duration = params[8];
            String level = params[9];
            String spellRange = params[10];
            String school = params[11];
            String belongsToClasses = params[12];
            String belongsToAccountId = params[13];

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
                        URLEncoder.encode("belongsToClasses", "UTF-8") + "=" + URLEncoder.encode(belongsToClasses, "UTF-8") +"&"+
                        URLEncoder.encode("belongsToAccountId", "UTF-8") + "=" + URLEncoder.encode(belongsToAccountId, "UTF-8"));
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

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String response[] = result.split("\\|");
            switch (response[0]) {
                case "createHomebrew": {
                    if (response[1].equals("success")) {
                        Log.d("onPost", result);
                        Toast.makeText(getBaseContext(), "Homebrew spell created.", Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(getBaseContext(), AccountManageHomebrewActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    } else {
                        Log.d("onPost", result);
                        Toast.makeText(getBaseContext(), "Error" + response[3], Toast.LENGTH_LONG).show();
                    }
                }
                default: {
                    Log.d("onPostDefault", result);
                    Toast.makeText(getBaseContext(), "Error" + result, Toast.LENGTH_LONG).show();
                    break;
                }
            }

        }
    }
}
