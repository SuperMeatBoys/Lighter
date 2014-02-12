package com.gesuper.lighter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HeadViewBase extends FrameLayout {
	public final static String TAG = "HeadViewBase";
	private Matrix mMatrix;
	private Bitmap mBitmap;
	public HeadViewBase(Context context, int layoutId) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, layoutId, this);
	}
	
	public HeadViewBase(Context context, AttributeSet attrs){
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
    
    /*
     * 设置矩阵，并重绘
     */
    public void setMatrixValues(float[] array) {
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mMatrix.reset();
        float cosValue = (float) Math.cos(-Math.PI/6);  
        
        float sinValue = (float) Math.sin(-Math.PI/6);  
  
        mMatrix.setValues(  
  
                new float[]{  
  
                        cosValue, -sinValue, 100,  
  
                        sinValue, cosValue, 100,  
  
                        0, 0, 2});  
    }
    
    public void resetMatrix() {
        if (mMatrix != null) {
            mMatrix.reset();
        }
    }
    
    public Bitmap getBitmap(){
    	return this.mBitmap;
    }
}
