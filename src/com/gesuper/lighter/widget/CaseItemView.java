package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.CaseModel;
import android.content.Context;

public class CaseItemView extends ItemViewBase{

	public CaseItemView(Context context) {
		super(context, R.layout.case_item);
		// TODO Auto-generated constructor stub
	}
	
	public void setModel(CaseModel mItemModel) {
		super.setModel(mItemModel);
	}
}
