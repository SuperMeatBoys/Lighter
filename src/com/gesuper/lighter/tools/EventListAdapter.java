package com.gesuper.lighter.tools;

import java.util.List;

import com.gesuper.lighter.model.EventModel;
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
	
	private double baseH = 212, baseS = 93, baseL = 53;
	private double spanH = -12.5, spanS = 5, spanL = 12.5;
	private double stepH = -2.5, stepS = 1, stepL = 2.5;
	private int maxColorSpan = 6;
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
	
	private int calculateColor(int o){
		int n = this.getCount();
		double dH = stepH, dS = stepS, dL = stepL;
		if(n > this.maxColorSpan){
			dH = spanH / n;
			dS = spanS / n;
			dL = spanL / n;
		}
		return Utils.HSLToRGB(baseH + o * dH, Math.min(100, baseS + o * dS)/100, Math.min(100, baseL + o * dL)/100);
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
		Log.v(TAG, "setBgAlpha " + 255*position/this.getCount() + " " + mItemModel.getContent());
		mItemView.setBgColor(this.calculateColor(position));
        return mItemView;
	}

}
