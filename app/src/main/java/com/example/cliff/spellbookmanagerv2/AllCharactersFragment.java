package com.example.cliff.spellbookmanagerv2;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AllCharactersFragment extends Fragment {
    protected FloatingActionButton floatingActionButton;
    protected DatabaseHelper databaseHelper;
    protected ListView listView;
    protected View view;
    protected Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_characters, container, false);
        databaseHelper = new DatabaseHelper(view.getContext());
        boolean displayNotification = getActivity().getIntent().getBooleanExtra("addingFromSearch", false);
        TextView textView = view.findViewById(R.id.add_to_character_notification);
        if (!displayNotification) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }

        AllCharactersAdapter adapter = new AllCharactersAdapter(view.getContext(), R.layout.listview_all_characters, databaseHelper.findAllCharacters());
        listView = view.findViewById(R.id.listview_characters);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = Integer.parseInt(view.getTag().toString().replace("id_", ""));
                Intent fromSpellSearch = getActivity().getIntent();

                if (fromSpellSearch.getBooleanExtra("addingFromSearch", false)) {
                    int spellId = fromSpellSearch.getIntExtra("addingFromSearchSpellId", -1);
                    String spellName = fromSpellSearch.getStringExtra("addingFromSearchSpellName");
                    databaseHelper.addToSpellbook(spellName, spellId, id);
                    Log.d("info", spellId + spellName);
                }
                Intent intent = new Intent(view.getContext(), CharacterSpellbookActivity.class);
                intent.putExtra("character_id", id);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });

        floatingActionButton = view.findViewById(R.id.create_new_character);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateCharacterActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });

        return view;
    }

    public class AllCharactersAdapter extends ArrayAdapter<String> {
        public AllCharactersAdapter(Context context, int resource, ArrayList<String> allCharacters) {
            super(context, resource, allCharacters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_all_characters, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.character_name);
            String id = str.split("\\|")[0];
            String characterName = str.split("\\|")[1];
            textView.setTag("id_"+id);
            textView.setText(characterName);
            return convertView;
        }
    }
}
