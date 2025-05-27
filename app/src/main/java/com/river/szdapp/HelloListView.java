package com.river.szdapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class HelloListView extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hello_list_view);
		
		String[] menuNames = {"first", "second" };
        int[] menuImg = {
                R.drawable.c1,
                R.drawable.c2
               
        };
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < menuNames.length; i++ ) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("img", menuImg[i]);
            listItem.put("name", menuNames[i]);
            listItems.add(listItem);
        }
        SimpleAdapter sampleAdapter = new SimpleAdapter(this
                , listItems
                , R.layout.menu_items
                , new String[] {"img", "name"}
                , new int[] { R.id.menu_img, R.id.menu_name}
            );

        ListView menuListView = (ListView)findViewById(R.id.menu_list);
        menuListView.setAdapter(sampleAdapter);
	}
		
}
