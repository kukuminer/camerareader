package com.example.midasapp;

import android.util.Pair;

import java.util.Set;

public class Order
{
    Set<Pair<Item, Integer>> order;

    public void addToOrder(Item item, int qty)
    {
        order.add(new Pair(item, qty));
    }
}
