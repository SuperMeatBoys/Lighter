package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.LinearLayout;

public class ListHeadView extends LinearLayout {
	private Context context;
	private Bitmap mBitmap;
	private Camera mCamera;
	private Matrix mMatrix;
	private int deltaX, deltaY, deltaZ, extraZ;
	public ListHeadView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		inflate(context, R.layout.event_head, this);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		
	}
	public void setDrawable(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    public void setDelta(int x, int y, int z, int extra) {
        deltaX += x;
        deltaY += y;
        deltaZ += z;
        extraZ += extra;
        invalidate();
    }

    public void reset() {
        deltaX = 0;
        deltaY = 0;
        deltaZ = 0;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	Log.v("ListHeadView", "onDrow");
        mCamera.save();
        mCamera.translate(10, 10, extraZ);
        mCamera.rotateX(deltaX);
        mCamera.rotateY(deltaY);
        mCamera.rotateZ(deltaZ);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        canvas.drawBitmap(mBitmap, mMatrix, null);
        super.onDraw(canvas);
    }
}
