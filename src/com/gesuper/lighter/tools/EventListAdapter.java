package com.gesuper.lighter.tools;

import java.util.List;

import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.ui.EventItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EventListAdapter extends ArrayAdapter<EventModel>{
	public static final String TAG = "EventListAdapter";
	
	public EventListAdapter(Context context, int textViewResourceId, List<EventModel> objects) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		EventItemView mItemView;
		if(convertView != null){
			mItemView = (EventItemView) convertView;
		}
		else mItemView  = new EventItemView(this.getContext());

		EventModel mItemModel = getItem(position);
		mItemView.setModel(mItemModel);
		if(mItemView.getModel().getId() == -1){
			mItemView.setPadding(0, - mItemView.getMHeight(), 0, 0);
		}
        return mItemView;
	}

}
