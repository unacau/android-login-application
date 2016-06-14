package cz.ekishigo.mobilitiassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cz.ekishigo.mobilitiassignment.utils.Security;

public class LoginActivity extends AppCompatActivity {

    public final static String EXTRA_IMAGE = "cz.ekishigo.mobilityassignment.IMAGE";

    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mSignInButton;
    private ProgressBar mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameView = (EditText) findViewById(R.id.username_edit);
        mPasswordView = (EditText) findViewById(R.id.password_edit);
        mSignInButton = (Button) findViewById(R.id.sign_in_btn);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(v);
            }
        });

    }

    public void attemptLogin(View view) {
        String username = mUsernameView.getText().toString().toLowerCase();
        String password = mPasswordView.getText().toString().toLowerCase();
        // TODO: Validate
        LoginTask loginTask = new LoginTask(username, password);
        loginTask.execute();
    }


    /**
     * Created by ekishigo on 13.6.16.
     */
    public class LoginTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mPassword;

        public LoginTask(String username, String password) {
            this.mUsername = username;
            this.mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            // TODO: Show progress bar
        }

        @Override
        protected String doInBackground(Void... params) {
            return login(mUsername, mPassword);
        }

        @Override
        protected void onPostExecute(String rawResponse) {
            if (rawResponse != null) { // OK
                // TODO: save user
                nextActivity(clearResponse(rawResponse));
            }
            // TODO: Else set Error View by response code
        }

        /**
         * Method opens connection for url, do POST request and returns String with response.
         * Method assumes that username and password have already validated.
         */
        private String login(String username, String password) {
            String rawResponse = null;
            try {
                String query = "username=".concat(username);
                String encodedPassword = Security.encodeSha1(password);

                String url = getString(R.string.host);
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                doRequest(query, encodedPassword, connection);
//                logRequest(query, connection);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    rawResponse = loadResponse(connection);
                }
            } catch (IOException e) {
                // TODO: Log
                e.printStackTrace();
            }
            return rawResponse;
        }

        /**
         * Sets up request headers, writes query to output stream.
         */
        private void doRequest(String query, String encodedPassword, HttpURLConnection connection)
                throws IOException {
            // set headers for request
            connection.setDoOutput(true); // Triggers POST
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", encodedPassword);

            // write data
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(query.getBytes(StandardCharsets.UTF_8.name()));
            outputStream.close();
        }

        /**
         * Returns String with response built by StringBuilder from connection InputStream.
         */
        private String loadResponse(HttpURLConnection connection) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseOutput = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            return responseOutput.toString();
        }

        private void logRequest(String query, HttpURLConnection connection) throws IOException {
            // TODO: log it!
            System.out.printf("POST request on : %query\n", connection.getURL().toString());
            System.out.printf("Query : %query\n", query);
            System.out.printf("Response code=%d\n", connection.getResponseCode());
        }

        /**
         * Split and returns substring which contains base-64 encoded picture
         */
        private String clearResponse(String s) {
            return s.substring(10, (s.length() - 2));
        }
    }

    /**
     * Decodes String with base-64 encoded picture to bytes array and fires new Activity with it.
     */
    private void nextActivity(String picture) {
        Intent intent = new Intent(this, ImageActivity.class);
        byte[] imgBytes = Base64.decode(picture, Base64.DEFAULT);
        intent.putExtra(EXTRA_IMAGE, imgBytes);
        startActivity(intent);
    }
}
