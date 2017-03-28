package com.HardcodedDataGrid.datagrid;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;
import com.HardcodedDataGrid.datagrid.DataGridStyle.HeaderCell;

class Header extends TextView implements OnGestureListener{

    private GestureDetector mGesture;
    private View.OnClickListener mOnclickListener = new View.OnClickListener() {@Override	public void onClick(View v) {mHeaderOnclick(v);	}};
    private DataGrid.MemberCollection  mc;

    public Header(Context context, DataGrid.MemberCollection mc) {
        super(context);
        mGesture = new GestureDetector(this);
        this.mc = mc;
        setBackgroundColor(HeaderCell.BackgroundColor);
        setTextSize(HeaderCell.FontSize);
        setTypeface(null, HeaderCell.typeface);
        setTextColor(Color.parseColor("#eaeaea"));
        setPadding(HeaderCell.LPadding, HeaderCell.TPadding, HeaderCell.RPadding, HeaderCell.BPadding);
        setSingleLine(true);
        setOnClickListener(mOnclickListener);
        setGravity(HeaderCell.Gravity);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        super.onTouchEvent(e);
        mGesture.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
              //  this.setTextColor(HeaderCell.OnClickForegroundColor);
                this.setBackgroundColor(HeaderCell.BackgroundColor);
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
             //   this.setTextColor(HeaderCell.ForegroundColor);
                this.setBackgroundColor(HeaderCell.BackgroundColor);
                break;
            }
            case MotionEvent.ACTION_UP:{
              //  this.setTextColor(HeaderCell.ForegroundColor);
                this.setBackgroundColor(HeaderCell.BackgroundColor);
                break;
            }
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    private void mHeaderOnclick(View v)
    {
        DataGrid.ColumnStyle ColumnStyle = (DataGrid.ColumnStyle)((TextView)v).getTag();

        if(ColumnStyle.getSortOder() == Sort.SORT_NOSORT || ColumnStyle.getSortOder() == Sort.SORT_DESC	)
        {
            ColumnStyle.setSortOrder(Sort.SORT_ASC);
            mc.SORT_ALGO.sortByColumn(mc.DATA_SOURCE.getColumnIndex(ColumnStyle.getFieldName()) ,Sort.SORT_ASC);
        }
        else
        {
            ColumnStyle.setSortOrder(Sort.SORT_DESC);
            mc.SORT_ALGO.sortByColumn(mc.DATA_SOURCE.getColumnIndex(ColumnStyle.getFieldName()), Sort.SORT_DESC);
        }

        mc.DATAGRID_ADAPTER.notifyDataSetChanged();
    }
}
