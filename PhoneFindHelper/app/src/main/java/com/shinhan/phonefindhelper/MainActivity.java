package com.shinhan.phonefindhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void infoClicked(View view){
        Intent updateIndent = new Intent(MainActivity.this, UpdateInfoActivity.class);
        startActivity(updateIndent);
    }

    public void registlostClicked(View view){
        Intent registIndent = new Intent(MainActivity.this, RegistLostActivity.class);
        startActivity(registIndent);
    }

    public void confirmationlocationClicked(View view){
        Intent lockIndent = new Intent(MainActivity.this, ConfirmationLocationActivity.class);
        startActivity(lockIndent);
    }
}
