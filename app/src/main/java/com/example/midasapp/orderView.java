package com.example.midasapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.List;

public class orderView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);


        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("one");
        spinnerArray.add("2");
        spinnerArray.add("THREE");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.customerName);
        sItems.setAdapter(adapter);




    }

    public void gotoScan(View v)
    {
        Intent i = new Intent(orderView.this, scanView.class);
        startActivity(i);
    }

    public void gotoScanView(View v)
    {
        Intent i = new Intent(orderView.this, scanView.class);
        startActivity(i);
    }
}
