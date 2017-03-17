package com.shinhan.activityexam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View view){
        EditText edittext = (EditText)findViewById(R.id.edittext);
        String string = edittext.getText().toString();
        //Toast.makeText(MainActivity.this, "클릭!!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        intent.putExtra("String", string);
        //startActivity(intent);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            if (resultCode == RESULT_OK) {//값을 넘기는 정상 종료일때만...  취소 및 finish는 CANCEL 이다.
                String string = data.getStringExtra("Result");
                EditText edittext = (EditText)findViewById(R.id.edittext);
                edittext.setText(string);
            }
        }
    }
}
