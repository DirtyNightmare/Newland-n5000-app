package com.HardcodedDataGrid.datagrid;

import java.util.Collections;
import java.util.Comparator;
import com.HardcodedDataGrid.datatable.*;

class Sort {

    public static int SORT_NOSORT = -1;
    public static int SORT_ASC = 0;
    public static int SORT_DESC = 1;
    private DataGrid.MemberCollection mc;

    public Sort(DataGrid.MemberCollection mc)
    {
        this.mc = mc;
    }

    public void sortByColumn(int columnToSort,int sortOrder) {
        // sortResult
        Collections.sort(mc.DATA_SOURCE.getAllRows(), new ArrayColumnComparator(columnToSort, sortOrder));
    }

    private class ArrayColumnComparator implements Comparator<DataTable.DataRow> {

        private int columnToSortOn = 0;
        private int sortOrder = 0;

        ArrayColumnComparator(int columnToSortOn, int sortOrder) {
            this.columnToSortOn = columnToSortOn;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(DataTable.DataRow arg0, DataTable.DataRow arg1) {

            if(sortOrder == SORT_ASC)
            {
                // compare the desired column values & return result

                String str0=arg0.get(columnToSortOn);
                String str1=arg1.get(columnToSortOn);
                if (str0!=null && str1!=null)
                {
                    return arg0.get(columnToSortOn).compareTo(arg1.get(columnToSortOn));
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                int result=0;
                String str0=arg0.get(columnToSortOn);
                String str1=arg1.get(columnToSortOn);
                if (str0!=null && str1!=null)
                {
                    result = arg0.get(columnToSortOn).compareTo(arg1.get(columnToSortOn));
                }
                if(result > 0 ) return -1;
                if(result < 0 ) return 1;
                return 0;
            }
        }
    }
}