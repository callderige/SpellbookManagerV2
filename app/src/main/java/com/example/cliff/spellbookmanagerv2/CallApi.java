package com.example.cliff.spellbookmanagerv2;

import android.os.AsyncTask;

public class CallApi extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder result = new StringBuilder();
        String action = params[0];

        try {
            switch (action) {
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

                    break;
                }

                default: {
                    break;
                }
            }
        } catch (Exception e) {
            result.append(e.toString());
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        /*-------------------------------
        Local database related API calls
        --------------------------------*/

    }
}
