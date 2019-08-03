package com.example.midasapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    TableLayout tableLayout;

    Spinner sItems;

    EditText searchText;

    TextView total;


    ArrayList<Item> allItems = new ArrayList<>(); //The item database arrayList
//    ArrayList<Item> orderItems = new ArrayList<>();





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
            if(!customerList.equals(""))
            {
                String[] split = customerList.split("\n");
                for (int a = 0; a < split.length; a += nth) {
                    String add = split[a] + " (" + split[a + 2] + ")"; //Contact name is found 2 lines below store name
                    spinnerArray.add(0, add);
                }
                //Update spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sItems = (Spinner) findViewById(R.id.customerName);
                sItems.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(this, "No valid customers!", Toast.LENGTH_LONG).show();
                finish();
            }
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

        Intent i = getIntent();
        File load;
        if(i != null && i.hasExtra("filename"))
        {
            load = (File) i.getSerializableExtra("filename");
            if(load != null)
            {
                loadFromFile(load);
            }
        }
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
                    addItemToOrder(allItems.get(a), false);
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
                if(addItemToOrder(allItems.get(a), false))
                {
                    Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
                }
                searchText.setText("");
                return;
            }
        }
        Toast.makeText(this, "Item not added", Toast.LENGTH_SHORT).show();
    }

    public void loadFromFile(File file) //TODO: FIX CUSTOMER DUPLICATES
    {
        String custName = file.getName().substring(0, file.getName().length()-4);
        Log.d(TAG + "LFF4", custName);
        for(int a = 0; a < sItems.getChildCount(); a++)
        {
            if(sItems.getChildAt(a).toString().compareToIgnoreCase(custName) == 0)
            {
                sItems.setSelection(a);
                break;
            }
        }

        String fileContents = FileManager.readFile(file, this);
        String[] splitContents = fileContents.split("\n");
        for(int a = 6; a < splitContents.length; a++) //a starts at 6 to skip over the customer info in the file contained in the first 6 lines of the file
        {
            String[] splitItem = splitContents[a].split("\t"); //splitItem[0] is the code, splitItem[4] is the qty
            for(int b = 0; b < allItems.size(); b++)
            {
                if(splitItem[0].trim().compareToIgnoreCase(allItems.get(b).code) == 0)
                {
                    addMultipleItems(allItems.get(b), Integer.parseInt(splitItem[6]));
                    break;
                }
            }
        }
        updateTotal();
    }

    public void addMultipleItems(Item item, int qty)
    {
        for(int a = 0;a < qty; a++)
        {
            addItemToOrder(item, true);
        }
    }

    public boolean addItemToOrder(Item item, boolean loading)
    {
        for(int a = 0; a < tableLayout.getChildCount(); a++)
        {
            TableRow tr = (TableRow) tableLayout.getChildAt(a);
            TextView caster = (TextView) tr.getChildAt(0); //0 is the 1st column; the item code
            String itemCode = caster.getText().toString();
            if(item.code.compareToIgnoreCase(itemCode) == 0)
            {
                caster = (TextView) tr.getChildAt(3);
                int tempQty = Integer.parseInt(caster.getText().toString());
                tempQty++;
                ((EditText) tr.getChildAt(3)).setText(Integer.toString(tempQty));
                if(!loading)
                {
                    Toast.makeText(this, "Item already in list, increased qty by 1", Toast.LENGTH_SHORT).show();
                }
                updateTotal();
                return false;
            }
        }

        tableLayout.addView(item.getAsTableRow(this), 0);
//        orderItems.add(item);
        updateTotal();
        return true;
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
        File orderPath = new File(path, "/orders/");
        orderPath.mkdir();
        File file = new File(orderPath, selectedCustomer + ".txt"); //Do this before the trim so that the number is in the file name as well

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
        if(FileManager.saveToFile(data.toString(), file, this, true))
        {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
