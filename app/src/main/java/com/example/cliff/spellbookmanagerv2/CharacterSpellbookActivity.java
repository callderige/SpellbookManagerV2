package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterSpellbookActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    ArrayList<String> characterInfo;
    Intent intent;
    int characterId;
    FloatingActionButton floatingActionButton;
    ListView listView;

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
                        Intent intent2 = new Intent(getBaseContext(), MainActivity.class);
                        intent2.putExtra("setFragment", R.layout.fragment_account);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.character_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_character: {
                return true;
            }
            case R.id.delete_character: {
                databaseHelper.deleteCharacter(characterId);
                intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("setFragment", R.layout.fragment_all_characters);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_spellbook);
        databaseHelper = new DatabaseHelper(this);
        intent = getIntent();
        characterId = intent.getIntExtra("character_id", -1);

        characterInfo = databaseHelper.findCharacterById(characterId);

        TextView textView = findViewById(R.id.spellbook_character_name);
        textView.setText(characterInfo.get(1));
        textView = findViewById(R.id.spellbook_character_classes);
        textView.setText(characterInfo.get(6));
        textView = findViewById(R.id.spellbook_character_proficiency_value);
        textView.setText(characterInfo.get(2));
        textView = findViewById(R.id.spellbook_character_intelligence_value);
        textView.setText(characterInfo.get(3) + " [" + getModifier(characterInfo.get(3))+ "]");
        textView = findViewById(R.id.spellbook_character_wisdom_value);
        textView.setText(characterInfo.get(4) + " [" + getModifier(characterInfo.get(4))+ "]");
        textView = findViewById(R.id.spellbook_character_charisma_value);
        textView.setText(characterInfo.get(5) + " [" + getModifier(characterInfo.get(5))+ "]");

        floatingActionButton = findViewById(R.id.begin_search_to_add_to_character);
        listView = findViewById(R.id.listview_character_spellbook);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("setFragment", R.layout.fragment_search);
                intent.putExtra("characterId", characterId);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        ArrayList<String> allSpellsInCharacterSpellbook = databaseHelper.findAllSpellsInCharacterSpellbook(characterId);
        if (allSpellsInCharacterSpellbook.size() <= 0) {
            TextView textView1 = findViewById(R.id.no_spells_in_spellbook);
            textView1.setVisibility(View.VISIBLE);
        } else {
            TextView textView1 = findViewById(R.id.no_spells_in_spellbook);
            textView1.setVisibility(View.GONE);
        }

        AllSpellsInCharacterSpellbookAdapter adapter = new AllSpellsInCharacterSpellbookAdapter(
                this, R.layout.listview_spells, allSpellsInCharacterSpellbook);
        listView = findViewById(R.id.listview_character_spellbook);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String spellBookAndCorrespondingSpellId = view.getTag().toString().replace("spellbookId_correspondingSpellId", "");
                String idArray[] = spellBookAndCorrespondingSpellId.split("\\|");
                Intent intent = new Intent(getBaseContext(), SpellDetailsActivity.class);
                intent.putExtra("characterId", characterId);
                intent.putExtra("viewingFromSpellbook", true);
                intent.putExtra("spellbookId", Integer.parseInt(idArray[0]));
                intent.putExtra("correspondingSpellId", Integer.parseInt(idArray[1]));
                Log.d("idk", idArray[0] + " " +idArray[1]);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public class AllSpellsInCharacterSpellbookAdapter extends ArrayAdapter<String> {
        public AllSpellsInCharacterSpellbookAdapter(Context context, int resource, ArrayList<String> allCharacters) {
            super(context, resource, allCharacters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_spells, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.spell_name);
            String spellbookId = str.split("\\|")[0];
            String correspondingSpellId = str.split("\\|")[2];
            String characterName = str.split("\\|")[1];
            textView.setTag("spellbookId_correspondingSpellId" + spellbookId + "|" + correspondingSpellId);
            textView.setText(characterName);
            return convertView;
        }
    }

    public int getModifier(String abilityScore) {
        int modifier = Integer.parseInt(abilityScore);
        double floorMod = Math.floor((modifier-10)/2);
        modifier = (int) floorMod;
        return modifier;
    }
}
