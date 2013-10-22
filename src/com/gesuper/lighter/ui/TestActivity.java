package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;
import com.gesuper.lighter.tools.Rotate3DAnimation;
import com.gesuper.lighter.widget.HeadViewBase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TestActivity extends Activity {  
    private Button btnDecrease;  
    private ImageView img;
    private EditText edit;
	private float rotateX;
	private int rotateY;
	private int rotateZ;
	private Camera camera;
	private float translateZ;
	private HeadViewBase item;
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        item = new HeadViewBase(this, R.layout.event_head);
        this.init();
    }
	private void init() {
		// TODO Auto-generated method stub
		this.edit = (EditText)findViewById(R.id.editText1);
		this.img = (ImageView)findViewById(R.id.imageView1);
		this.btnDecrease = (Button)findViewById(R.id.btnDecrease);
		this.camera = new Camera();
		this.btnDecrease.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				float height = 63;
				if(edit.getText().toString().length() != 0){
					height = Float.valueOf(edit.getText().toString());
				}
				//roateImage(height);
				applyRotation(0, 180);
			}
			
		});
		this.img.setImageBitmap(getBitmap());
	}
    
	private void roateImage(float height){
		rotateX = (float) (Math.acos(height / img.getHeight())*(180/Math.PI));
		//rotateX = 30;
		rotateY = 0;
		rotateZ = 0;
		translateZ = 0;
		refreshImage();
	}
	
	private void refreshImage() {
        // 获取待处理的图像
        //BitmapDrawable tmpBitDra = (BitmapDrawable) getResources().getDrawable(R.drawable.honeycomb);
        //Bitmap tmpBit = tmpBitDra.getBitmap();
		Bitmap tmpBit = this.getBitmap();
        // 开始处理图像
        // 1.获取处理矩阵
        // 记录一下初始状态。save()和restore()可以将图像过渡得柔和一些。
        // Each save should be balanced with a call to restore().
        this.camera.save();
        Matrix matrix = new Matrix();
        // rotate
        camera.rotateX(rotateX);
        camera.rotateY(rotateY);
        camera.rotateZ(rotateZ);
        // translate
        camera.translate(0, 0, translateZ);
        camera.getMatrix(matrix);
        // 恢复到之前的初始状态。
        camera.restore();
        // 设置图像处理的中心点
        int w = tmpBit.getWidth()/2;
        int h = tmpBit.getHeight()/2;
        matrix.preTranslate(-w, -h);
        matrix.postTranslate(w, h); 
        //matrix.preSkew(tmpBit.getWidth() >> 1, tmpBit.getHeight() >> 1);
        //matrix.postSkew(tmpBit.getWidth() >> 1, tmpBit.getHeight() >> 1);
        // 直接setSkew()，则前面处理的rotate()、translate()等等都将无效。
        // matrix.setSkew(skewX, skewY);
        // 2.通过矩阵生成新图像(或直接作用于Canvas)
        Log.d("TestActivity", "width=" + tmpBit.getWidth() + " height=" + tmpBit.getHeight());
        Bitmap newBit = null;
        try {
            // 经过矩阵转换后的图像宽高有可能不大于0，此时会抛出IllegalArgumentException
            newBit = Bitmap.createBitmap(tmpBit, 0, 0, tmpBit.getWidth(), tmpBit.getHeight(), matrix, false);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }

        Log.d("TestActivity", "width=" + newBit.getWidth() + " height=" + newBit.getHeight());
        if (newBit != null) {
            img.setImageBitmap(newBit);
        }
    }
	private void applyRotation(float start, float end) { 
		// 计算中心点 
		final float centerX = img.getWidth() / 2.0f; 
		final float centerY = img.getHeight() / 2.0f; 
		final Rotate3DAnimation rotation = new Rotate3DAnimation(start, end, 
		centerX, centerY, 310.0f, true); 
		rotation.setDuration(500); 
		rotation.setFillAfter(true); 
		rotation.setInterpolator(new AccelerateInterpolator()); 
		// 设置监听  
		img.startAnimation(rotation); 
	}
	 
	private Bitmap getBitmap() {
		// TODO Auto-generated method stub
		this.measureView(item);
		item.measure(
				MeasureSpec.makeMeasureSpec(item.getMeasuredWidth(), MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(item.getMeasuredHeight(), MeasureSpec.EXACTLY));
		item.layout(0, 0, item.getMeasuredWidth(),item.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(item.getMeasuredWidth(), item.getMeasuredHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		item.draw(c);
		return b;
	}
	
	private void measureView(View child) {  
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	    int screenWidth = wm.getDefaultDisplay().getWidth();
	    ViewGroup.LayoutParams p = child.getLayoutParams();  
		if (p == null) {  
		    p = new ViewGroup.LayoutParams(screenWidth,  
		    		ViewGroup.LayoutParams.WRAP_CONTENT);  
		}  
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
		int lpHeight = p.height;  
		int childHeightSpec;  
		if (lpHeight > 0) {  
		    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
		} else {  
		    childHeightSpec = MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED);  
		}  
		child.measure(childWidthSpec, childHeightSpec);  
	}
}  