package com.gesuper.lighter.tools;

import java.util.List;

import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.ui.MainActivity;
import com.gesuper.lighter.widget.EventItemView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EventListAdapter extends BaseAdapter{
	public static final String TAG = "EventListAdapter";
	
	private List<EventModel> listItems;
	private Context context;
	
	public EventListAdapter(Context context, int textViewResourceId, List<EventModel> objects) {
		this.context = context;
		this.listItems = objects;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listItems.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return this.listItems.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		EventItemView mItemView;
		if(convertView != null){
			mItemView = (EventItemView) convertView;
		}
		else mItemView  = new EventItemView(this.context);

		EventModel mItemModel = this.listItems.get(position);
		mItemView.setModel(mItemModel);
		Log.v(TAG, "" + ((MainActivity) this.context).calculateColor(position));
		mItemView.setBgColor(((MainActivity) this.context).calculateColor(position));
        return mItemView;
	}

}
