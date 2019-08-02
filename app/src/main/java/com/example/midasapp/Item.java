package com.example.midasapp;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;


public class Item
{
    Item(String cCode, String cDesc, double cCost)
    {
        code = cCode;
        desc = cDesc;
        cost = cCost;
    }
    public String code;
    public String desc;
    public double cost;

    TextView total;
    EditText qtyText;


    public TableRow getAsTableRow(Context context) //TODO: ADD BUTTON TO REMOVE ITEM FROM LIST
    {
        TableRow tr = new TableRow(context);

        TextView codeView = new TextView(context);
        codeView.setText(code);
        tr.addView(codeView);

        TextView descView = new TextView(context);
        descView.setText(desc);
        tr.addView(descView);

        TextView unitView = new TextView(context);
        unitView.setText(String.format("%.2f", cost));
        tr.addView(unitView);

        total = new TextView(context);
        total.setText(String.format("%.2f", cost));

        qtyText = new EditText(context);
        qtyText.setInputType(InputType.TYPE_CLASS_NUMBER);
        int qty = 1;
        qtyText.setText(Integer.toString(qty));
        qtyText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!qtyText.getText().toString().equals(""))
                    {
                        total.setText(String.format("%.2f", cost*Integer.parseInt(qtyText.getText().toString())));
                    }
                }
            }
        );
        tr.addView(qtyText);
        tr.addView(total);

        return tr;
    }
}
