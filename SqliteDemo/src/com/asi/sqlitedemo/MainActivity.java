package com.asi.sqlitedemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.asi.notedatabase.DbManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{
	TextView view = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		view = (TextView)findViewById(R.id.log);

        testDb();
	}

    String TAG = "TestDb";
    void testDb() {
        DbManager.createInstance(getApplication());


        List data = ctreateDataList();
        DbManager.getInstance().insert(data);

        List listRes = DbManager.getInstance().queryAll(TestInfo.class);
        if (listRes != null) {
            Log.d(TAG, "" + listRes.size());
        }
    }

    List ctreateDataList() {
        List list = new ArrayList();
        for(int i = 1; i <= 5; i++) {
            list.add(new TestInfo(true, true, 'a', 'a', (short) i, (short) i, i, i, i, (long) i, i, (double) i, i, (float)i, "asi"));
        }
        return list;
    }
}
