package com.example.midasapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class loadOrder extends AppCompatActivity {

    File path;
    File orderPath;
    String[] files;

    Spinner fileSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_order);


        path = this.getExternalMediaDirs()[0];
        orderPath = new File(path, "/orders/");
        files = orderPath.list();

        if(files.length != 0)
        {
            List<String> spinnerArray = new ArrayList<String>();
            for (int a = 0; a < files.length; a++)
            {
                spinnerArray.add(0, files[a]);
            }

            //Update spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fileSelect = (Spinner) findViewById(R.id.fileList);
            fileSelect.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "No orders to load!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void load(View v)
    {
        String fileName = fileSelect.getSelectedItem().toString();
        File loadFile = new File(orderPath, fileName);
        Intent i = new Intent(loadOrder.this, orderView.class);
        i.putExtra("filename", loadFile);
        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
        finish();
    }
}
