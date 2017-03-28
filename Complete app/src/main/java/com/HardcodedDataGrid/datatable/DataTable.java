package com.HardcodedDataGrid.datatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class DataTable {

    private List<String> column = new ArrayList<String>();
    private List<DataRow> row = new ArrayList<DataRow>();
    private int indexCounter = 0;
    private Hashtable<Integer, Integer> IDnIndex = new Hashtable<Integer,Integer>();

    public class DataRow{

        private List<String> columnValues;
        private int id;

        public DataRow(int id)
        {
            this.id = id;
            columnValues = new ArrayList<String>();
            for(int i = 0; i < column.size() ; i++ ) columnValues.add(null);
        }

        public DataRow()
        {
            columnValues = new ArrayList<String>();
            for(int i = 0; i < column.size() ; i++ ) columnValues.add(null);
        }

        public int getID()
        {
            return id;
        }

        public String get(String columnName)
        {
            return columnValues.get(column.indexOf(columnName));
        }

        public String get(int columnIndex)
        {
            return columnValues.get(columnIndex);
        }

        public void set(String columnName, String value)
        {
            columnValues.set(column.indexOf(columnName), value);
        }

        public void set(int columnIndex, String value)
        {
            columnValues.set(columnIndex, value);
        }

        public int getColumnSize()
        {
            return column.size();
        }
    }

    public void addColumn(String columnName)
    {
        column.add(columnName);
    }

    public void addAllColumns(String[] columns)
    {
        Collections.addAll(column, columns);
    }

    public int getColumnSize()
    {
        return column.size();
    }

    public int getColumnIndex(String columnName)
    {
        return column.indexOf(columnName);
    }

    public String getColumnName(int intIndex)
    {
        return column.get(intIndex);
    }

    public int getRowSize()
    {
        return row.size();
    }

    public DataRow getRow(int rowIndex)
    {
        return row.get(rowIndex);
    }

    public void remove(int rowIndex)
    {
        row.remove(rowIndex);
        refreshIDnIndex();
    }

    public void remove(DataRow dataRow)
    {
        row.remove((int)IDnIndex.get(dataRow.getID()));
        refreshIDnIndex();
    }

    public void update(DataRow dataRow)
    {
        row.set(IDnIndex.get(dataRow.getID()), dataRow);
    }

    public void add(DataRow row)
    {
        IDnIndex.put(row.getID(), this.row.size());
        this.row.add(row);
    }

    public void addAllRows(DataRow[] row)
    {
        for(DataRow r : row)
        {
            IDnIndex.put(r.getID(), this.row.size());
            this.row.add(r);
        }
    }

    public DataRow newRow()
    {
        DataRow dr = new DataRow(indexCounter);
        indexCounter++;
        return dr;
    }

    public DataTable copy()
    {
        DataTable dt = new DataTable();
        dt.addAllColumns(column.toArray(new String[column.size()]));
        dt.addAllRows(row.toArray(new DataRow[row.size()]));
        return dt;
    }

    public List<DataRow> getAllRows()
    {
        return row;
    }

    public void clear()
    {
        row.clear();
    }

    public DataRow[] select(HashMap<String,String> condition)
    {
        ArrayList<DataRow> rs = new ArrayList<DataRow>();
        boolean isMatch = true;
        Iterator<String> i;

        for(DataRow r : row)
        {
            isMatch = true;
            i = condition.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                if(r.get(key) == null && condition.get(key)!= null) isMatch= false;
                if(r.get(key) != null && condition.get(key)== null) isMatch= false;
                if(r.get(key) != null &&
                        condition.get(key)!= null &&
                        !r.get(key).equals(condition.get(key))) isMatch= false;
            }

            if(isMatch) rs.add(r);
        }

        return rs.toArray(new DataRow[rs.size()]);
    }

    public DataRow select(int rowID) throws Exception
    {
        for(DataRow r : row)
        {
            if(r.getID() == rowID)return r;
        }

        throw new Exception("Id doesn't exist");
    }

    private void refreshIDnIndex()
    {
        IDnIndex.clear();
        for(int i = 0; i < row.size(); i++)
            IDnIndex.put(row.get(i).getID(), i);
    }
}
