package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.widget.MoveableListView.OnItemFocusListener;
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventItemView extends FrameLayout implements OnItemFocusListener{
	public static final String TAG = "EventItemView";
	
	private Context context;
	private EventModel model;
	private LinearLayout mEventLinear;
	private EditText mEventContentEt;
	private TextView mEventContentTv;
	private TextView mEventCount;
	public EventItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		inflate(context, R.layout.event_item, this);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mEventLinear = (LinearLayout)findViewById(R.id.event_linear);
		this.mEventContentEt = (EditText)findViewById(R.id.event_content_et);
		this.mEventContentTv = (TextView)findViewById(R.id.event_content_tv);
		this.mEventCount = (TextView)findViewById(R.id.event_count);
		ViewGroup.LayoutParams p = this.mEventLinear.getLayoutParams();
		p.width = ((MainActivity)this.context).screenWidth;
		this.mEventLinear.setLayoutParams(p);
	}

	public void setModel(EventModel mItemModel) {
		// TODO Auto-generated method stub
		this.model = mItemModel;
		this.mEventContentEt.setText(mItemModel.getContent());
		this.mEventContentTv.setText(mItemModel.getContent());
		
		this.calcFocusRect();
	}

	public EventModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}

	public int getMHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void calcFocusRect(){
		focusRect.left = this.mEventContentTv.getLeft();
		focusRect.top = this.mEventContentTv.getTop();
		focusRect.right = this.mEventContentTv.getRight();
		focusRect.bottom = this.mEventContentTv.getBottom();
	}
	
	@Override
	public boolean isNeedFocus(int x, int y) {
		// TODO Auto-generated method stub
		this.calcFocusRect();
		if(focusRect.contains(x, y)){
			return true;
		}
		return false;
	}
	
	@Override
	public void onFocus() {
		// TODO Auto-generated method stub
		this.mEventContentTv.setVisibility(View.GONE);
		this.mEventContentEt.setVisibility(View.VISIBLE);
	}

	@Override
	public void outFocus() {
		// TODO Auto-generated method stub
		this.mEventContentTv.setText(this.mEventContentEt.getText());
		this.mEventContentEt.setVisibility(View.GONE);
		this.mEventContentTv.setVisibility(View.VISIBLE);
		
		this.calcFocusRect();
	}

}
