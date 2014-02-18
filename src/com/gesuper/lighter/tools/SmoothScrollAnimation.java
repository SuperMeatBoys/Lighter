package com.gesuper.lighter.tools;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SmoothScrollAnimation extends Animation {
	public static int SCROLL_LEFT = 0;
	public static int SCROLL_TOP = 1;
	
	private View view;
	private int startPosition;
	private int endPosition;
	private int direction;
	
	
	public SmoothScrollAnimation(View v, int start, int end, int direction){
		this.view = v;
		this.startPosition = start;
		this.endPosition = end;
		this.direction = direction;
	}
	

	@Override 
	public void initialize(int width, int height, int parentWidth, 
			int parentHeight) { 
		super.initialize(width, height, parentWidth, parentHeight);
	}
	
	// 生成Transformation 
	@Override 
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int position = (int) (this.startPosition + (this.endPosition - this.startPosition) * interpolatedTime);
		
		if(this.direction == 0){
			this.view.setPadding(position, this.view.getPaddingTop(), this.view.getPaddingRight(), this.view.getPaddingBottom());
		} else if(this.direction == 1){
			this.view.setPadding(view.getPaddingLeft(), position, view.getPaddingRight(), view.getPaddingBottom());
		}
	}
}
