package com.example.midasapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.midasapp.customerInfo.CUSTOMER_FILE_NAME;

public class orderView extends AppCompatActivity {

    private static String TAG = "~~~CHECK HERE:";

    public static final String ITEM_FILE_NAME = "items.txt";
    File path;
    File custFile;
    File itemFile;
    final int nth = 7; //Customer names appear on every 7th line

    String[] codeList;

    ScrollView scrollView;
    TableLayout tableLayout;

    EditText searchText;


    ArrayList<Item> items = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        tableLayout = findViewById(R.id.tableLayout);

        searchText = findViewById(R.id.searchText);


        //Create files for reading
        path = this.getExternalMediaDirs()[0];
        custFile = new File(path, CUSTOMER_FILE_NAME);
        itemFile = new File(path, ITEM_FILE_NAME);

        {
            ///Fill customer spinner array
            //Read files
            List<String> spinnerArray = new ArrayList<String>();
            String customerList = FileManager.readFile(custFile, this);
            String[] split = customerList.split("\n");
            for (int a = 0; a < split.length; a += nth) {
                String add = split[a] + " (" + split[a + 1] + ")";
                spinnerArray.add(0, add);
            }
            //Update spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.customerName);
            sItems.setAdapter(adapter);
        }////// Updating the spinner

        ///Get item list
        String itemListRaw = FileManager.readFile(itemFile, this);
        if(itemListRaw == "")
        {
            Toast.makeText(this, "File is empty! Forgot to import?", Toast.LENGTH_LONG).show();
            return;
        }
        //Making the item arraylist
        String[] itemListSplit = itemListRaw.split("\n");
        for(int a = 0; a < itemListSplit.length; a++)
        {
            int firstSpace = itemListSplit[a].indexOf('\t');
            int dollarSign = itemListSplit[a].indexOf('$');
            if(firstSpace == -1 || dollarSign == -1)
            {
                Log.d(TAG + "ILS30", "Missing \\t or $ on line " + a);
            }
            else
            {
                String code = itemListSplit[a].substring(0, firstSpace);
                String desc = itemListSplit[a].substring(firstSpace, dollarSign);
                String costString = itemListSplit[a].substring(dollarSign+1);
                double cost = Double.parseDouble(costString);
                items.add(new Item(code, desc, cost));
            }
        }

        //Make a code list
        codeList = new String[items.size()];
        for(int a = 0; a < items.size(); a++)
        {
            codeList[a] = items.get(a).code;
        }
        {//Add list of codes to autocomplete options
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, codeList);
            AutoCompleteTextView searchText = (AutoCompleteTextView) findViewById(R.id.searchText);
            searchText.setAdapter(adapter);
        }//Add list of codes to autocomplete options

        Intent i = getIntent();
        i.getStringExtra("code");


    }

    public void gotoScan(View v)
    {
        Intent i = new Intent(orderView.this, scanView.class);
        startActivity(i);
    }

    public void gotoScanView(View v)
    {
        Intent i = new Intent(orderView.this, scanView.class);
        i.putExtra("codes", codeList);
        startActivity(i);
    }

    public void addClick(View v)
    {
        String code = searchText.getText().toString();
        for(int a = 0; a < items.size(); a++)
        {
            if(code.compareToIgnoreCase(items.get(a).code) == 0)
            {
                addItemToOrder(items.get(a));
                searchText.setText("");
                Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, "Item not added", Toast.LENGTH_SHORT).show();
    }

    public void addItemToOrder(Item item)
    {
        tableLayout.addView(item.getAsTableRow(this));

    }
}
