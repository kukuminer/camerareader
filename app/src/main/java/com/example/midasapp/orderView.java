package com.example.midasapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.midasapp.customerInfo.CUSTOMER_FILE_NAME;

public class orderView extends AppCompatActivity {

    File path;
    File file;
    final int nth = 7; //Customer names appear on every 7th line



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        path = this.getExternalMediaDirs()[0];
        file = new File(path, CUSTOMER_FILE_NAME);


        List<String> spinnerArray = new ArrayList<String>();
        String customerList = FileManager.readFile(file, this);
        String[] split = customerList.split("\n");
        for(int a = 0; a < split.length; a += nth)
        {
            String add = split[a] + " (" + split[a+1] + ")";
            spinnerArray.add(0, add);
        }


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
