package com.HardcodedDataGrid.datagrid;

import com.HardcodedDataGrid.datagrid.DataGridStyle.SpliterCell;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

class Spliter2 extends TextView{

    public Spliter2(Context context, DataGrid.MemberCollection mc, int intIndex) {
        super(context);
        setBackgroundColor(SpliterCell.OnClickBackgroundColor);
        setBackgroundColor(SpliterCell.BackgroundColor);

        if(mc.COLUMN_STYLE.get(intIndex).getWitdh() == 0)
            setPadding(0, SpliterCell.TPadding,0, SpliterCell.BPadding);
        else
            setPadding(SpliterCell.LPadding, SpliterCell.TPadding, SpliterCell.RPadding, SpliterCell.BPadding);

        setTextSize(SpliterCell.FontSize);
        setText("");
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return true;
    }

}
