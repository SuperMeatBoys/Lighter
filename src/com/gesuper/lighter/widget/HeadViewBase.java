package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.ui.TestActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class HeadViewBase extends FrameLayout {
	public final static String TAG = "HeadViewBase";
	private Context context;
	private Matrix mMatrix;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
	 private Bitmap mBitmap;
	private Canvas canvas;
    
	public HeadViewBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.event_head, this);
		this.context = context;
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
