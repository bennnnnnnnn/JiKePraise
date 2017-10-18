package com.example.ben.jikepraise.View;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.ben.jikepraise.R;

/**
 * Created on 17/10/13.
 *
 * @author Ben
 */


public class JiKePraiseView extends View {
    private static final String TAG = "JiKePraiseView";
    private Paint picPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint sameTextPaint, newTextPaint, oldTextPaint;
    @ColorInt
    private int textColor;
    private int textSize;
    private int textStrokeWidth;
    private float textMoveHeight;
    private float textWidth;
    private float picDevHeight;
    private float textBaseLineHeight;
    private float sameNumWidth;
    private float numAnimateScale = 0;
    private float picAnimateScale = 0;
    private float shineAnimateScale = 0;
    private int number = 120;
    private int duration = 500;
    private String oldNumStr;
    private String newNumStr;
    private String sameToDrawNumStr = "";
    private String oldToDrawNumStr;
    private String newToDrawNumStr;
    private boolean isPlusNum;
    private boolean isInitial = true;
    private Bitmap praiseActiveBitmap;
    private Bitmap praiseNormalBitmap;
    private Bitmap praiseShiningBitmap;
    private Bitmap drawingBitmap;
    private int textToPic = 3; //dp
    private boolean active = false;
    private boolean isShineAnimating;
    int praiseWidth;
    int praiseHeight;
    int shineWidth;
    int shineHeight;
    float praiseX;
    float praiseY;
    float shineX;
    float shineY;

    {
        praiseActiveBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_praise_active);
        praiseNormalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_praise_normal);
        praiseShiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_shining);
    }

    public JiKePraiseView(Context context) {
        super(context);
        init(null);
    }

    public JiKePraiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JiKePraiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (null == attrs) {
            number = 0;
            textSize = dp2px(14);
            textStrokeWidth = dp2px(2);
            textColor = Color.parseColor("#bdbdbd");
            textToPic = dp2px(3);
            active = false;
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.JiKePraiseView);
            try {
                number = ta.getInt(R.styleable.JiKePraiseView_number, 0);
                textSize = ta.getDimensionPixelSize(R.styleable.JiKePraiseView_textSize, dp2px(14));
                textStrokeWidth = ta.getDimensionPixelSize(R.styleable.JiKePraiseView_textStrokeWidth, dp2px(2));
                textColor = ta.getColor(R.styleable.JiKePraiseView_textColor, Color.parseColor("#bdbdbd"));
                textToPic = ta.getDimensionPixelSize(R.styleable.JiKePraiseView_textToPic, dp2px(3));
                active = ta.getBoolean(R.styleable.JiKePraiseView_active, false);
            } finally {
                ta.recycle();
            }
        }

        sameTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        sameTextPaint.setColor(textColor);
        sameTextPaint.setTextSize(textSize);
        sameTextPaint.setStrokeWidth(textStrokeWidth);

        newTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        newTextPaint.setColor(textColor);
        newTextPaint.setTextSize(textSize);
        newTextPaint.setStrokeWidth(textStrokeWidth);

        oldTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        oldTextPaint.setColor(textColor);
        oldTextPaint.setTextSize(textSize);
        oldTextPaint.setStrokeWidth(textStrokeWidth);

        measureTextHeightAndBaseLineHeight();
        measureTextWidth();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState();
            }
        });
    }

    private void changeState() {
        if (active) {
            add(-1);
        } else {
            add(1);
        }
        active = !active;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPic(canvas);
        drawNumber(canvas);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTextpaints() {
        newTextPaint.setTextSize(textSize);
        newTextPaint.setAlpha((int) (255 * numAnimateScale)); //[0..255]
        if (isPlusNum) {
            newTextPaint.setLetterSpacing(0.05f * (1 - numAnimateScale));
        } else {
            newTextPaint.setLetterSpacing(-0.05f * (1 - numAnimateScale));
        }
        oldTextPaint.setTextSize(textSize);
        oldTextPaint.setAlpha((int) (255 * (1 - numAnimateScale))); //[0..255]
        if (isPlusNum) {
            oldTextPaint.setLetterSpacing(-0.05f * numAnimateScale);
        } else {
            oldTextPaint.setLetterSpacing(0.05f * numAnimateScale);
        }
        measureTextWidth();
    }

    private void
    drawNumber(Canvas canvas) {
        initTextpaints();
        if (isInitial) {
            canvas.drawText(String.valueOf(number), praiseActiveBitmap.getWidth() + textToPic, textBaseLineHeight + textMoveHeight, sameTextPaint);
            isInitial = false;
        } else {
            canvas.drawText(sameToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic, textBaseLineHeight + textMoveHeight, sameTextPaint);
            canvas.drawText(sameToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic, textBaseLineHeight + textMoveHeight, sameTextPaint);
            if (isPlusNum) {
                canvas.drawText(oldToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic + sameNumWidth, textBaseLineHeight + textMoveHeight * (1 - numAnimateScale), oldTextPaint);
                canvas.drawText(newToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic + sameNumWidth, textBaseLineHeight + textMoveHeight * (2 - numAnimateScale), newTextPaint);
            } else {
                canvas.drawText(oldToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic + sameNumWidth, textBaseLineHeight + textMoveHeight * (1 + numAnimateScale), oldTextPaint);
                canvas.drawText(newToDrawNumStr, praiseActiveBitmap.getWidth() + textToPic + sameNumWidth, textBaseLineHeight + textMoveHeight * numAnimateScale, newTextPaint);
            }
        }
    }

    private void drawPic(Canvas canvas) {
        measurePicPointXY();
        if (isInitial) {
            if (active) {
                canvas.save();
                canvas.translate(praiseX, praiseY);
                canvas.drawBitmap(praiseActiveBitmap, 0, 0, picPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(shineX, shineY);
                canvas.drawBitmap(praiseShiningBitmap, 0, 0, picPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.translate(praiseX, praiseY);
                canvas.drawBitmap(praiseNormalBitmap, 0, 0, picPaint);
                canvas.restore();
            }
        } else {
            if (isPlusNum) {
                canvas.save();
                canvas.translate(praiseX, praiseY);
                canvas.scale(picAnimateScale, picAnimateScale);
                canvas.drawBitmap(praiseActiveBitmap, 0, 0, picPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(shineX, shineY);
                canvas.scale((0.6f + 0.4f * shineAnimateScale), (0.6f + 0.4f * shineAnimateScale));
                canvas.drawBitmap(praiseShiningBitmap, 0, 0, picPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.translate(praiseX, praiseY);
                canvas.scale(picAnimateScale, picAnimateScale);
                canvas.drawBitmap(drawingBitmap, 0, 0, picPaint);
                canvas.restore();
                if (isShineAnimating) {
                    canvas.save();
                    canvas.translate(shineX, shineY);
                    canvas.scale((0.9f + 0.1f * shineAnimateScale), (0.9f + 0.1f * shineAnimateScale));
                    canvas.drawBitmap(praiseShiningBitmap, 0, 0, picPaint);
                    canvas.restore();
                }
            }
        }
    }

    private void slideText() {
        ObjectAnimator numAnimator = ObjectAnimator.ofFloat(this, "numAnimateScale", 0f, 1f);
        numAnimator.setDuration(duration);
        numAnimator.start();
    }

    private void slidePic() {
        if (isPlusNum) {
            Keyframe keyframe1 = Keyframe.ofFloat(0, 0.9f);
            Keyframe keyframe2 = Keyframe.ofFloat(0.6f, 1);
            Keyframe keyframe3 = Keyframe.ofFloat(0.8f, 0.95f);
            Keyframe keyframe4 = Keyframe.ofFloat(1, 1);
            PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("picAnimateScale", keyframe1, keyframe2, keyframe3, keyframe4);
            ObjectAnimator picAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holder);
            picAnimator.setDuration(duration);
            picAnimator.start();
        } else {
            Keyframe keyframe1 = Keyframe.ofFloat(0, 1);
            Keyframe keyframe2 = Keyframe.ofFloat(0.5f, 0.9f);
            Keyframe keyframe3 = Keyframe.ofFloat(1, 1);
            PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("picAnimateScale", keyframe1, keyframe2, keyframe3);
            PropertyValuesHolder holder1 = PropertyValuesHolder.ofObject("drawingBitmap", new BitmapEvaluator(), praiseActiveBitmap, praiseNormalBitmap);
            ObjectAnimator picAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holder, holder1);
            picAnimator.setDuration(duration);
            picAnimator.start();
        }

    }

    private class BitmapEvaluator implements TypeEvaluator<Bitmap> {

        @Override
        public Bitmap evaluate(float fraction, Bitmap startValue, Bitmap endValue) {
            if (fraction <= 0.5) {
                return praiseActiveBitmap;
            } else {
                return praiseNormalBitmap;
            }
        }
    }

    private void slideShine() {
        if (isPlusNum) {
            ObjectAnimator shineAnimator = ObjectAnimator.ofFloat(this, "shineAnimateScale", 0f, 1f);
            shineAnimator.setDuration((long) (0.6 * duration));
            shineAnimator.start();
        } else {
            ObjectAnimator shineAnimator = ObjectAnimator.ofFloat(this, "shineAnimateScale", 1f, 0);
            shineAnimator.setDuration((long) (0.5 * duration));
            shineAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isShineAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShineAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            shineAnimator.start();
        }
    }

    private void add(int num) {
        if (num == 0) {
            return;
        }
        int newNum = number + num;
        oldNumStr = String.valueOf(number);
        newNumStr = String.valueOf(newNum);
        this.number = newNum;
        setDrawNumStr();
        isPlusNum = num > 0;
        slideText();
        slidePic();
        slideShine();
    }

    private void setDrawNumStr() {
        if (oldNumStr == null || newNumStr == null || oldNumStr.equals(newNumStr)) {
            return;
        }
        if (oldNumStr.length() != newNumStr.length()) {
            oldToDrawNumStr = oldNumStr;
            newToDrawNumStr = newNumStr;
        } else {
            int length = oldNumStr.length();
            int i = 0;
            for (; i < length; i++) {
                if (oldNumStr.charAt(i) != newNumStr.charAt(i)) {
                    break;
                }
            }
            sameToDrawNumStr = oldNumStr.substring(0, i);
            oldToDrawNumStr = oldNumStr.substring(i);
            newToDrawNumStr = newNumStr.substring(i);
        }
    }

    public float getNumAnimateScale() {
        return numAnimateScale;
    }

    public void setNumAnimateScale(float numAnimateScale) {
        this.numAnimateScale = numAnimateScale;
        postInvalidate();
    }

    public float getPicAnimateScale() {
        return picAnimateScale;
    }

    public void setPicAnimateScale(float picAnimateScale) {
        this.picAnimateScale = picAnimateScale;
    }

    public Bitmap getDrawingBitmap() {
        return drawingBitmap;
    }

    public void setDrawingBitmap(Bitmap drawingBitmap) {
        this.drawingBitmap = drawingBitmap;
    }

    public float getShineAnimateScale() {
        return shineAnimateScale;
    }

    public void setShineAnimateScale(float shineAnimateScale) {
        this.shineAnimateScale = shineAnimateScale;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        setNumber(number, false);
    }

    public void setNumber(int number, boolean slide) {
        add(number - this.number);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextToPic() {
        return textToPic;
    }

    public void setTextToPic(int textToPic) {
        this.textToPic = textToPic;
    }

    private void measureTextHeightAndBaseLineHeight() {
        Paint.FontMetrics metrics = sameTextPaint.getFontMetrics();
        textMoveHeight = -metrics.ascent;
        textBaseLineHeight = -metrics.top;
    }

    private void measureTextWidth() {
        sameNumWidth = sameTextPaint.measureText(sameToDrawNumStr);
        textWidth = sameTextPaint.measureText(String.valueOf(number));
    }

    private void measurePicPointXY() {
        praiseWidth = praiseActiveBitmap.getWidth();
        praiseHeight = praiseActiveBitmap.getHeight();
        shineWidth = praiseShiningBitmap.getWidth();
        shineHeight = praiseShiningBitmap.getHeight();
        picDevHeight = textBaseLineHeight + 0.6f * textMoveHeight - shineHeight / 2 - praiseHeight / 2;
        if (isInitial) {
            praiseX = 0;
            praiseY = picDevHeight + shineHeight / 2;
            shineX = (praiseWidth - shineWidth) / 2;
            shineY = picDevHeight;
        } else {
            praiseX = praiseWidth / 2 - praiseWidth / 2 * picAnimateScale;
            praiseY = picDevHeight + praiseHeight / 2 + shineHeight / 2 - praiseHeight / 2 * picAnimateScale;
            if (isPlusNum) {
                shineX = praiseWidth / 2 - shineWidth / 2 * (0.6f + 0.4f * shineAnimateScale);
                shineY = picDevHeight + shineHeight / 2 - shineHeight / 2 * (0.6f + 0.4f * shineAnimateScale);
            } else {
                shineX = praiseWidth / 2 - shineWidth / 2 * (0.9f + 0.1f * shineAnimateScale);
                shineY = picDevHeight + shineHeight / 2 - shineHeight / 2 * (0.9f + 0.1f * shineAnimateScale);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
