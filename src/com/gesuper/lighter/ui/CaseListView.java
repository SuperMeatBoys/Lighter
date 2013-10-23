package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;
import com.gesuper.lighter.widget.MoveableListView;

import android.content.Context;
import android.util.AttributeSet;

public class CaseListView extends MoveableListView {
	protected MainActivity context;
	public CaseListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CaseListView(Context context, AttributeSet attrs){
		super(context, attrs, R.layout.case_head, R.layout.case_foot);
		// TODO Auto-generated constructor stub
	}
}
