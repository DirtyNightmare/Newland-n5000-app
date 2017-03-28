package com.HardcodedDataGrid.datagrid;

import java.util.ArrayList;
import java.util.Collections;

import com.HardcodedDataGrid.datagrid.DataGridStyle.HeaderCell;
import com.HardcodedDataGrid.datagrid.DataGridStyle.HeaderContainer;
import com.HardcodedDataGrid.datagrid.DataGridStyle.DataContainer;
import com.HardcodedDataGrid.datagrid.DataGridStyle.HeaderSpliterCell;
import com.HardcodedDataGrid.datagrid.DataGridStyle.SpliterCell;
import com.HardcodedDataGrid.datatable.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class DataGrid extends LinearLayout{

    private Context mContext;
    private LinearLayout mListHeader;
    private HorizontalScroller mHsv;
    private MemberCollection mc;
    private TextView mTvNoData;
    private String strNoDataText;
    private Display display;

    public static class ColumnStyle{

        private String strColumnName;
        private String strFieldName;
        private int intWidth;
        private int intIndex;
        private int intSortOrder = -1;

        public ColumnStyle(String DisplayName, String FieldName, int width)
        {
            strColumnName = DisplayName;
            strFieldName = FieldName;
            intWidth = width;
        }

        public void setIndex(int index)
        {
            intIndex = index;
        }

        public int getIndex()
        {
            return intIndex;
        }

        public String getColumnName()
        {
            return strColumnName;
        }

        public String getFieldName()
        {
            return strFieldName;
        }

        public int getWitdh()
        {
            return intWidth;
        }

        public void setWidth(int width){
            intWidth = width;
        }

        public int getSortOder()
        {
            return intSortOrder;
        }

        public void setSortOrder(int SortOrder)
        {
            intSortOrder = SortOrder;
        }
    }

    class MemberCollection{
        public boolean IS_SPLITER_CLICKED = false;
        public MotionEvent DOWNSTART;
        public LinearLayout DATAGRID_VIEW;
        public DataGrid DATAGRID_WINDOW;
        public ArrayList<TextView> HEADER_VIEW;
        public ArrayList<ArrayList<TextView>> ITEM_VIEW;
        public ArrayList<ArrayList<Spliter2>> SPLITER_VIEW;
        public ArrayList<ColumnStyle> COLUMN_STYLE;
        public DataGridAdapter DATAGRID_ADAPTER;
        public ListView LIST_VIEW;
        public Sort SORT_ALGO;
        public DataTable DATA_SOURCE;
    }

    public DataGrid(Context context , AttributeSet attrs) throws Exception {

        super(context, attrs);
        setOrientation(VERTICAL);
        setBackgroundColor(DataGridStyle.DataGrid.BackgroundColor);

        display  = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mContext = context;

        mc = new MemberCollection();
        mc.DATAGRID_WINDOW = this;
        mc.HEADER_VIEW = new ArrayList<TextView>();
        mc.ITEM_VIEW = new ArrayList<ArrayList<TextView>>();
        mc.SPLITER_VIEW = new ArrayList<ArrayList<Spliter2>>();
        mc.COLUMN_STYLE = new ArrayList<ColumnStyle>();
        mc.SORT_ALGO = new Sort(mc);
    }

    private void initHeader()
    {
        Splitter Spliter;
        Header HeaderView;
        int intCellSpliter = 0;
        int headerSpilterWidth = 0;
        int dataSpliterWidth = 0;
        int WidthDifference = 0;

        for(int i = 0; i < mc.COLUMN_STYLE.size(); i++)
        {
            mc.COLUMN_STYLE.get(i).setIndex(i);

            HeaderView = new Header(getContext(), mc);
            HeaderView.setText((mc.COLUMN_STYLE.get(i)).getColumnName());
            HeaderView.setTag(mc.COLUMN_STYLE.get(i));

            headerSpilterWidth = HeaderSpliterCell.LPadding +HeaderSpliterCell.RPadding;
            dataSpliterWidth = SpliterCell.LPadding +SpliterCell.RPadding;
            WidthDifference = (i == 0 || i == mc.COLUMN_STYLE.size()) ? (headerSpilterWidth - dataSpliterWidth) /2 :headerSpilterWidth - dataSpliterWidth;
            HeaderView.setWidth((mc.COLUMN_STYLE.get(i).getWitdh() - WidthDifference));

            mc.HEADER_VIEW.add(HeaderView);

            mListHeader.addView(HeaderView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, HeaderCell.Height));

            if(intCellSpliter < mc.COLUMN_STYLE.size())
            {
                Spliter = new Splitter(getContext(), mc, i);
                Spliter.setIndex(i);
                mListHeader.addView(Spliter,new LinearLayout.LayoutParams(HeaderCell.Width, HeaderCell.Height));
                intCellSpliter++;
            }
        }
    }

    private void initListView()
    {
        mc.LIST_VIEW.setBackgroundColor(DataContainer.BackgroundColor);
        mc.DATAGRID_ADAPTER = new DataGridAdapter(getContext(), mc);
        mc.LIST_VIEW.setDividerHeight(2);
        mc.LIST_VIEW.setAdapter(mc.DATAGRID_ADAPTER);
    }

    public void create()
    {
        mListHeader = new LinearLayout(mContext);
        mListHeader.setOrientation(LinearLayout.HORIZONTAL);
        mListHeader.setBackgroundColor(HeaderContainer.BackgroundColor);
        mc.LIST_VIEW = new ListView(mContext);

        mc.DATAGRID_VIEW = new LinearLayout(mContext);
        mc.DATAGRID_VIEW.setOrientation(VERTICAL);
        mc.DATAGRID_VIEW.setPadding(2 , 0, 3 , 0);
        mTvNoData = new TextView(mContext);
        mTvNoData.setText(strNoDataText == null ? "No data available" : strNoDataText);
        mTvNoData.setGravity(Gravity.LEFT);
        mTvNoData.setTextColor(DataGridStyle.DataCell.ForegroundColor);

        //
        //	Trick to position the text to center of the screen
        //
        mTvNoData.setPadding((display.getWidth() /2 ) - (150/2), 10, 0, 10);
        mTvNoData.setVisibility(View.GONE);
        mHsv = new HorizontalScroller(mContext, mc);

        ArrayList<TextView> ary;
        ArrayList<Spliter2> ary2;
        for(int i = 0; i < mc.COLUMN_STYLE.size();i++)
        {
            ary = new ArrayList<TextView>();
            ary2 = new ArrayList<Spliter2>();
            mc.ITEM_VIEW.add(ary);
            mc.SPLITER_VIEW.add(ary2);
        }

        mc.DATAGRID_VIEW.addView(mListHeader);
        mc.DATAGRID_VIEW.addView(mc.LIST_VIEW);
        mc.DATAGRID_VIEW.addView(mTvNoData);
        mHsv.addView(mc.DATAGRID_VIEW);
        addView(mHsv);

        initHeader();
        initListView();
    }

    public void refresh()
    {
        if(mListHeader == null)create();
        mc.DATAGRID_ADAPTER.notifyDataSetChanged();

        mTvNoData.setVisibility(View.GONE);
        if(mc.DATA_SOURCE.getRowSize() == 0 ) mTvNoData.setVisibility(View.VISIBLE);
    }

    public void addColumnStyle(ColumnStyle columnStyle)
    {
        mc.COLUMN_STYLE.add(columnStyle);
    }

    public void addColumnStyle(String DisplayName,String strFieldName,int intDisplaySize)
    {
        mc.COLUMN_STYLE.add(new ColumnStyle(DisplayName,strFieldName,intDisplaySize));
    }

    public void addColumnStyles(ColumnStyle[] columnStyle)
    {
        Collections.addAll(mc.COLUMN_STYLE, columnStyle);
    }

    public void removeColumnStyle(int index)
    {
        mc.COLUMN_STYLE.remove(index);
    }

    public void removeColumnStyles()
    {
        mc.COLUMN_STYLE.clear();
    }

    public ColumnStyle[] getColumnStyles()
    {
        return mc.COLUMN_STYLE.toArray(new ColumnStyle[mc.COLUMN_STYLE.size()]);
    }

    public void setDataSource(DataTable data)
    {
        mc.DATA_SOURCE = data;
    }

    public void setLvOnClickListener(OnClickListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnClickListener(listener);
    }

    public void setLvOnItemClickListener(OnItemClickListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnItemClickListener(listener);
    }

    public void setLvOnItemLongClickListener(OnItemLongClickListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnItemLongClickListener(listener);
    }

    public void setLvOnItemSelectedListener(OnItemSelectedListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnItemSelectedListener(listener);
    }

    public void setLvOnCreateContextMenuListener(OnCreateContextMenuListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnCreateContextMenuListener(listener);
    }

    public void setLvOnFocusChangeListener(OnFocusChangeListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnFocusChangeListener(listener);
    }

    public void setLvOnLongClickListener(OnLongClickListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnLongClickListener(listener);
    }

    public void setLvOnScrollListener(OnScrollListener listener)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setOnScrollListener(listener);
    }

    public void setLvClickable(boolean isClickable)
    {
        if(mc.LIST_VIEW == null)create();
        mc.LIST_VIEW.setClickable(isClickable);
    }

    public void setNoDataText(String strText)
    {
        strNoDataText = strText;
    }
}
