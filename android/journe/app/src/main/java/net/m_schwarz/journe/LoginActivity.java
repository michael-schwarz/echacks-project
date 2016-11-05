package net.m_schwarz.journe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
    }
}
