package com.shinhan.helloworld;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.button);
        TextView textView = (TextView)findViewById(R.id.textview);
        textView.setText("안녕, 영환 & 민준");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "버튼 클릭!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onButton2Clicked(View view){
        Toast.makeText(MainActivity.this, "두번째 버튼 클릭", Toast.LENGTH_SHORT).show();
    }
    public void onButton3Clicked(View view){
        Toast.makeText(MainActivity.this, "세번째 버튼 클릭", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.shinhan.com"));
        startActivity(intent);
    }
    public void onButton4Clicked(View view){
        Toast.makeText(MainActivity.this, "네번째 버튼 클릭", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-1234-5678"));
        startActivity(intent);
    }
}
