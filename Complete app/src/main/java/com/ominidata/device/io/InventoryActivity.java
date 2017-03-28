package com.ominidata.device.io;

/**
 * Created by kewin on 22-03-2017.
 */

import java.util.List;

import com.ominidata.device.R;
import com.ominidata.device.R.id;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class InventoryActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        TextView description1 = (TextView) findViewById(id.header_line1);
        TextView description2 = (TextView) findViewById(id.header_line2);


        TextView columnHeader1 = (TextView) findViewById(R.id.column_header1);
        TextView columnHeader2 = (TextView) findViewById(R.id.column_header2);

        columnHeader1.setText(R.string.carnumber);
        columnHeader2.setText(R.string.Shelfnumber);

        ListView view = (ListView) findViewById(R.id.listview);
        final List<MyStringPair> myStringPairList = MyStringPair.makeData(10);
        MyStringPairAdapter adapter = new MyStringPairAdapter(this, myStringPairList);

        view.setAdapter(adapter);
    }
}
