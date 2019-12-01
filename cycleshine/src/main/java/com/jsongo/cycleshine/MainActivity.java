package com.jsongo.cycleshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jsongo.cycleshine.view.CycleShine;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CycleShine cycleShine = findViewById(R.id.cs);
        cycleShine.startAnim();
    }
}
