package com.gesuper.lighter.tools;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SwitchAnimation extends Animation {
	private View mUpView;
	private View mDownView;
	private int downStartPosition;
	private int downLength;
	private int startDistance;
	private int finalDistance;
	
	public SwitchAnimation(View up, View down, int downStart, int downLength, int startDistance, int finalDistance){
		this.mUpView = up;
		this.mDownView = down;
		this.downStartPosition = downStart;
		this.downLength = downLength;
		this.startDistance = startDistance;
		this.finalDistance = finalDistance;
		Log.v("Animation", downStart + "　" + downLength + " " + startDistance + " " + finalDistance);
	}
	
	@Override 
	public void initialize(int width, int height, int parentWidth, 
			int parentHeight) { 
		super.initialize(width, height, parentWidth, parentHeight);
	}
	
	// 生成Transformation 
	@Override 
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int distance = (int) (this.startDistance + (this.finalDistance - this.startDistance) * interpolatedTime);
		int paddingTop = (int) (this.downStartPosition + downLength * interpolatedTime);
		this.mDownView.setPadding(this.mDownView.getPaddingLeft(), paddingTop,
				this.mDownView.getPaddingRight(), this.mDownView.getPaddingBottom());
		
		this.mUpView.setPadding(this.mUpView.getPaddingLeft(), this.mUpView.getPaddingTop(),
				this.mUpView.getPaddingRight(), distance);
	}
	
}
