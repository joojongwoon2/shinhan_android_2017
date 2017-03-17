package com.shinhan.activityexam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent = getIntent();
        String string = intent.getStringExtra("String");
        Toast.makeText(SubActivity.this, string, Toast.LENGTH_SHORT).show();


        EditText edittext = (EditText)findViewById(R.id.edittext);
        edittext.setText(string);
    }

    public void onClosedButtonClicked(View view){
        //Toast.makeText(SubActivity.this, "클릭", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(SubActivity.this, MainActivity.class);
        //startActivity(intent);
        Intent intent = new Intent();

        EditText edittext = (EditText)findViewById(R.id.edittext);
        intent.putExtra("Result", edittext.getText().toString());
        setResult(RESULT_OK, intent);

        finish();
    }
}
