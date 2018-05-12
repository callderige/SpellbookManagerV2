package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class SearchFragment extends Fragment {
    DatabaseHelper databaseHelper;
    View view;
    String spells[];
    Button button;
    ImageButton imageButton;
    CheckBox classCheckBox;
    EditText editText;
    protected ListView listView;
    Intent intent;
    int characterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(this.getContext());
        intent = new Intent(getContext(), SpellDetailsActivity.class);
        view = inflater.inflate(R.layout.fragment_search, container, false);
        if (getArguments() != null) {
            characterId = getArguments().getInt("characterId");
        } else {
            characterId = -1;
        }
        classCheckBox = view.findViewById(R.id.search_by_class);
        editText = view.findViewById(R.id.search_for_spell);
        //callApi call = new callApi(getContext());
        //call.execute("syncDatabase");
        //callApiHomebrew call2 = new callApiHomebrew(getContext());
        //call2.execute("syncHomebrew");
        callApiHomebrewSync call3 = new callApiHomebrewSync(getContext());
        call3.execute("syncHomebrew");
        imageButton = view.findViewById(R.id.submit_spell_search);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchQuery = editText.getText().toString();
                ArrayList<String> searchResult;
                if (classCheckBox.isChecked()) {
                    searchResult = databaseHelper.findSpellByClass(searchQuery);
                } else {
                    searchResult = databaseHelper.findSearchedSpells(searchQuery);
                }
                searchResultsAdapter adapter = new searchResultsAdapter(view.getContext(), R.layout.listview_spells, searchResult);
                listView = view.findViewById(R.id.listview_spells);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(onItemClickHandler);
            }
        });

        classCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (classCheckBox.isChecked()) {
                    editText.setHint("Enter class name");
                } else {
                    editText.setHint("Enter spell name");
                }
            }
        });

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH){
                    String searchQuery = editText.getText().toString();
                    ArrayList<String> searchResult;
                    if (classCheckBox.isChecked()) {
                        searchResult = databaseHelper.findSpellByClass(searchQuery);
                    } else {
                        searchResult = databaseHelper.findSearchedSpells(searchQuery);
                    }
                    searchResultsAdapter adapter = new searchResultsAdapter(view.getContext(), R.layout.listview_spells, searchResult);
                    listView = view.findViewById(R.id.listview_spells);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(onItemClickHandler);
                }
                return false;
            }
        });
        return view;
    }

    public class searchResultsAdapter extends ArrayAdapter<String> {
        public searchResultsAdapter(Context context, int resource, ArrayList<String> spellSearchResults) {
            super(context, resource, spellSearchResults);
        }

        @Override @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_spells, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.spell_name);
            String id = str.split("\\|")[0];
            String spellName = str.split("\\|")[1];
            textView.setTag("id_"+id);
            textView.setText(spellName);
            return convertView;
        }
    }

    public AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            TextView textView = view.findViewById(R.id.spell_name);
            int spellId = Integer.parseInt(textView.getTag().toString().replace("id_",""));
            intent.putExtra("correspondingSpellId", spellId);
            intent.putExtra("characterId", characterId);
            startActivity(intent);
            getActivity().overridePendingTransition(0,0);
        }
    };



    private class callApi extends AsyncTask<String, Integer, String> {
        private Context mContext;

        public callApi (Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8"));
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
            //super.onPostExecute(result);
            spells = result.split("<br>");
            DatabaseHelper databaseHelper2 = new DatabaseHelper(mContext);
            for (int i = 0; i < spells.length; i++) {
                String spellColumns[] = spells[i].split("\\|");
                String name = spellColumns[1];
                String castingTime = spellColumns[2];
                String components = spellColumns[3];
                String material = spellColumns[4];
                String ritual = spellColumns[5];
                String concentration = spellColumns[6];
                String description = spellColumns[7];
                String duration = spellColumns[8];
                String level = spellColumns[9];
                String spellRange = spellColumns[10];
                String school = spellColumns[11];
                String classes = spellColumns[12];
                databaseHelper2.populateTableWithOfficial(name, castingTime, components, material, ritual, concentration, description, duration, level, spellRange, school, classes);
            }
            Log.d("complete", "sync complete");
        }
    }

    private class callApiHomebrew extends AsyncTask<String, Integer, String> {
        private Context mContext;

        public callApiHomebrew (Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8"));
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
            //super.onPostExecute(result);
            String homebrew[]  = result.split("<br>");
            DatabaseHelper databaseHelper2 = new DatabaseHelper(mContext);
            for (int i = 0; i < homebrew.length; i++) {
                String spellColumns[] = homebrew[i].split("\\|");
                String name = spellColumns[1];
                String castingTime = spellColumns[2];
                String components = spellColumns[3];
                String material = spellColumns[4];
                String ritual = spellColumns[5];
                String concentration = spellColumns[6];
                String description = spellColumns[7];
                String duration = spellColumns[8];
                String level = spellColumns[9];
                String spellRange = spellColumns[10];
                String school = spellColumns[11];
                String classes = spellColumns[12];
                String isSpellHomebrew = "true";
                int homebrewId = Integer.parseInt(spellColumns[0]);
                int belongsToAccountId = Integer.parseInt(spellColumns[13]);
                databaseHelper2.populateTableWithHomebrew(name, castingTime, components, material, ritual, concentration, description, duration, level, spellRange, school, classes, isSpellHomebrew, homebrewId, belongsToAccountId);
            }
            Log.d("complete", "sync complete");
        }
    }

    private class callApiHomebrewSync extends AsyncTask<String, Integer, String> {
        private Context mContext;

        public callApiHomebrewSync (Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8"));
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
            //super.onPostExecute(result);
            String homebrew[]  = result.split("<br>");
            ArrayList<String> homebrewToSync = new ArrayList<>();
            DatabaseHelper databaseHelper2 = new DatabaseHelper(mContext);
            for (int i = 0; i < homebrew.length; i++) {
                homebrewToSync.add(homebrew[i]);
            }

            if (homebrewToSync.size() > 0) {
                databaseHelper2.syncHomebrewContent(homebrewToSync);
                Log.d("check", homebrewToSync.get(0));
            } else {
                Log.d("check", "no found");
            }
        }
    }
}
