package com.gesuper.lighter.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class MultiGestureDetector {
	public static final String TAG = "MutiGestureDetector";
	
	/** 
     * 事件信息类 <br/> 
     * 用来记录一个手势 
     */  
    private class EventInfo {  
        private MotionEvent mCurrentDownEvent;    // 当前的down事件  
        //private MotionEvent mPreviousUpEvent;    // 上一次up事件  for doubleclick
        private boolean mStillDown;                    // 当前手指是否还在屏幕上  
        private boolean mInLongPress;                // 当前事件是否属于长按手势  
        private boolean mAlwaysInTapRegion;            // 是否当前手指仅在小范围内移动，当手指仅在小范围内移动时，视为手指未曾移动过，不会触发onScroll手势  
        private boolean mAlwaysInBiggerTapRegion;    // 是否当前手指在较大范围内移动，仅当此值为true时，双击手势才能成立  
        private boolean mIsDoubleTapping;            // 当前手势，是否为双击手势  
        private float mLastMotionY;                    // 最后一次事件的X坐标  
        private float mLastMotionX;                    // 最后一次事件的Y坐标  
  
        private EventInfo(MotionEvent me) {  
            mCurrentDownEvent = me;  
            mStillDown = true;  
            mInLongPress = false;  
            mAlwaysInTapRegion = true;  
            mAlwaysInBiggerTapRegion = true;  
            mIsDoubleTapping = false;  
        }  
  
        // 释放MotionEven对象，使系统能够继续使用它们  
        public void recycle() {  
            if (mCurrentDownEvent != null) {  
                mCurrentDownEvent.recycle();  
                mCurrentDownEvent = null;  
            }  
//            if (mPreviousUpEvent != null) {  
//                mPreviousUpEvent.recycle();  
//                mPreviousUpEvent = null;  
//            }  
        }  
  
        @Override  
        public void finalize() {  
            this.recycle();  
        }  
    }
    
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
	
	// 事件信息队列，队列的下标与MotionEvent的pointId对应  
    private static List<EventInfo> sEventInfos = new ArrayList<EventInfo>(10);  
    // 指定大点击区域的大小（这个比较拗口），这个值主要用于帮助判断双击是否成立  
    private int mBiggerTouchSlopSquare = 20 * 20;  
    // 判断是否构成onScroll手势，当手指在这个范围内移动时，不触发onScroll手势  
    private int mTouchSlopSquare;
    // 最小滑动速度  
    private int mMinimumFlingVelocity;  
    // 最大滑动速度  
    private int mMaximumFlingVelocity;
    
	// GestureHandler所处理的Message的what属性可能为以下 常量：  
    // showPress手势  
    private static final int SHOW_PRESS = 1;  
    // 长按手势  
    private static final int LONG_PRESS = 2;  
    // SingleTapConfirmed手势  
    private static final int TAP_SINGLE = 3;
    
    // 手势处理器  
    private final MultiGestureHandler mHandler;  
    // 手势监听器  
    private final onMultiGestureListener mListener;
  
    // 长按允许阀值  
    private boolean mIsLongpressEnabled;  
    // 速度追踪器  
    private VelocityTracker mVelocityTracker;
    
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
			case SHOW_PRESS:{
				if (idx >= sEventInfos.size()) {  
                    // Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx +  
                    // ", while sEventInfos.size()="  
                    // + sEventInfos.size());  
                    break;  
                }  
                EventInfo info = sEventInfos.get(idx);  
                if (info == null) {  
                    // Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx +  
                    // ", Info = null");  
                    break;  
                }  
                // 触发手势监听器的onShowPress事件  
                mListener.onShowPress(info.mCurrentDownEvent);  
                break;  
			}
			case LONG_PRESS:{
				if (idx >= sEventInfos.size()) {  
                    // Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx +  
                    // ", while sEventInfos.size()="  
                    // + sEventInfos.size());  
                    break;  
                }  
                EventInfo info = sEventInfos.get(idx);  
                if (info == null) {  
                    // Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx +  
                    // ", Info = null");  
                    break;  
                }  
                // 触发手势监听器的onLongPress事件  
                mHandler.removeMessages(TAP_SINGLE, idx);// 移除单击事件确认  
                info.mInLongPress = true;  
                mListener.onLongPress(info.mCurrentDownEvent);  
                break;  
			}
			case TAP_SINGLE:
				break;
			}
		}
	}
	
	 /** 
     * 构造器1 
     * @param context 
     * @param listener 
     */  
    public MultiGestureDetector(Context context, onMultiGestureListener listener) {  
        this(context, listener, null);  
    }  
  
    /** 
     * 构造器2 
     * @param context 
     * @param listener 
     * @param handler 
     */  
    public MultiGestureDetector(Context context, onMultiGestureListener listener, Handler handler) {  
        if (handler != null) {  
            mHandler = new MultiGestureHandler(handler);  
        } else {  
            mHandler = new MultiGestureHandler();  
        }  
        mListener = listener;    
        init(context);  
    }

    private void init(Context context) {  
        if (mListener == null) {  
            throw new NullPointerException("OnGestureListener must not be null");  
        }  
        mIsLongpressEnabled = true;  
        int touchSlop;  
        if (context == null) {  
            touchSlop = ViewConfiguration.getTouchSlop();
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();  
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();  
        } else {// 允许识别器在App中，使用偏好的设定  
            final ViewConfiguration configuration = ViewConfiguration.get(context);  
            touchSlop = configuration.getScaledTouchSlop(); 
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();  
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();  
        }
        mTouchSlopSquare = touchSlop * touchSlop / 16; 
    }
    
    /** 
     * 设置是否允许长按 
     * @param isLongpressEnabled 
     */  
    public void setIsLongpressEnabled(boolean isLongpressEnabled) {  
        mIsLongpressEnabled = isLongpressEnabled;  
    }
    
    /** 
     * 从事件信息队列中移除指定序号的事件 
     *  
     * @param idx 
     */  
    private void removeEventFromList(int id) {  
        if (id > sEventInfos.size() || id < 0) {  
            // Log.e(MYTAG, CLASS_NAME + ".removeEventFromList(), id=" + id + ", while sEventInfos.size() =" +  
            // sEventInfos.size());  
            return;  
        }  
        sEventInfos.set(id, null);  
    }  
  
    /** 
     * 向事件队列中添加新信息 
     *  
     * @param e 
     */  
    private void addEventIntoList(EventInfo info) {  
        int id = info.mCurrentDownEvent.getPointerId(info.mCurrentDownEvent.getActionIndex());  
        if (id < sEventInfos.size()) {  
            // if (sEventInfos.get(id) != null)  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, info(" + id + ") has not set to null !");  
            sEventInfos.set(info.mCurrentDownEvent.getPointerId(info.mCurrentDownEvent.getActionIndex()), info);  
        } else if (id == sEventInfos.size()) {  
            sEventInfos.add(info);  
        } else {  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, invalidata id !");  
        }  
    } 
}

