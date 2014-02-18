package com.gesuper.lighter.tools;

import java.util.List;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.ui.CaseActivity;
import com.gesuper.lighter.widget.CaseItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CaseListAdapter extends BaseAdapter{
	public static final String TAG = "EventListAdapter";
	
	private List<CaseModel> listItems;
	private Context context;
	
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		CaseItemView mItemView;
		if(convertView != null){
			mItemView = (CaseItemView) convertView;
		}
		else mItemView  = new CaseItemView(this.context);

		CaseModel mItemModel = this.listItems.get(position);
		mItemView.setModel(mItemModel);
		mItemView.setBgColor(((CaseActivity) this.context).calculateColor(position));
		if(position == 0){
			View v = mItemView.findViewById(R.id.item_linear_bg);
			v.setPadding(v.getPaddingLeft(), v.getPaddingBottom(), v.getPaddingRight(), v.getPaddingBottom());
		}
        return mItemView;
	}
	
}
