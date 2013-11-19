package com.gesuper.lighter.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class MultiGestureDetector {
    public static final String TAG = "MutiGestureDetector";
    
    /** 
     * 事件信息类 <br/> 
     * 用来记录一个手势 
     */  
    public class EventInfo {  
        private MultiMotionEvent  mCurrentDownEvent;    // 当前的down事件 
        private boolean mAlwaysInTapRegion;            // 是否当前手指仅在小范围内移动，当手指仅在小范围内移动时，视为手指未曾移动过，不会触发onScroll手势  
        //private boolean mAlwaysInBiggerTapRegion;    // 是否当前手指在较大范围内移动，仅当此值为true时，双击手势才能成立  
        private float mLastMotionY;                    // 最后一次事件的X坐标  
        private float mLastMotionX;                    // 最后一次事件的Y坐标 
        private float mDownMotionX;                 //首次事件的X坐标
        private float mDownMotionY;                     //首次事件的Y坐标
        
        private EventInfo(MotionEvent e){
            this(new MultiMotionEvent(e));
        }
        
        private EventInfo(MultiMotionEvent me) {  
            mCurrentDownEvent = me;  
            mAlwaysInTapRegion = true;  
            //mAlwaysInBiggerTapRegion = true;
        } 
        
        public MultiMotionEvent getCurrentDownEvent(){
            return this.mCurrentDownEvent;
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
    
    /** 
     * 多点事件类 <br/> 
     * 将一个多点事件拆分为多个单点事件，并方便获得事件的绝对坐标 <br/> 
     * 绝对坐标用以在界面中找到触点所在的控件  
     */  
    public class MultiMotionEvent {  
        private MotionEvent mEvent;  
        private int mIndex;  
  
        private MultiMotionEvent(MotionEvent e) {  
            mEvent = e;  
            mIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;  
            // 等效于  mEvent.getActionIndex();  
        }  
  
        private MultiMotionEvent(MotionEvent e, int idx) {  
            mEvent = e;  
            mIndex = idx;  
        }  
  
        // 行为  
        public int getAction() {  
            int action = mEvent.getAction() & MotionEvent.ACTION_MASK;    // 等效于 mEvent.getActionMasked();  
            switch (action) {  
                case MotionEvent.ACTION_POINTER_DOWN:  
                    action = MotionEvent.ACTION_DOWN;  
                    break;  
                case MotionEvent.ACTION_POINTER_UP:  
                    action = MotionEvent.ACTION_UP;  
                    break;  
            }  
            return action;  
        }  
  
        // 返回X的绝对坐标  
        public float getX() {
            return mEvent.getX(mIndex) + mEvent.getRawX() - mEvent.getX();  
        }  
  
        // 返回Y的绝对坐标  
        public float getY() {  
            return mEvent.getY(mIndex) + mEvent.getRawY() - mEvent.getY();  
        }
        
        // 返回X的绝对坐标  
        public float getOffsetX() {
            return mEvent.getX(mIndex);  
        }  
  
        // 返回Y的绝对坐标  
        public float getOffsetY() {  
            return mEvent.getY(mIndex);  
        }
  
        // 事件发生的时间  
        public long getEventTime() {  
            return mEvent.getEventTime();  
        }  
  
        // 事件序号  
        public int getIndex() {  
            return mIndex;  
        }  
  
        // 事件ID  
        public int getId() {  
            return mEvent.getPointerId(mIndex);  
        }  
  
        // 释放事件对象，使系统能够继续使用  
        public void recycle() {  
            if (mEvent != null) {  
                mEvent.recycle();  
                mEvent = null;  
            }  
        }  
    } 
    
    public interface OnMultiGestureListener {
        /**
         * 手指触摸屏幕，由ACTION_DOWN, ACTION_POINTER_DOWN触发
         * @param e
         * @return
         */
        boolean onDown(MultiMotionEvent e);
        
        /**
         * 手指离开屏幕， 有ACTION_UP, ACTION_POINTER_UP触发
         * @param e
         * @return
         */
        boolean onSingleTapUp(MultiMotionEvent e);
        
        /*
         * 手指在屏幕上按了片刻后抬起
         * @param e
         */
        void onShowPress(MultiMotionEvent e);
        
        /*
         * 手指在屏幕上按了较长时间后抬起
         * @param e
         */
        void onLongPress(MultiMotionEvent e);
        
        /**
         * 手指在屏幕上移动
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */
        boolean onScroll(MultiMotionEvent e1, MultiMotionEvent e2, float distanceX, float distanceY);
        
        /*
         * 手指在屏幕上快速滑动
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return 
         */
        boolean onFling(MultiMotionEvent e1, MultiMotionEvent e2, float velocityX, float velocityY);
    }
    
    // 事件信息队列，队列的下标与MotionEvent的pointId对应  
    private static List<EventInfo> sEventInfos = new ArrayList<EventInfo>(10);  
    // 指定大点击区域的大小（这个比较拗口），这个值主要用于帮助判断双击是否成立  
    //private int mBiggerTouchSlopSquare = 20 * 20;  
    // 判断是否构成onScroll手势，当手指在这个范围内移动时，不触发onScroll手势  
    private int mTouchSlopSquare;
    // 最小滑动速度  
    private int mMinimumFlingVelocity;  
    // 最大滑动速度  
    private int mMaximumFlingVelocity;
    
    // 长按阀值，当手指按下后，在该阀值的时间内，未移动超过mTouchSlopSquare的距离并未抬起，则长按手势触发  
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();  
    // showPress手势的触发阀值，当手指按下后，在该阀值的时间内，未移动超过mTouchSlopSquare的距离并未抬起，则showPress手势触发  
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();  
    
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
    private final OnMultiGestureListener mListener;
  
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
                //mHandler.removeMessages(TAP_SINGLE, idx);// 移除单击事件确认  
                //info.mInLongPress = true;  
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
    public MultiGestureDetector(Context context, OnMultiGestureListener listener) {  
        this(context, listener, null);  
    }  
  
    /** 
     * 构造器2 
     * @param context 
     * @param listener 
     * @param handler 
     */  
    public MultiGestureDetector(Context context, OnMultiGestureListener listener, Handler handler) {  
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
            ViewConfiguration vc = ViewConfiguration.get(context);
            touchSlop = vc.getScaledTouchSlop();
            mMinimumFlingVelocity = vc.getScaledMinimumFlingVelocity();  
            mMaximumFlingVelocity = vc.getScaledMaximumFlingVelocity();  
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
        int id = info.mCurrentDownEvent.getId();  
        if (id < sEventInfos.size()) {  
            // if (sEventInfos.get(id) != null)  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, info(" + id + ") has not set to null !");  
            sEventInfos.set(info.mCurrentDownEvent.getId(), info);  
        } else if (id == sEventInfos.size()) {  
            sEventInfos.add(info);  
        } else {  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, invalidata id !");  
        }  
    }
    
    public int getFingerCount(){
        int count = 0, len = sEventInfos.size();
        for(int i=0;i<len;i++){
            if(sEventInfos.get(i) != null){
                count +=1;
            }
        }
        Log.v(TAG, "FingerCount: " + count);
        return count;
    }
    
    public EventInfo getEventInfoAt(int id){
        return sEventInfos.get(id);
    }
    
    public boolean onTouchEvent(MotionEvent ev) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(ev);// 把所有事件都添加到速度追踪器，为计算速度做准备  
        boolean handled = false;  
        final int action = ev.getAction(); // 获取Action    
        int idx = ev.getPointerId(ev.getActionIndex());// 获取触摸事件的id
        switch(action & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:  
        case MotionEvent.ACTION_POINTER_DOWN: {  
            EventInfo info = new EventInfo(MotionEvent.obtain(ev));  
            this.addEventIntoList(info);// 将手势信息保存到队列中    
            // 记录X坐标和Y坐标  
            info.mDownMotionX = info.mLastMotionX = info.mCurrentDownEvent.getX();  
            info.mDownMotionY = info.mLastMotionY = info.mCurrentDownEvent.getY();  
            
            if (mIsLongpressEnabled) {// 允许长按  
                mHandler.removeMessages(LONG_PRESS, idx);  
                mHandler.sendMessageAtTime(mHandler.obtainMessage(LONG_PRESS, idx), info.mCurrentDownEvent.getEventTime() + TAP_TIMEOUT  
                        + LONGPRESS_TIMEOUT);// 延时消息以触发长按手势  
                // Log.d(MYTAG, CLASS_NAME +  
                // ":add LONG_PRESS to handler  for idx " + idx);  
            }  
            mHandler.sendMessageAtTime(mHandler.obtainMessage(SHOW_PRESS, idx), info.mCurrentDownEvent.getEventTime() + TAP_TIMEOUT);// 延时消息，触发showPress手势  
            handled |= mListener.onDown(info.mCurrentDownEvent);// 触发onDown（）  
            break;
        }
        case MotionEvent.ACTION_UP:  
        case MotionEvent.ACTION_POINTER_UP: {  
            MultiMotionEvent currentUpEvent = new MultiMotionEvent(ev);  
            if (idx >= sEventInfos.size()) {  
                // Log.e(MYTAG, CLASS_NAME + ":ACTION_POINTER_UP, idx=" + idx + ", while sEventInfos.size()=" +  
                // sEventInfos.size());  
                break;  
            }  
            EventInfo info = sEventInfos.get(currentUpEvent.getId());  
            if (info == null) {  
                Log.e(TAG, "ACTION_POINTER_UP, idx=" + idx + ", Info = null");  
                break;  
            }

            removeEventFromList(currentUpEvent.getId());// 手指离开，则从队列中删除手势信息  
            //if (info.mInLongPress) {// 处于长按状态  
            //    mHandler.removeMessages(TAP_SINGLE, idx);// 可以无视这行代码  
            //    info.mInLongPress = false;  
            //} else if (info.mAlwaysInTapRegion) {// 尚未移动过    
            if (info.mAlwaysInTapRegion) {// 尚未移动过    
                handled = mListener.onSingleTapUp(currentUpEvent);// 触发onSingleTapUp事件  
            } else {  
                // A fling must travel the minimum tap distance  
                final VelocityTracker velocityTracker = mVelocityTracker;  
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);// 计算1秒钟内的滑动速度  
                // 获取X和Y方向的速度  
                final float velocityX = velocityTracker.getXVelocity(idx);  
                final float velocityY = velocityTracker.getYVelocity(idx);  
                // Log.i(MYTAG, CLASS_NAME + ":ACTION_POINTER_UP, idx=" + idx +  
                // ", vx=" + velocityX + ", vy=" + velocityY);  
                // 触发滑动事件  
                if ((Math.abs(velocityY) > mMinimumFlingVelocity) || (Math.abs(velocityX) > mMinimumFlingVelocity)) {  
                    handled = mListener.onFling(info.mCurrentDownEvent, currentUpEvent, velocityX, velocityY);  
                }else {
                    handled = mListener.onSingleTapUp(currentUpEvent);// 触发onSingleTapUp事件  
                }
            }  
            // Hold the event we obtained above - listeners may have changed the  
            // original.  
            if (action == MotionEvent.ACTION_UP) {    // 释放速度追踪器  
                mVelocityTracker.recycle();  
                mVelocityTracker = null;  
                // Log.w(MYTAG, CLASS_NAME +  
                // ":ACTION_POINTER_UP, mVelocityTracker.recycle()");  
            }  

            // Log.d(MYTAG, CLASS_NAME + "remove LONG_PRESS");  
            // 移除showPress和长按消息  
            mHandler.removeMessages(SHOW_PRESS, idx);  
            mHandler.removeMessages(LONG_PRESS, idx);  
            break;
        }
        case MotionEvent.ACTION_MOVE:{
            for (int rIdx = 0; rIdx < ev.getPointerCount(); rIdx++) {// 因为无法确定当前发生移动的是哪个手指，所以遍历处理所有手指  
                MultiMotionEvent e = new MultiMotionEvent(ev, rIdx);  
                if (e.getId() >= sEventInfos.size()) {  
                    Log.e(TAG, "ACTION_MOVE, idx=" + rIdx + ", while sEventInfos.size()=" +  
                    sEventInfos.size());  
                    break;  
                }  
                EventInfo info = sEventInfos.get(e.getId());  
                if (info == null) {  
                    Log.e(TAG, "ACTION_MOVE, idx=" + rIdx + ", Info = null");  
                    break;  
                }  
                //if (info.mInLongPress) {    // 长按，则不处理move事件  
                //    break;  
                //}
                // 当前坐标  
                float x = e.getX();  
                float y = e.getY();  
                // 距离上次事件移动的位置  
                final float scrollX = x - info.mLastMotionX;  
                final float scrollY = y - info.mLastMotionY;  
                // 计算从落下到当前事件，移动的距离 
                final int deltaX = (int)(x - info.mDownMotionX);  
                final int deltaY = (int)(y - info.mDownMotionY);  
                // Log.d(MYTAG, CLASS_NAME + "deltaX="+deltaX+";deltaY=" +  
                // deltaX +"mTouchSlopSquare=" + mTouchSlopSquare);  
                if (info.mAlwaysInTapRegion) {// 该手势尚未移动过（移动的距离小于mTouchSlopSquare,视为未移动过）  
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);  
                    if (distance > mTouchSlopSquare) {     // 移动距离超过mTouchSlopSquare  
                        handled = mListener.onScroll(info.mCurrentDownEvent, e, deltaX, deltaY);  
                        info.mLastMotionX = e.getX();  
                        info.mLastMotionY = e.getY();  
                        info.mAlwaysInTapRegion = false;  
                        // Log.d(MYTAG, CLASS_NAME +  
                        // ":remove LONG_PRESS for idx" + rIdx +  
                        // ",mTouchSlopSquare("+mTouchSlopSquare+"), distance("+distance+")");  
                        // 清除onSingleTapConform，showPress,longPress三种消息  
                        int id = e.getId();  
                        mHandler.removeMessages(TAP_SINGLE, id);  
                        mHandler.removeMessages(SHOW_PRESS, id);  
                        mHandler.removeMessages(LONG_PRESS, id);  
                    }  
                    //if (distance > mBiggerTouchSlopSquare) {// 移动距离大于mBiggerTouchSlopSquare，则无法构成双击事件  
                    //    info.mAlwaysInBiggerTapRegion = false;  
                    //}  
                } else if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {// 之前已经移动过了  
                    handled = mListener.onScroll(info.mCurrentDownEvent, e,  deltaX, deltaY);  
                    info.mLastMotionX = x;
                    info.mLastMotionY = y;  
                }  
            }  
            break;  
        }
        case MotionEvent.ACTION_CANCEL:  
            cancel();// 清理  
        }
        return handled;
    }

    // 清理所有队列  
    private void cancel() {  
        mHandler.removeMessages(SHOW_PRESS);  
        mHandler.removeMessages(LONG_PRESS);  
        mHandler.removeMessages(TAP_SINGLE);  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
        sEventInfos.clear(); 
    } 
}

