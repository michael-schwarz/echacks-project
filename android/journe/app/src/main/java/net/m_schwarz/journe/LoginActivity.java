package net.m_schwarz.journe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.m_schwarz.journe.Comm.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void clickRegister(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    public void clickLogin(View view) {
        EditText et = (EditText) findViewById(R.id.username);
        String email = et.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText pass = (EditText) findViewById(R.id.password);
        String password = pass.getText().toString().trim();

        if (password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask<String,Void,User> userLoginTask = new AsyncTask<String,Void,User>() {
            @Override
            protected User doInBackground(String... params) {
                try {
                    return User.login(params[0], params[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(User user) {
                gotUserDetails(user);
            }
        };

        userLoginTask.execute(email,password);
    }

    private void gotUserDetails(User user) {
        if(user != null && user.email != null){
            Preferences preferences = new Preferences(this);
            preferences.setUserId(user.id);

            Intent intent = new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        } else {
            if(user != null && user.errorReason != null){
                Toast.makeText(this,user.errorReason, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
