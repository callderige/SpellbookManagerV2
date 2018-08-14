package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class SpellDetailsActivity extends AppCompatActivity {
    protected TextView textView;
    protected Intent intent;
    protected final String[] textViewIds = {"spell_name", "casting_time", "components", "material",
            "ritual", "concentration", "description", "duration", "level", "spell_range", "school", "classes"};
    protected final String[] textViewTitles = {"Spell name", "Casting time", "Components", "Material",
            "Ritual", "Concentration", "Description", "Duration", "Spell level", "Spell range", "School", "Classes"};
    protected FloatingActionButton floatingActionButton;
    int characterId;
    boolean viewingFromSpellbook;
    int spellbookId;
    DatabaseHelper databaseHelper;
    TextView homebrewRating;
    ImageButton rateHomebrew;
    Button startHomebrewCommentActivity;
    SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewingFromSpellbook) {
            getMenuInflater().inflate(R.menu.spell_detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_from_spellbook: {
                databaseHelper.deleteFromSpellbook(spellbookId);
                intent = new Intent(getBaseContext(), CharacterSpellbookActivity.class);
                intent.putExtra("character_id", characterId);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_details);
        databaseHelper = new DatabaseHelper(this);
        intent = getIntent();
        homebrewRating = findViewById(R.id.homebrew_rating);
        rateHomebrew = findViewById(R.id.rate_homebrew);
        startHomebrewCommentActivity = findViewById(R.id.start_homebrew_comment_activity);
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
        spellbookId = intent.getIntExtra("spellbookId", -1);
        int correspondingSpellId = intent.getIntExtra("correspondingSpellId", -1);
        characterId = intent.getIntExtra("characterId", -1);
        viewingFromSpellbook = intent.getBooleanExtra("viewingFromSpellbook", false);
        Log.d("checking", characterId+"");
        final ArrayList<String> spellDetails = databaseHelper.findSpellById(correspondingSpellId);
        if (spellDetails.get(13).equals("false")) {
            homebrewRating.setVisibility(View.GONE);
            rateHomebrew.setVisibility(View.GONE);
            startHomebrewCommentActivity.setVisibility(View.GONE);
        } else {
            callApi call = new callApi(this);
            call.execute("getRating", "ignore", spellDetails.get(14));
        }
        for (int i = 1; i < spellDetails.size()-3; i++) {
            int resourceId = getResources().getIdentifier(textViewIds[i-1], "id", getPackageName());
            if (resourceId != 0) {
                switch (textViewTitles[i-1]) {
                    case "Spell name": {
                        String details = spellDetails.get(i);
                        textView = findViewById(resourceId);
                        textView.setText(details);
                        break;
                    }
                    case "Materials": {
                        String details = textViewTitles[i-1] + "\n" + spellDetails.get(i);
                        if (details.equals("empty")) {
                            textView = findViewById(resourceId);
                            textView.setText("None");
                        } else {
                            textView = findViewById(resourceId);
                            textView.setText(details);
                        }
                        break;
                    }
                    default: {
                        String details = textViewTitles[i-1] + "\n" + spellDetails.get(i);
                        textView = findViewById(resourceId);
                        textView.setText(details);
                        break;
                    }
                }
            }
        }

        floatingActionButton = findViewById(R.id.add_to_character_spellbook);
        if (viewingFromSpellbook) {
            floatingActionButton.setVisibility(View.GONE);
        } else {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (characterId == -1) {
                        Log.d("characterId", "Adding from search" + characterId);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("setFragment", R.layout.fragment_all_characters);
                        intent.putExtra("addingFromSearch", true);
                        intent.putExtra("addingFromSearchSpellName", spellDetails.get(1));
                        intent.putExtra("addingFromSearchSpellId", Integer.parseInt(spellDetails.get(0)));
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    } else {
                        Log.d("characterId", "Adding from character" + characterId);
                        databaseHelper.addToSpellbook(spellDetails.get(1), Integer.parseInt(spellDetails.get(0)), characterId);
                        Intent intent = new Intent(getBaseContext(), CharacterSpellbookActivity.class);
                        intent.putExtra("character_id", characterId);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }

                }
            });
        }

        rateHomebrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
                if (loggedIn){
                    int accountId = sharedPreferences.getInt("accountId", -1);
                    String homebrewId = spellDetails.get(14);
                    callApi call = new callApi(view.getContext());
                    call.execute("setRating", accountId+"", homebrewId);
                } else {
                    Toast.makeText(getBaseContext(), "You need to have an account to rate", Toast.LENGTH_LONG).show();
                }
            }
        });

        startHomebrewCommentActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
                if (loggedIn) {
                    Intent commentIntent = new Intent(getBaseContext(), CommentActivity.class);
                    commentIntent.putExtra("correspondingHomebrewId", Integer.parseInt(spellDetails.get(14)));
                    startActivity(commentIntent);
                    overridePendingTransition(0, 0);
                }   else {
                    Toast.makeText(getBaseContext(), "You need to have an account to comment", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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
            String accountId = params[1];
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
                if (action.equals("getRating")) {
                    writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") +"&"+
                            URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8"));
                } else if (action.equals("setRating")) {
                    writer.write(URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8") +"&"+
                            URLEncoder.encode("accountId", "UTF-8") + "=" + URLEncoder.encode(accountId, "UTF-8") +"&"+
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
            try {
                int rating = Integer.parseInt(result);
                homebrewRating.setText(result);
            } catch (NumberFormatException nfe) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_spell_details_container), "Error retrieving homebrew rating", Snackbar.LENGTH_LONG);
                snackbar.show();
                homebrewRating.setText("0");
            }
        }
    }
}
