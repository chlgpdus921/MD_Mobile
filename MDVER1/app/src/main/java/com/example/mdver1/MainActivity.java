package com.example.mdver1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


//import androidx.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBar();

        Button carissue_page=(Button)findViewById(R.id.carissue_btn);
        carissue_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CarIssueActivity.class);
                startActivity(intent);
              //  finish();
            }
        });

        Button submit_page=(Button)findViewById(R.id.submit_btn);
        submit_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SubmitActivity.class);
                startActivity(intent);
                //  finish();
            }
        });

        Button purpose_page=(Button)findViewById(R.id.purpose_btn);
        purpose_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, purpose.class);
                startActivity(intent);
                //  finish();
            }
        });

        Button how_to_use_page=(Button)findViewById(R.id.use_btn);
        how_to_use_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, howtouse.class);
                startActivity(intent);
                //  finish();
            }
        });
    }

    private void setActionBar(){
        CustomActionBar ca=new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar();
    }

}