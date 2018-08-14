package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AccountLoggedInActivity extends AppCompatActivity {
    String accountName;
    int accountId;
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
        setContentView(R.layout.activity_account_logged_in);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_account);
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
        accountName = sharedPreferences.getString("accountName", "null");
        accountId = sharedPreferences.getInt("accountId", -1);
        TextView textView = findViewById(R.id.account_username);
        textView.setText(accountName + "'s settings");
    }
}
