package com.gesuper.lighter.tools;

import java.util.List;

import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.widget.CaseItemView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CaseListAdapter extends BaseAdapter{
	public static final String TAG = "EventListAdapter";
	
	private List<CaseModel> listItems;
	private Context context;
	
	private double baseH = 354, baseS = 100, baseL = 46;
	private double spanH = 49, spanL = 14;
	private double stepH = 7, stepL = 2;
	private int maxColorSpan = 7;
	public CaseListAdapter(Context context, int textViewResourceId, List<CaseModel> objects) {
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
		double dH = stepH, dL = stepL;
		if (n > maxColorSpan && o != 0) {
            dH = spanH / n;
            dL = spanL / n;
        }
		return Utils.HSLToRGB(baseH + o * dH, (o == 0 ? baseS - 10 : baseS)/100, (baseL + o*dL)/100);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		CaseItemView mItemView;
		if(convertView != null){
			mItemView = (CaseItemView) convertView;
		}
		else mItemView  = new CaseItemView(this.context);

		CaseModel mItemModel = this.listItems.get(position);
		mItemView.setModel(mItemModel);
		mItemView.setBgColor(this.calculateColor(position));
        return mItemView;
	}
	
}
