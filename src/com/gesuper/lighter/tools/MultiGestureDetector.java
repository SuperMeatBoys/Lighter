package com.gesuper.lighter.tools;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class MultiGestureDetector {
	public static final String TAG = "MutiGestureDetector";
	
	public interface onMultiGestureListener {
		/**
		 * 手指触摸屏幕，由ACTION_DOWN, ACTION_POINTER_DOWN触发
		 * @param e
		 * @return
		 */
		boolean onDown(MotionEvent e);
		
		/**
		 * 手指离开屏幕， 有ACTION_UP, ACTION_POINTER_UP触发
		 * @param e
		 * @return
		 */
		boolean onSingTabUp(MotionEvent e);
		
		/*
		 * 手指在屏幕上按了片刻后抬起
		 * @param e
		 */
		void onShowPress(MotionEvent e);
		
		/*
		 * 手指在屏幕上按了较长时间后抬起
		 * @param e
		 */
		void onLongPress(MotionEvent e);
		
		/**
		 * 手指在屏幕上移动
		 * @param e1
		 * @param e2
		 * @param distanceX
		 * @param distanceY
		 * @return
		 */
		boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
		
		/*
		 * 手指在屏幕上快速滑动
		 * @param e1
		 * @param e2
		 * @param velocityX
		 * @param velocityY
		 * @return 
		 */
		boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
	}
	
	// GestureHandler所处理的Message的what属性可能为以下 常量：  
    // showPress手势  
    private static final int SHOW_PRESS = 1;  
    // 长按手势  
    private static final int LONG_PRESS = 2;  
    // SingleTapConfirmed手势  
    private static final int TAP_SINGLE = 3;
	
    private MultiGestureHandler mHandler;
    private onMultiGestureListener mListener;
    
    //当前的点击事件
    private MotionEvent mCurrentDownEvent;
    
	private class MultiGestureHandler extends Handler {
		MultiGestureHandler(){
			super();
		}
		
		MultiGestureHandler(Handler handler){
			super(handler.getLooper());
		}
		
		public void handleMessage(Message msg){
			int idx = (Integer) msg.obj;
			switch(msg.what){
			case SHOW_PRESS:
				mListener.onShowPress(mCurrentDownEvent);
				break;
			case LONG_PRESS:
				mListener.onLongPress(mCurrentDownEvent);
				break;
			case TAP_SINGLE:
				break;
			}
		}
	}
}

