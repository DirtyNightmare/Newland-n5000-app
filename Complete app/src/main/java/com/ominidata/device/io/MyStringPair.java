package com.ominidata.device.io;

/**
 * Created by kewin on 22-03-2017.
 */

import java.util.ArrayList;
import java.util.List;

public class MyStringPair {

    private String columnOne;
    private String columnTwo;

    public MyStringPair(String columnOne, String columnTwo) {
        super();
        this.columnOne = columnOne;
        this.columnTwo = columnTwo;
    }

    public String getColumnOne() {
        return columnOne;
    }
    public void setColumnOne(String columnOne) {
        this.columnOne = columnOne;
    }
    public String getColumnTwo() {
        return columnTwo;
    }
    public void setColumnTwo(String columnTwo) {
        this.columnTwo = columnTwo;
    }

    public static List<MyStringPair> makeData(int n) {
        List<MyStringPair> pair = new ArrayList<MyStringPair>();
        for (int i=0;i<n;i++) {
            pair.add(new MyStringPair(Integer.toString(i), "Col 2 value "+Integer.toString(i)));
        }
        return pair;
    }

}