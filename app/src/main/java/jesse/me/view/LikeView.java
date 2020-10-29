package jesse.me.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.util.Collections;

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
    private Paint mTextPaint1;
    private Paint mBitmapPaint;

    private Bitmap mBmpNormal;
    private Bitmap mBmpLike;
    private Bitmap mBmpDecor;
    private int mLikeCnt;

    private float scaleFact;//缩放系数
    private float transYUp;//上移动画系数
    private float transYDown;//下移动画系数
    private float alphaIn;//渐显动画系数
    private float alphaOut;//渐隐动画系数

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

    public float getTransYUp() {
        return transYUp;
    }

    public void setTransYUp(float transYUp) {
        this.transYUp = transYUp;
        invalidate();
    }

    public float getTransYDown() {
        return transYDown;
    }

    public void setTransYDown(float transYDown) {
        this.transYDown = transYDown;
        invalidate();
    }

    public float getAlphaIn() {
        return alphaIn;
    }

    public void setAlphaIn(float alphaIn) {
        this.alphaIn = alphaIn;
        invalidate();
    }

    public float getAlphaOut() {
        return alphaOut;
    }

    public void setAlphaOut(float alphaOut) {
        this.alphaOut = alphaOut;
        invalidate();
    }

    private void init(){
        scaleFact = 1.0f;
        mLike = false;
        mLikeCnt = 1000;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.parseColor("#CECECE"));
        mTextPaint.setTextSize(SystemUtil.sp2px(getContext(), 16));
        mTextPaint1.setColor(Color.parseColor("#CECECE"));
        mTextPaint1.setTextSize(SystemUtil.sp2px(getContext(), 16));
    }

    public boolean isLike() {
        return mLike;
    }

    public void setLike(boolean like) {
        this.mLike = like;
        invalidate();
    }

    public void click(){
        setLike(!mLike);

        if (mLike){
            mLikeCnt++;
        }else{
            mLikeCnt--;
        }

        /**对icon应用scaleIn -> scaleOut 动画*/
        ObjectAnimator scaleIn = ObjectAnimator.ofFloat(this, "scaleFact", 1f, 0.9f);
        ObjectAnimator scaleOut = ObjectAnimator.ofFloat(this, "scaleFact", 0.9f, 1.0f);

        scaleIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                setLike(!mLike);
            }
        });

        /**icon 动画*/
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleIn, scaleOut);
        animatorSet.setDuration(100);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();

        /**对文字最后一位应用translationY && alpha动画, 将文字最后一位上弹&&渐隐
         *
         * 对文字最后一位应用translationY && alpha动画, 将新的最后一位上弹&&渐显
         *
         * */

        ObjectAnimator transYUp = ObjectAnimator.ofFloat(this, "transYUp", 0, 1);
        ObjectAnimator transYDown = ObjectAnimator.ofFloat(this, "transYDown", 0, 1);
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(this, "alphaIn", 0, 1);
        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(this, "alphaOut", 1, 0);

        AnimatorSet textAnimSet = new AnimatorSet();
        if (mLike){
            textAnimSet.playTogether(alphaOut, transYUp);
        }else{
            textAnimSet.playTogether(alphaOut, transYDown);
        }

        textAnimSet.setDuration(200).start();
        // TODO: 2020/10/27 1. 文字需要对个位进行动画, 要得到文字个位数的值

        // TODO: 2020/10/27 translationY需要移动文字高度的距离, 需测量文字高度: getTextBounds()

        // TODO: 2020/10/27 translationY与alpha需同时执行


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
        String text = String.valueOf(mLikeCnt-1);
        String text1 = String.valueOf(mLikeCnt);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length()-1, bounds);
        int x = mBmpNormal.getWidth() + SystemUtil.dp2px(getContext(), 5);
        int y = getHeight()/2 - (mBmpNormal.getHeight()/2 - bounds.height()/2);
        canvas.drawText(text,  x, y-(bounds.height()*transYUp), mTextPaint);

        mTextPaint.setAlpha((int) (255*alphaOut));
        canvas.drawText(text1, x, y-(bounds.height()*transYUp) + bounds.height(), mTextPaint1);

    }

    private void drawDecor(Canvas canvas) {

        mBmpDecor = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_message_like_shining);
        canvas.save();
        canvas.scale(scaleFact, scaleFact, mBmpDecor.getWidth()/2, mBmpDecor.getHeight()/2);
        canvas.drawBitmap(mBmpDecor, 0+SystemUtil.dp2px(getContext(), 2),
                getHeight()/2- mBmpLike.getHeight() -mBmpDecor.getHeight()/2, mBitmapPaint);
        canvas.restore();
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

        drawDecor(canvas);
    }


}
