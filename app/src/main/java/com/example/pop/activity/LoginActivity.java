package com.example.pop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pop.DBConstants;
import com.example.pop.R;
import com.example.pop.helper.CheckNetworkStatus;
import com.example.pop.helper.HttpJsonParser;
import com.example.pop.model.User;
import com.example.pop.sqlitedb.SQLiteDatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private SQLiteDatabaseAdapter db;
    private EditText email;
    private EditText password;
    private TextView registerLink;
    private Button loginBtn;

    private ProgressDialog pDialog;
    private int success;
    private String message;
    private User user = new User();

    private Context context;
    private Session session;

    private static final String ALGORITHM = "AES";
    private static final String KEY = "F0C101355CD00EF098BD78C3D85E141C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(getApplicationContext());
        checkLogin(session.getLogin());

        db = new SQLiteDatabaseAdapter(this);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.regLink);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    validateMatch();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateMatch() {
        if(!DBConstants.STRING_EMPTY.equals(email.getText().toString()) || !DBConstants.STRING_EMPTY.equals(password.getText().toString())){
            new validateMatchAsyncTask().execute();
        }
        else {
            Toast.makeText(LoginActivity.this,"Some fields left empty", Toast.LENGTH_LONG).show();
        }
    }

    private void checkLogin(String login) {
        if(login.equals("Login")) {
            Intent i = new Intent(LoginActivity.this, FragmentHolder.class);
            startActivity(i);
        }
    }

    private class validateMatchAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Checking Database. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String encryptedPassword = encrypt(password.getText().toString());

                HttpJsonParser httpJsonParser = new HttpJsonParser();
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put(DBConstants.EMAIL, email.getText().toString());
                httpParams.put(DBConstants.PASSWORD, encryptedPassword);

                JSONObject jsonObject = httpJsonParser.makeHttpRequest(DBConstants.BASE_URL+"login.php", "POST", httpParams);
                try {
                    success = jsonObject.getInt("success");
                    if(success == 0){
                        message = jsonObject.getString("message");
                    }
                    else{
                        JSONArray userObjects = jsonObject.getJSONArray("data");
                        JSONObject userObject = userObjects.getJSONObject(0);
                        user.setId(userObject.getInt("user_id"));
                        user.setFirstName(userObject.getString("first_name"));
                        user.setLastName(userObject.getString("last_name"));
                        user.setEmail(userObject.getString("email"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (success == 1) {
                //Display success messageSystem.out.println("SUCCESS");
                Toast.makeText(LoginActivity.this,"Login", Toast.LENGTH_LONG).show();

                session.setLogin("Login");
                session.setUserId(user.getId());
                session.setFirstName(user.getFirstName());
                session.setLastName(user.getLastName());
                session.setEmail(user.getEmail());

                Intent i = new Intent(LoginActivity.this, FragmentHolder.class);
                startActivity(i);
                //Finish ths activity and go back to listing activity
                finish();

            } else {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;
    }

    private static Key generateKey() {
        Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        return key;
    }
}
