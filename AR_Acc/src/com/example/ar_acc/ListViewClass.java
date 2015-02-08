package com.example.ar_acc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ListViewClass extends Activity {
	
	private Button mBtn_back,mBtn_del;
	private ListView mListview_data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		
		mListview_data = (ListView)findViewById(R.id.listview_data);
		
		final ArrayList<String> place = getIntent().getExtras().getStringArrayList("Place");
		
		
		CustomAdapter customAdapter = new CustomAdapter(place,getApplicationContext());
		mListview_data.setAdapter(customAdapter);
		
		mBtn_back = (Button) findViewById(R.id.btn_back);

		mBtn_back.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				finish();
			}
			
		});
		
/*		mBtn_del = (Button)findViewById(R.id.btn_del);
		
		mBtn_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i = 0; i < place.size(); i++ ){
					place.remove(i);
				}
			}
		});
*/
	}

}