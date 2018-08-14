package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountFragment extends Fragment {
    Button loginButton;
    Button registerButton;
    EditText usernameEditText;
    EditText passwordEditText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        usernameEditText = view.findViewById(R.id.username_entry);
        passwordEditText = view.findViewById(R.id.password_entry);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String salt = "Counterstrike: Global offensive";
                String username = usernameEditText.getText().toString();
                String password = sha512(passwordEditText.getText().toString() + salt);
                callApi call = new callApi();
                call.execute("login", username, password);
                Toast.makeText(getContext(), "Logging in please wait.", Toast.LENGTH_LONG).show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                int passes = 0; // needs to equal 3
                if (username.trim().length() >= 2) {
                    passes += 1;
                }

                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(password);
                boolean found = matcher.find();
                if (!found && password.length() >= 8) {
                    passes += 1;
                    String salt = "Counterstrike: Global offensive";
                    password = sha512(password + salt);
                }

                if (passes == 2) {
                    callApi call = new callApi();
                    call.execute("createAccount", username, password);
                    Toast.makeText(getContext(), "Creating account, please wait.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Name must be > 2 characters and password must be 8 characters long with no white spaces", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }

    public String sha512(String stringToHash) {
        String hash = "";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-512");
            digest.update(stringToHash.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }

            hash = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.d("error", e.toString());
        }

        return hash;
    }

    private class callApi extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String username = params[1];
            String password = params[2];
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
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +"&"+
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8"));
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
                case "createAccount": {
                    if (response[1].equals("success")) {
                        Log.d("onPostcreateAccount", response[2] + ":" + response[3]);
                        Toast.makeText(getContext(), "Account created, logged in", Toast.LENGTH_LONG).show();
                        sharedPreferences = getContext().getSharedPreferences("loggedIn", Context.MODE_PRIVATE);
                        editor = getContext().getSharedPreferences("loggedIn", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("loggedIn", true);
                        editor.putInt("accountId", Integer.parseInt(response[2]));
                        editor.putString("accountName", response[3]);
                        editor.commit();
                        Intent intent = new Intent(getContext(), AccountLoggedInActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                    } else {
                        Log.d("onPostcreateAccount", response[3]);
                        Toast.makeText(getContext(), "Error " + response[3], Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case "login": {
                    if (response[1].equals("success")) {
                        Log.d("onPostlogin", response[2] + ":" + response[3]);
                        Toast.makeText(getContext(), "Logged in", Toast.LENGTH_LONG).show();
                        sharedPreferences = getContext().getSharedPreferences("loggedIn", Context.MODE_PRIVATE);
                        editor = getContext().getSharedPreferences("loggedIn", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("loggedIn", true);
                        editor.putInt("accountId", Integer.parseInt(response[2]));
                        editor.putString("accountName", response[3]);
                        editor.commit();
                        Intent intent = new Intent(getContext(), AccountLoggedInActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                    } else {
                        Log.d("onPostlogin", response[3]);
                        Toast.makeText(getContext(), "Error " + response[3], Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                default: {
                    Log.d("onPostDefault", result);
                    Toast.makeText(getContext(), "Error" + result, Toast.LENGTH_LONG).show();
                    break;
                }
            }

        }
    }
}
