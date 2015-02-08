package com.example.ar_acc;

import java.util.ArrayList;

import com.google.android.gms.internal.mr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	private ArrayList<String> mPlace;
	private TextView mTxt_Pla;
	private Context mContext;

	public CustomAdapter(ArrayList<String> Place, Context context) {
		// TODO Auto-generated constructor stub
		mPlace = Place;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPlace.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater mInflat = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflat.inflate(R.layout.myitem, parent, false);
		mTxt_Pla = (TextView)view.findViewById(R.id.txt_place);		
		mTxt_Pla.setText(mPlace.get(position));
		return view;
	}

}
