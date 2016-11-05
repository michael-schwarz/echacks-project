package net.m_schwarz.journe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
    }
}
