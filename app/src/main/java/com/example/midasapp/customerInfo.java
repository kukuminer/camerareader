package com.example.midasapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static com.example.midasapp.FileManager.saveToFile;

public class customerInfo extends AppCompatActivity
{
    public static final String CUSTOMER_FILE_NAME = "customerData.txt";
    final static String TAG = "~~~~~~~CATCH HERE:";
    File file;
    File path;

    EditText name;
    EditText ON;
    EditText contact;
    EditText address;
    EditText phone;
    EditText email;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);

        path = this.getExternalMediaDirs()[0];
        file = new File(path, CUSTOMER_FILE_NAME);
        if(!file.exists())
        {
            try
            {
                if(path.mkdirs())
                {
                    Toast.makeText(this, "made path: " + file.getPath(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e)
            {
                Log.d(TAG + "OCC5", e.getMessage());
            }
        }
        Toast.makeText(this, "Abs path: " + file.getAbsolutePath() + "\npath: " + file.getPath(), Toast.LENGTH_LONG).show();

        //The 4 required fields
        name = findViewById(R.id.editName);
        contact = findViewById(R.id.editContact);
        ON = findViewById(R.id.editON);
        address = findViewById(R.id.editAddress);
        //2 extra fields
        phone = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
    }

    public void saveExit(View v)
    {
        String data = "";
        if(name.getText().toString().equals("") || contact.getText().toString().equals("") || address.getText().toString().equals("") || ON.getText().toString().equals(""))
        {
            Toast.makeText(this, "Missing a field!", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            data += name.getText().toString() + "\n";
            data += ON.getText().toString() + "\n";
            data += contact.getText().toString() + "\n";
            data += address.getText().toString() + "\n";
            if(phone.getText().toString().equals(""))
            {
                data += "No phone\n";
            } else
            {
                data += phone.getText().toString() + "\n";
            }
            if(email.getText().toString().equals(""))
            {
                data += "No email\n";
            } else
            {
                data += email.getText().toString() + "\n";
            }
        }

        if(saveToFile(data, file, this))
        {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(customerInfo.this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this, "Not saved!", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View v)
    {
        Toast.makeText(this, "Data not saved!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(customerInfo.this, MainActivity.class);
        startActivity(i);
    }


//    public boolean saveToFile(String data)
//    {
//        String write = readCustomerFile() + data + System.getProperty("line.separator");
//
//
///*
//        File path = this.getFilesDir();
//        File file = new File(path, CUSTOMER_FILE_NAME);
//*/
//
//        try
//        {
//            FileWriter f = new FileWriter(file);
//            f.write(write);
//            f.close();
//            return true;
//        } catch (IOException e)
//        {
//            Log.d(TAG + "STF1", e.getMessage());
//        }
//        return false;
//    }

//    public String readCustomerFile()
//    {
///*
//        File path = this.getFilesDir();
//        File file = new File(path, CUSTOMER_FILE_NAME);
//*/
//        if(!file.exists())
//        {
//            try
//            {
//                file.createNewFile();
//                Toast.makeText(this, "Created new file named " + CUSTOMER_FILE_NAME, Toast.LENGTH_SHORT).show();
//            } catch(IOException e)
//            {
//                Log.d(TAG + "RCF1", e.getMessage());
//            }
//        }
//        else
//        {
//            Log.d(TAG + "RCF50", file.getPath());
//        }
//        StringBuilder customerList = new StringBuilder();
//        try
//        {
//            FileReader r = new FileReader(file);
//            for(int c = r.read(); c != -1; c = r.read())
//            {
//                customerList.append((char) c);
//            }
//            r.close();
//            return customerList.toString();
//        } catch (Exception e)
//        {
//            Log.d(TAG + "RCF2", e.getMessage());
//        }
//        return "Error reading customer info file";
//    }



}
