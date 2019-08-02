package com.example.midasapp;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

    TableLayout tableLayout;

    Spinner sItems;

    EditText searchText;

    TextView total;


    ArrayList<Item> allItems = new ArrayList<>();
    ArrayList<Item> orderItems = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        tableLayout = findViewById(R.id.tableLayout);
        tableLayout.setColumnShrinkable(1, true);
        tableLayout.setColumnStretchable(1, true);

        searchText = findViewById(R.id.searchText);

        total = findViewById(R.id.totalCost);

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
            sItems = (Spinner) findViewById(R.id.customerName);
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
                allItems.add(new Item(code, desc, cost));
            }
        }

        //Make a code list
        codeList = new String[allItems.size()];
        for(int a = 0; a < allItems.size(); a++)
        {
            codeList[a] = allItems.get(a).code;
        }
        {//Add list of codes to autocomplete options
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, codeList);
            AutoCompleteTextView searchText = (AutoCompleteTextView) findViewById(R.id.searchText);
            searchText.setAdapter(adapter);
        }//Add list of codes to autocomplete options

//        Intent i = getIntent();
//        String scannedCode = i.getStringExtra("code");
//        if(scannedCode != null && !scannedCode.equals(""))
//        {
//            for(int a = 0; a < allItems.size(); a++)
//            {
//                if(scannedCode.compareToIgnoreCase(allItems.get(a).code) == 0)
//                {
//                    addItemToOrder(allItems.get(a));
//                    Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//            }
//        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            String add = data.getStringExtra("code");
            for(int a = 0; a < allItems.size(); a++)
            {
                if(add.compareToIgnoreCase(allItems.get(a).code) == 0)
                {
                    addItemToOrder(allItems.get(a));
                    Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "Item not added", Toast.LENGTH_SHORT).show();
        }
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
//        i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(i,0);
    }

    public void addClick(View v)
    {
        String code = searchText.getText().toString();
        for(int a = 0; a < allItems.size(); a++)
        {
            if(code.compareToIgnoreCase(allItems.get(a).code) == 0)
            {
                addItemToOrder(allItems.get(a));
                searchText.setText("");
                Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, "Item not added", Toast.LENGTH_SHORT).show();
    }

    public void addItemToOrder(Item item)
    {
        tableLayout.addView(item.getAsTableRow(this), 0);
        orderItems.add(item);
        updateTotal();
    }

    public void updateTotal()
    {
        double totalCostD = 0;
        for(int a = 0; a < tableLayout.getChildCount(); a++)
        {
            TableRow tr = (TableRow) tableLayout.getChildAt(a);
            TextView caster = (TextView) tr.getChildAt(4); //4 is the 5th column; the total cost
            totalCostD += Double.parseDouble(caster.getText().toString());
        }
        total.setText("$" + String.format("%.2f", totalCostD));
    }

    public void saveExit(View v)
    {
        save(v);
        finish();
    }

    public void save(View v)
    {
        updateTotal();

        StringBuilder data = new StringBuilder();

        String customerList = FileManager.readFile(custFile, this);
        String[] customerListSplit = customerList.split("\n");

        String selectedCustomer = sItems.getSelectedItem().toString();

        File file = new File(path + "/orders/", selectedCustomer + ".txt"); //Do this before the trim so that the number is in the file name as well

        int bracketIndex = selectedCustomer.indexOf('(');
        selectedCustomer = selectedCustomer.substring(0, bracketIndex).trim();

        int custNumber = 0;
        for(int a = 0; a < customerListSplit.length; a += nth)
        {
            if(customerListSplit[a].compareToIgnoreCase(selectedCustomer) == 0)
            {
                custNumber = a;
            }
        }
        //Append customer info
        for(int a = 0; a < 6; a++) //6 customer fields: optical name, optical num, contact name, address, phone, email
        {
            data.append(customerListSplit[custNumber + a]);
            data.append('\n');
        }




        for(int a = 0; a < tableLayout.getChildCount(); a++)
        {
            TableRow tr = (TableRow) tableLayout.getChildAt(a);
            for(int b = 0; b < tr.getChildCount(); b++)
            {
                TextView text = (TextView) tr.getChildAt(b); //Casts the view into a textview to retrieve its text
                data.append(text.getText().toString());
                data.append('\t');
            }
            data.append('\n');
        }

        data.append("Total: ");
        data.append(total.getText().toString());

        Log.d(TAG, data.toString());
        if(FileManager.saveToFile(data.toString(), file, this))
        {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
