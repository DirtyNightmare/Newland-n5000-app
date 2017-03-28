package com.HardcodedDataGrid.datagrid;

import com.HardcodedDataGrid.datagrid.DataGridStyle.HeaderSpliterCell;
import com.HardcodedDataGrid.datagrid.DataGridStyle.SpliterCell;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ominidata.device.R;
class Splitter extends LinearLayout {

    private final static int START_DRAGGING = 0;
    private final static int STOP_DRAGGING = 1;
    private float startX;
    private int status;
    private int index;
    private int newWidth;
    private DataGrid.MemberCollection mc;

    public Splitter(Context context, DataGrid.MemberCollection mc, int intIndex) {
        super(context);
        this.mc = mc;
        this.setOrientation(VERTICAL);
        setIndex(intIndex);

        boolean isCollapsed = false;
        if( mc.COLUMN_STYLE.get(index).getWitdh() == 0) isCollapsed = true;

        //
        // Top LinearLayout
        //
        LinearLayout llTop = new LinearLayout(context);
        llTop.setOrientation(HORIZONTAL);
        TextView tvTop1 = new TextView(context);
        TextView tvTop2 = new TextView(context);
        TextView tvTop3 = new TextView(context);

        tvTop1.setText("");
        tvTop1.setHeight(12);
        tvTop1.setBackgroundColor(SpliterCell.BackgroundColor);

        if(!isCollapsed)
            tvTop1.setPadding(SpliterCell.LPadding , 0, SpliterCell.RPadding, 0);
        else
            tvTop1.setPadding(0, 0, 0, 0);

        tvTop2.setText("");
        tvTop2.setHeight(12);
        if(!isCollapsed)
            tvTop2.setPadding((HeaderSpliterCell.LPadding - SpliterCell.LPadding), 0, 0, 0);
        else
            tvTop2.setPadding(0, 0, 0, 0);

        tvTop3.setText("");
        tvTop3.setHeight(12);

        if(!isCollapsed || index == 0) // in case of the first column in hidden column, panel on the right is still needed to fill up the space
            tvTop3.setPadding(0, 0, (HeaderSpliterCell.RPadding - SpliterCell.RPadding) , 0);
        else
            tvTop3.setPadding(0,0,0,0);

        llTop.addView(tvTop2);
        llTop.addView(tvTop1);
        llTop.addView(tvTop3);

        //
        // Bottom LinearLayout
        //
        LinearLayout llBottom = new LinearLayout(context);
        llBottom.setOrientation(HORIZONTAL);
        TextView tvBottom1 = new TextView(context);
        TextView tvBottom2 = new TextView(context);
        TextView tvBottom3 = new TextView(context);

        tvBottom1.setText("");
        tvBottom1.setHeight(36);
        tvBottom1.setBackgroundColor(SpliterCell.BackgroundColor);

        if(!isCollapsed)
            tvBottom1.setPadding(SpliterCell.LPadding, 0, SpliterCell.RPadding, 0);
        else
            tvBottom1.setPadding(0, 0, 0, 0);

        tvBottom2.setText("");
        tvBottom2.setHeight(36);

        if(!isCollapsed)
            tvBottom2.setPadding((HeaderSpliterCell.LPadding - SpliterCell.LPadding), 0, 0, 0);
        else
            tvBottom2.setPadding(0, 0, 0, 0);

        tvBottom3.setText("");
        tvBottom3.setHeight(36);

        if(!isCollapsed || index == 0) // in case of the first column in hidden column, panel on the right is still needed to fill up the space
            tvBottom3.setPadding(0, 0, (HeaderSpliterCell.RPadding - SpliterCell.RPadding), 0);
        else
            tvBottom3.setPadding(0, 0, 0, 0);

        llBottom.addView(tvBottom2);
        llBottom.addView(tvBottom1);
        llBottom.addView(tvBottom3);

        //
        // Middle LinearLayout
        //
        ImageView ivClick = new ImageView(context);
        ivClick.setImageDrawable(getResources().getDrawable(R.drawable.ic_drag_normal));
        if(isCollapsed) ivClick.setVisibility(GONE);

        this.addView(llTop);
        this.addView(ivClick);
        this.addView(llBottom);
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            status = START_DRAGGING;
            startX = e.getX();
            mc.IS_SPLITER_CLICKED = true;
            ((ImageView)(this.getChildAt(1))).setImageDrawable(getResources().getDrawable(R.drawable.ic_drag_pressed));
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            status = STOP_DRAGGING;
            mc.IS_SPLITER_CLICKED = false;
            ((ImageView)(this.getChildAt(1))).setImageDrawable(getResources().getDrawable(R.drawable.ic_drag_normal));

        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (status == START_DRAGGING) {

                newWidth = mc.HEADER_VIEW.get(index).getWidth() + (int) (e.getX() - startX);
                mc.HEADER_VIEW.get(index).setWidth(newWidth);

                int headerSpilterWidth = (HeaderSpliterCell.LPadding+HeaderSpliterCell.RPadding);
                int dataSpliterWidth = (SpliterCell.LPadding +SpliterCell.RPadding);
                int WidthDifference = (index == 0 || index == mc.COLUMN_STYLE.size()) ? (headerSpilterWidth - dataSpliterWidth) /2 :headerSpilterWidth - dataSpliterWidth;

                if(newWidth+WidthDifference > headerSpilterWidth)
                    mc.COLUMN_STYLE.get(index).setWidth((newWidth+WidthDifference) );
                else
                    mc.COLUMN_STYLE.get(index).setWidth((headerSpilterWidth-dataSpliterWidth));

                for(TextView tv: mc.ITEM_VIEW.get(index))
                {
                    if(newWidth+WidthDifference > headerSpilterWidth)
                        tv.setWidth(newWidth+WidthDifference);
                    else
                        // Due to padding is used early development, it is hard & confusing to understand
                        // the actual width of headerSpilterWidth. It should be replaced by setWitdh function in coming time.
                        // For the sake of time saving, a trick below is used to achieve the correct positioning.
                        tv.setWidth(headerSpilterWidth-dataSpliterWidth);

                }
            }
        }

        return true;
    }
}
