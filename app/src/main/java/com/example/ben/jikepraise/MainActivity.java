package com.example.ben.jikepraise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ben.jikepraise.View.JiKePraiseView;

public class MainActivity extends AppCompatActivity {

    private JiKePraiseView mJiKePraiseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
