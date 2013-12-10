package com.gesuper.lighter.tools; 
import android.graphics.Camera; 
import android.graphics.Matrix; 
import android.view.animation.Animation; 
import android.view.animation.Transformation; 
public class Rotate3DAnimation extends Animation { 
	// 开始角度 
	private final float mFromDegrees; 
	// 结束角度 
	private final float mToDegrees; 
	// 中心点 
	private final float mCenterX; 
	// 摄像头 
	private Camera mCamera; 
	
	public Rotate3DAnimation(float fromDegrees, float toDegrees, float centerX) { 
		mFromDegrees = fromDegrees; 
		mToDegrees = toDegrees; 
		mCenterX = centerX; 
	}
	
	@Override 
	public void initialize(int width, int height, int parentWidth, 
			int parentHeight) { 
		super.initialize(width, height, parentWidth, parentHeight); 
		mCamera = new Camera(); 
	} 
	
	// 生成Transformation 
	@Override 
	protected void applyTransformation(float interpolatedTime, Transformation t) { 
		final float fromDegrees = mFromDegrees; 
		// 生成中间角度 
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime); 
		final float centerX = mCenterX; 
		final Camera camera = mCamera; 
		final Matrix matrix = t.getMatrix(); 
		camera.save();
		camera.rotateX(degrees); 
		// 取得变换后的矩阵 
		camera.getMatrix(matrix); 
		camera.restore(); 
		matrix.preTranslate(-centerX, 0); 
		matrix.postTranslate(centerX, 0); 
		
	} 
} 