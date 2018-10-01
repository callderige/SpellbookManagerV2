package com.example.cliff.spellbookmanagerv2;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CallApi extends AsyncTask<String, String, String> {
    private String mAction = "";
    private WeakReference<View> mWeakReferenceView;

    public CallApi(View view) {
        this.mWeakReferenceView = new WeakReference<>(view);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String apiResponse = "";
        //String action = params[0];
        mAction = params[0];
        switch (mAction) {
            /*-------------------------
            Homebrew related API calls
            --------------------------*/
            case "createHomebrew": {

                break;
            }
            case "deleteHomebrew": {

                break;
            }
            case "editHomebrew": {

                break;
            }
            case "getHomebrewById": {

                break;
            }
            case "getYourHomebrew": {

                break;
            }

            /*----------------------------------
            Account functions related API calls
            -----------------------------------*/
            case "createAccount": {

                break;
            }
            case "login": {

                break;
            }

            /*-------------------------
            Comments related API calls
            --------------------------*/
            case "createComment": {

                break;
            }
            case "deleteComment": {

                break;
            }
            case "editComment": {

                break;
            }
            case "getComments": {

                break;
            }
            case "getYourComments": {

                break;
            }

            /*-----------------------
            Rating related API calls
            ------------------------*/
            case "getRating": {

                break;
            }
            case "setRating": {

                break;
            }

            /*-------------------------------
            Local database related API calls
            --------------------------------*/
            case "syncHomebrew": {
                apiResponse = syncHomebrew(params);
                break;
            }

            default: {
                break;
            }
        }

        return apiResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(result);
        switch (mAction) {
            /*-------------------------------
            Local database related API calls
            --------------------------------*/
            case "syncHomebrew": {
                syncHomebrewResponse(result);
                break;
            }
        }
    }

    private String syncHomebrew(String[] apiParams) {
        StringBuilder apiResponse = new StringBuilder();
        String action = apiParams[0];
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
                apiResponse.append(line);
            }
        }
        catch (Exception e) {
            Log.d("Exception", apiResponse.toString());
        }

        return apiResponse.toString();
    }

    private void syncHomebrewResponse(String apiResponse) {
        View view = mWeakReferenceView.get().getRootView();
        String cleanedResult = apiResponse.substring(apiResponse.lastIndexOf("|~|") + 3);
        String remoteHomebrew[]  = cleanedResult.split("<br>");
        if (remoteHomebrew[0].equalsIgnoreCase("error")) {
            Snackbar snackbar = Snackbar.make(mWeakReferenceView.get().getRootView(), "Error connecting to homebrew databse.", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            ArrayList<String> homebrewToSync = new ArrayList<>();
            DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
            for (int i = 0; i < remoteHomebrew.length; i++) {
                homebrewToSync.add(remoteHomebrew[i]);
            }

            if (homebrewToSync.size() > 0) {
                databaseHelper.syncHomebrewContent(homebrewToSync);
                Log.d("check", homebrewToSync.get(0));
            } else {
                Log.d("check", "no found");
            }
        }
    }
}
