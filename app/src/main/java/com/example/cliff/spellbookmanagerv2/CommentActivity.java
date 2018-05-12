package com.example.cliff.spellbookmanagerv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class CommentActivity extends AppCompatActivity {
    Intent intent;
    SharedPreferences sharedPreferences;
    Button submitButton;
    EditText commentExitText;
    ListView commentListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        intent = getIntent();
        sharedPreferences = getSharedPreferences("loggedIn", MODE_PRIVATE);
        final int correspondingHomebrewId = intent.getIntExtra("correspondingHomebrewId", -1);
        final int accountId = sharedPreferences.getInt("accountId", -1);
        submitButton = findViewById(R.id.create_comment_submit);
        commentExitText = findViewById(R.id.create_comment_edit_text);
        commentListView = findViewById(R.id.listview_homebrew_comments);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi call = new callApi(getBaseContext());
                call.execute("createComment", commentExitText.getText().toString(), accountId+"", correspondingHomebrewId+"");
                intent.putExtra("correspondingHomebrewId", correspondingHomebrewId);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        callApiGenerateCommentsListView callComments = new callApiGenerateCommentsListView(this);
        callComments.execute("getComments", correspondingHomebrewId+"");
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
            String comment = params[1];
            String accountId = params[2];
            String homebrewId = params[3];

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
                        URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8") +"&"+
                        URLEncoder.encode("accountId", "UTF-8") + "=" + URLEncoder.encode(accountId, "UTF-8") +"&"+
                        URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8"));

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


            return response; //result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("whatwentwrong", result);
        }
    }

    private class callApiGenerateCommentsListView extends AsyncTask<String, Integer, String> {
        private Context mContext;

        public callApiGenerateCommentsListView (Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params)  {
            String action = params[0];
            String homebrewId = params[1];

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
                        URLEncoder.encode("homebrewId", "UTF-8") + "=" + URLEncoder.encode(homebrewId, "UTF-8"));

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


            return response; //result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ArrayList<String> homebrewComments = new ArrayList<>();
            if (!result.equals("getYourHomebrew|failed|")) {
                String rows[] = result.split("<br>");
                for (int i = 0; i < rows.length; i++) {
                    homebrewComments.add(rows[i]);
                }
            } else {
                homebrewComments.add("This spell has no comments yet.");
            }
            CommentsAdapter adapter = new CommentsAdapter(getBaseContext(), R.layout.listview_comment, homebrewComments);
            commentListView.setAdapter(adapter);
            //commentListView.setOnItemClickListener(onItemClickHandler);
        }
    }

    public class CommentsAdapter extends ArrayAdapter<String> {
        public CommentsAdapter(Context context, int resource, ArrayList<String> homebrewSpellNames) {
            super(context, resource, homebrewSpellNames);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String str = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_comment, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.comment);
            //String id = str.split("\\|")[0];
            String comment = str;
            //textView.setTag("id_"+id);
            textView.setText(comment);
            return convertView;
        }
    }

}
