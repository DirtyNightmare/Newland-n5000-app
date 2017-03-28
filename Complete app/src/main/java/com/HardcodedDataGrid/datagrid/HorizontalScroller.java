package com.HardcodedDataGrid.datagrid;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

class HorizontalScroller extends HorizontalScrollView{

    private DataGrid.MemberCollection mc;

    public HorizontalScroller(Context context, DataGrid.MemberCollection mc) {
        super(context);
        this.setSmoothScrollingEnabled(true);
        this.setHorizontalFadingEdgeEnabled(false);
        this.mc = mc;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try
        {
            switch(event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                {
                    mc.DOWNSTART = MotionEvent.obtain(event);
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    if(!mc.IS_SPLITER_CLICKED)
                    {
                        float deltaX = event.getX() - mc.DOWNSTART.getX();
                        float deltaY = event.getY() - mc.DOWNSTART.getY();
                        if(Math.abs(deltaX) > Math.abs(deltaY)) return true;
                    }
                }
            }
        }
        catch(Exception ex)
        {

        }

        return false;
    }

    @Override
    public boolean  onTouchEvent(MotionEvent e){

        if(e.getAction() != 2) return false;

        int WindowWidth = mc.DATAGRID_WINDOW.getWidth();
        int ScrollWidth = -( (int) ( (e.getX() - mc.DOWNSTART.getX()) /4    ));
        int balance;

        if(ScrollWidth != 0 && mc.DATAGRID_VIEW.getWidth() > WindowWidth)
        {
            //Scrolls to the right
            if(ScrollWidth > 0 )
            {
                //Scroll to the end of right edge
                if(ScrollWidth + WindowWidth > (mc.DATAGRID_VIEW.getWidth()- mc.DATAGRID_VIEW.getScrollX()))
                {
                    balance = ScrollWidth + WindowWidth - (mc.DATAGRID_VIEW.getWidth()- mc.DATAGRID_VIEW.getScrollX());
                    mc.DATAGRID_VIEW.scrollBy(ScrollWidth-balance, 0);

                }
                else
                    mc.DATAGRID_VIEW.scrollBy(ScrollWidth, 0);
            }
            //Scroll to the left
            else
            {
                if(mc.DATAGRID_VIEW.getScrollX() != 0)
                {
                    //Scroll to the end of left edge
                    if(Math.abs(ScrollWidth) > mc.DATAGRID_VIEW.getScrollX())
                    {
                        mc.DATAGRID_VIEW.scrollBy(-mc.DATAGRID_VIEW.getScrollX(), 0);
                    }
                    else
                        mc.DATAGRID_VIEW.scrollBy(ScrollWidth, 0);
                }
            }
        }

        return true;
    }


}
