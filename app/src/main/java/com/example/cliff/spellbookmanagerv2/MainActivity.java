package com.example.cliff.spellbookmanagerv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    SearchFragment searchFragment = new SearchFragment();
    AllCharactersFragment allCharactersFragment = new AllCharactersFragment();
    AccountFragment accountFragment = new AccountFragment();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean loggedIn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_characters:
                    if (!item.isChecked()) {
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, allCharactersFragment).commit();
                    }
                    return true;
                case R.id.navigation_search:
                    if (!item.isChecked()) {
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, searchFragment).commit();
                    }
                    return true;
                case R.id.navigation_account:
                    if (!item.isChecked()) {
                        if (loggedIn) {
                            Intent intent = new Intent(getBaseContext(), AccountLoggedInActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        } else {
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content, accountFragment).commit();
                        }

                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        Log.d("logged", loggedIn +"");
        Intent intent = getIntent();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        //databaseHelper.clearLocalHomebrewSpells();
        //databaseHelper.onUpgrade(databaseHelper.getWritableDatabase(), 1,1);
        //databaseHelper.onCreate(databaseHelper.getWritableDatabase());
        int loadFragment = intent.getIntExtra("setFragment", -1);
        int characterId = intent.getIntExtra("characterId", -1);
        switch (loadFragment) {
            case R.layout.fragment_all_characters: {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, allCharactersFragment).commit();
                navigation.setSelectedItemId(R.id.navigation_characters);
                break;
            }
            case R.layout.fragment_search: {
                Bundle bundle = new Bundle();
                bundle.putInt("characterId", characterId);
                searchFragment.setArguments(bundle);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, searchFragment).commit();
                navigation.setSelectedItemId(R.id.navigation_search);
                break;
            }
            case R.layout.fragment_account: {
                if (loggedIn) {
                    intent = new Intent(getBaseContext(), AccountLoggedInActivity.class);
                    navigation.setSelectedItemId(R.id.navigation_account);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                } else {
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, accountFragment).commit();
                    navigation.setSelectedItemId(R.id.navigation_account);
                }
                break;
            }

            default:
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, allCharactersFragment).commit();
                navigation.setSelectedItemId(R.id.navigation_characters);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, allCharactersFragment).commit();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_characters);
    }
}
