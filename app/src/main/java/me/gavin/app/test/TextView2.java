//package me.gavin.app.test;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//import me.gavin.app.Config;
//import me.gavin.app.model.Book;
//import me.gavin.app.model.Page;
//import me.gavin.base.function.Consumer;
//
///**
// * TextView
// *
// * @author gavin.xiong 2018/4/22.
// */
//public class TextView2 extends View {
//
//    Book book;
//    final Page[] pages = new Page[Config.pageCount];
//
//    private Pager mPager;
//    private Flipper mFlipper;
//
//    Consumer<Page> onFlipCallback;
//
//    public void setOnFlipCallback(Consumer<Page> callback) {
//        this.onFlipCallback = callback;
//    }
//
//    public TextView2(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        setKeepScreenOn(true);
//    }
//
//    public void setBook(Book book) {
//        this.book = book;
//        mFlipper.offset(book.getOffset());
//    }
//
//    public void setPager(Pager pager) {
//        this.mPager = pager;
//        pager.mView = this;
//        invalidate();
//    }
//
//    public Pager getPager() {
//        return mPager;
//    }
//
//    public void setFlipper(Flipper flipper) {
//        this.mFlipper = flipper;
//        flipper.mView = this;
//        invalidate();
//    }
//
//    public Flipper getFlipper() {
//        return mFlipper;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mPager != null && mFlipper != null && mFlipper.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (mPager != null && mFlipper != null) {
//            mFlipper.onDraw(canvas);
//        }
//    }
//
//    public void onFlip() {
//        if (onFlipCallback != null) {
//            onFlipCallback.accept(pages[1]);
//        }
//    }
//}
