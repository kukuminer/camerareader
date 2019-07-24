package com.example.midasapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

        public void gotoCustomerInfo(View v)
        {
            Intent i = new Intent(MainActivity.this, customerInfo.class);
            startActivity(i);
        }

        public void gotoCreateOrder(View v)
        {
            Intent i = new Intent(MainActivity.this, orderView.class);
            startActivity(i);
        }

        public void gotoViewOrders(View v)
        {

        }

        public void gotoData(View v)
        {

        }



}
