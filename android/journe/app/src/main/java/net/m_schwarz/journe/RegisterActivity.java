package net.m_schwarz.journe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void clickRegister(View view) {
        Preferences preferences = new Preferences(this);
        preferences.setUserId(42);

        Intent intent = new Intent(this,MainActivity.class);
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
        EditText confirmPass = (EditText) findViewById(R.id.password_repeat);
        String password = pass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(),"Passwords do not match. Please re-enter.",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this,LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
