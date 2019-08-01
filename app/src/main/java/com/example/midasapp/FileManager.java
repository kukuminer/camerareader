package com.example.midasapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    private static String TAG = "~~~CHECK HERE:";


    public static boolean saveToFile(String data, File file, Context context)
    {
        String write = readFile(file, context) + data + System.getProperty("line.separator");

        try
        {
            FileWriter f = new FileWriter(file);
            f.write(write);
            f.close();
            return true;
        } catch (IOException e)
        {
            Log.d(TAG + "STF1", e.getMessage());
        }
        return false;
    }


    public static String readFile(File file, Context context)
    {
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
                Toast.makeText(context, "Created new file named " + file.getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Stored in " + file.getPath(), Toast.LENGTH_LONG).show();
            } catch(IOException e)
            {
                Log.d(TAG + "RCF1", e.getMessage());
            }
        }
        else
        {
            Log.d(TAG + "RCF50", file.getPath());
        }
        StringBuilder customerList = new StringBuilder();
        try
        {
            FileReader r = new FileReader(file);
            for(int c = r.read(); c != -1; c = r.read())
            {
                customerList.append((char) c);
            }
            r.close();
            return customerList.toString();
        } catch (Exception e)
        {
            Log.d(TAG + "RCF2", e.getMessage());
        }
        Toast.makeText(context, "Error reading file " + file.getName(), Toast.LENGTH_LONG).show();
        return "Error reading customer info file";
    }
}
