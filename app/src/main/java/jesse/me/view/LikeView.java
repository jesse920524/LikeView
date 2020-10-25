package jesse.me.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import jesse.me.R;

/**
 * author: Jesse Fu
 * date: 2020/10/24 18:28
 * description: 仿写即刻点赞效果
 */
public class LikeView extends View {
    private static final String TAG = "LikeView";

    private boolean mLike;

    private Paint mTextPaint;
    private Paint mBitmapPaint;

    private Bitmap mBmpNormal;
    private Bitmap mBmpLike;
    private Bitmap mBmpDecor;
    private int mLikeCnt;

    private float scaleFact;

    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public float getScaleFact() {
        return scaleFact;
    }

    public void setScaleFact(float scaleFact) {
        this.scaleFact = scaleFact;
        invalidate();
    }

    private void init(){
        scaleFact = 1.0f;
        mLike = false;
        mLikeCnt = 1000;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.parseColor("#CECECE"));
        mTextPaint.setTextSize(SystemUtil.sp2px(getContext(), 16));
    }

    public boolean isLike() {
        return mLike;
    }

    public void setLike(boolean like) {
        this.mLike = like;
        invalidate();
    }

    public void click(){

        ObjectAnimator scaleIn = ObjectAnimator.ofFloat(this, "scaleFact", 1f, 0.9f);
        ObjectAnimator scaleOut = ObjectAnimator.ofFloat(this, "scaleFact", 0.9f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleIn, scaleOut);
        animatorSet.setDuration(240);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();
//        invalidate();
        this.mLike = !mLike;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制点赞小手
        drawBmp(canvas);
        //绘制文字
        drawTexts(canvas);

    }

    private void drawTexts(Canvas canvas) {
        String text = String.valueOf(mLikeCnt);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length()-1, bounds);
        int x = mBmpNormal.getWidth() + SystemUtil.dp2px(getContext(), 5);
        int y = getHeight()/2 - (mBmpNormal.getHeight()/2 - bounds.height()/2);
        canvas.drawText(String.valueOf(mLikeCnt),  x, y, mTextPaint);
    }

    private void drawDecor(Canvas canvas) {
        if (mLike == false) return;
        mBmpDecor = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_message_like_shining);
        canvas.drawBitmap(mBmpDecor, 0+SystemUtil.dp2px(getContext(), 2),
                getHeight()/2- mBmpLike.getHeight() -mBmpDecor.getHeight()/2, mBitmapPaint);
    }

    private void drawBmp(Canvas canvas) {
        /**根据like状态, 绘制图标*/
        if (mLike){
            drawBmpLike(canvas);
        }else{
            drawBmpNormal(canvas);
        }
    }

    private void drawBmpNormal(Canvas canvas) {
        mBmpNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_message_unlike);

        canvas.save();
        canvas.scale(scaleFact, scaleFact, mBmpNormal.getWidth()/2, mBmpNormal.getHeight()/2);
        canvas.drawBitmap(mBmpNormal, 0, getHeight()/2 - mBmpNormal.getHeight(), mBitmapPaint);
        canvas.restore();
    }

    /**绘制喜欢状态的icon*/
    private void drawBmpLike(Canvas canvas) {

        /**执行动画: icon先缩小再放大. 放大到最大后, 绘制新icon*/

        mBmpLike = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_message_like);
        /**缩放: 使用几何变换*/
        canvas.save();
        canvas.scale(scaleFact, scaleFact, mBmpLike.getWidth()/2, mBmpLike.getHeight()/2);
        canvas.drawBitmap(mBmpLike, 0,
                getHeight()/2 - mBmpLike.getHeight(), mBitmapPaint);
        canvas.restore();

//        drawDecor(canvas);
    }


}
