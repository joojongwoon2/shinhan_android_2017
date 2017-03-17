package com.shinhan.linearlayoutexam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
    }

    public void onButtonClicked(View view){
        ImageView imageview1 = (ImageView)findViewById(R.id.imageview1);
        ImageView imageview2 = (ImageView)findViewById(R.id.imageview2);

        Button button = (Button)view;

        Toast.makeText(SubActivity.this, button.getId() + "", Toast.LENGTH_LONG).show();

        if(button.getText().toString().equals("클릭1")){
            imageview1.setBackgroundResource(R.drawable.img1);
            imageview2.setBackgroundResource(R.drawable.img2);
        }else{
            imageview1.setBackgroundResource(R.drawable.img3);
            imageview2.setBackgroundResource(R.drawable.img4);
        }
    }
}
