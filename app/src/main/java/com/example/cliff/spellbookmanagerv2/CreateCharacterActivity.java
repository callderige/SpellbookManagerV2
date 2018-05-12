package com.example.cliff.spellbookmanagerv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateCharacterActivity extends AppCompatActivity {
    Button button;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);
        databaseHelper = new DatabaseHelper(this);

        button = findViewById(R.id.submit_new_character);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout createCharacterLayout = findViewById(R.id.create_character_constraint_layout);
                ArrayList<String> characterInformationEditTextsStrings = new ArrayList<>();
                StringBuilder classCheckboxStrings = new StringBuilder();

                for (int i = 0; i < createCharacterLayout.getChildCount(); i++) {
                    if (createCharacterLayout.getChildAt(i) instanceof EditText) {
                        characterInformationEditTextsStrings.add( ((EditText) createCharacterLayout.getChildAt(i)).getText().toString());
                    }
                    if (createCharacterLayout.getChildAt(i) instanceof CheckBox) {
                        if (((CheckBox) createCharacterLayout.getChildAt(i)).isChecked()) {
                            classCheckboxStrings.append(((CheckBox) createCharacterLayout.getChildAt(i)).getText().toString());
                            classCheckboxStrings.append(",");
                        }
                    }
                }

                String characterFieldsArray[] = {"characterName", "characterLevel", "proficiencyBonus", "intelligence", "widsom", "charisma", "classes"};
                HashMap<String, String> hashMap = new HashMap<String, String>();

                for(int i = 0; i < characterFieldsArray.length-1 ; i++) {
                    hashMap.put(characterFieldsArray[i], characterInformationEditTextsStrings.get(i));
                }
                hashMap.put(characterFieldsArray[6], classCheckboxStrings.toString());

                if (verifyInputs(characterInformationEditTextsStrings, classCheckboxStrings)) {
                    databaseHelper.createCharacter(hashMap.get("characterName"), hashMap.get("proficiencyBonus"), hashMap.get("intelligence"),
                            hashMap.get("widsom"), hashMap.get("charisma"), hashMap.get("classes"));
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("setFragment", R.layout.fragment_all_characters);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    Toast.makeText(getBaseContext(), hashMap.get("characterName"), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "All fields and atleast 1 class is required.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean verifyInputs (ArrayList<String> characterInformationEditTexts, StringBuilder classCheckboxes) {
        byte verifyCounter = 0;
        boolean verified = false;

        for (int i = 0; i < characterInformationEditTexts.size(); i++) {
            if (characterInformationEditTexts.get(i).length() > 0) {
                verifyCounter += 1;
                Log.d("verify charinfo", verifyCounter+"");
            }
        }

        if (classCheckboxes.length() > 0) {
            verifyCounter += 1;
            Log.d("verify charinfo", verifyCounter+"");
        }

        if (verifyCounter == 7) {
            verified = true;
        }

        return verified;
    }
}
