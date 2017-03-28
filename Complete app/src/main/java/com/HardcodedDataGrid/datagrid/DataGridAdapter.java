package com.HardcodedDataGrid.datagrid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.HardcodedDataGrid.datatable.DataTable;;


class DataGridAdapter extends BaseAdapter {

    private Context mContext;
    private DataGrid.MemberCollection mc;

    public DataGridAdapter(Context context, DataGrid.MemberCollection mc) {
        mContext = context;
        this.mc = mc;
    }


    public int getCount() {
        return mc.DATA_SOURCE.getRowSize();
    }

    public Object getItem(int position) {
        return mc.DATA_SOURCE.getRow(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a SpeechView to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Item ri;

        DataTable.DataRow data = mc.DATA_SOURCE.getRow(position);

        if(convertView == null)
        {
            ri = new Item(mContext, mc, data);
        }
        else
        {
            ri = (Item)convertView;
            ri.populate(data);
        }

        return ri;
    }
}