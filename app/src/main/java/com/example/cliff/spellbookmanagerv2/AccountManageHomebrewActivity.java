package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class AccountManageHomebrewActivity extends AppCompatActivity {
    ListView myHomebrewSpellsListView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_characters:
                    if (!item.isChecked()) {
                        Intent intent2 = new Intent(getBaseContext(), MainActivity.class);
                        intent2.putExtra("setFragment", R.layout.fragment_all_characters);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                    }
                    return true;
                case R.id.navigation_search:
                    if (!item.isChecked()) {
                        Intent intent2 = new Intent(getBaseContext(), MainActivity.class);
                        intent2.putExtra("setFragment", R.layout.fragment_search);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                    }
                    return true;
                case R.id.navigation_account:
                    if (!item.isChecked()) {

                    }
                    return true;
            }
            return false;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_logged_in_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account_logged_in_manage_homebrew: {
                Log.d("accountIn", "manage");
                Intent intent = new Intent(getBaseContext(), AccountManageHomebrewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.account_logged_in_create_homebrew: {
                Log.d("accountIn", "homebrew");
                Intent intent = new Intent(getBaseContext(), AccountCreateHomebrewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.account_logged_in_manage_comments: {
                Log.d("accountIn", "settings");
                Intent intent = new Intent(getBaseContext(), AccountYourCommentsActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.account_logged_in_settings: {
                Log.d("accountIn", "settings");
                Intent intent = new Intent(getBaseContext(), AccountLoggedInActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.account_logged_in_logout: {
                sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
                editor = getBaseContext().getSharedPreferences("loggedIn", Context.MODE_PRIVATE).edit();
                editor.putBoolean("loggedIn", false);
                editor.putInt("accountId", -1);
                editor.putString("accountName", "null");
                editor.commit();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage_homebrew);
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_account);

        if (!loggedIn) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("setFragment", R.layout.fragment_all_characters);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
        myHomebrewSpellsListView = findViewById(R.id.listview_homebrew_spells);
        int accountId = sharedPreferences.getInt("accountId", -1);
        callApi call = new callApi();
        call.execute("getYourHomebrew", accountId+"", "ignore");
        myHomebrewSpellsListView.setOnItemClickListener(onItemClickHandler);
    }

    private class callApi extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String belongsToAccountId = params[1];
            String homebrewId = params[2];

            String response = "";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://ochofuzzycheese.000webhostapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                if (action.equals("getYourHomebrew")) {
                    writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") + "&" +
                            URLEncoder.encode("belongsToAccountId", "UTF-8") + "=" + URLEncoder.encode(belongsToAccountId, "UTF-8"));
                } else {
                    writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") + "&" +
                            URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8"));
                }
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
            Log.d("onpost", result);
            ArrayList<String> homebrewSpellNames = new ArrayList<>();
            if (!result.equals("Homebrew item deleted")) {
                if (!result.equals("getYourHomebrew|failed|")) {
                    String rows[] = result.split("<br>");
                    for (int i = 0; i < rows.length; i++) {
                        homebrewSpellNames.add(rows[i].split("\\|")[0] + "|" + rows[i].split("\\|")[1]);
                    }
                } else {
                    homebrewSpellNames.add("-1|You have not created any homebrew spells yet.");
                }
                MyHomebrewSpells adapter = new MyHomebrewSpells(getBaseContext(), R.layout.listview_all_characters, homebrewSpellNames);
                myHomebrewSpellsListView.setAdapter(adapter);
            }
        }
    }

    public class MyHomebrewSpells extends ArrayAdapter<String> {
        public MyHomebrewSpells(Context context, int resource, ArrayList<String> homebrewSpellNames) {
            super(context, resource, homebrewSpellNames);
        }

        @Override
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
            final TextView textView = view.findViewById(R.id.spell_name);
            AlertDialog alertDialog = new AlertDialog.Builder(AccountManageHomebrewActivity.this).create();
            alertDialog.setTitle("Choose option for " +textView.getText());
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DELETE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            callApi call = new callApi();
                            int homebrewId = Integer.parseInt(textView.getTag().toString().replace("id_", ""));
                            Log.d("gettag", "onItemClick: " +homebrewId);
                            call.execute("deleteHomebrew", "ignore", homebrewId+"");
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0,0);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "EDIT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int homebrewId = Integer.parseInt(textView.getTag().toString().replace("id_", ""));
                            Intent intent = new Intent(getBaseContext(), AccountEditHomebrewActivity.class);
                            dialog.dismiss();
                            intent.putExtra("homebrewId", homebrewId);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    };
}
