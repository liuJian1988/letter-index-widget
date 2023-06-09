package com.lj.wiget.letterlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author： LJ
 * @data: 2022/04/06
 * @email： 1187502892@qq.com
 * <p>
 * <p>
 * 设置样式：
 * //1、设置字体大小
 * mLetterId.setTextSize(30);
 * //2、设置选中后背景padding
 * mLetterId.setBgPadding(2);
 * //3、设置选中背景颜色
 * mLetterId.setSelectedBgColor(Color.CYAN);
 * //4、设置字母颜色
 * mLetterId.setLetterSelectedColor(Color.YELLOW)
 * .setLetterUnSelectedColor(Color.DKGRAY);
 * <p>
 * <p>
 * 控制目前只支持单个字符，纵向排列，如需扩展重新控件，或者使用 View组合的方式。
 * 目前控件是通过单View绘制的方式实现，性能好。如果想要更加灵活，建议使用 View组合的方式实现
 * <p>
 * 控件编写的难点：
 * 1、如何是文字居中显示：https://www.cnblogs.com/tianzhijiexian/p/4297664.html
 */
public class LetterIndexView extends View {
    /**
     * 定义默认值
     */
    private final int DEFAULT_TEXT_SIZE = 30;
    private final int DEFAULT_BG_PADDING = 3;
    private final int DEFAULT_SELECTED_TEXT_COLOR = Color.WHITE;
    private final int DEFAULT_UNSELECTED_TEXT_COLOR = Color.GRAY;
    private final int DEFAULT_BG_COLOR = Color.RED;
    private final int DEFAULT_MIN_SPACE = 10;
    private final int DEFAULT_MAX_SPACE = 50;

    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mTextHeight = 0;// 文字的高度
    private final List<String> mLetters = new ArrayList<>();// 原始字母列表
    private List<LetterPosition> mLetterPositionList = new ArrayList<>();// 记录字母的位置
    private LetterPosition mCurrentSelectedLetterPosition;//当前选中的item
//    /**
//     * 字母 垂直方向间隔是在 "miniSpace～maxSpace" 之间，实际间隔根据控件本身空间大小决定
//     */
//    private final int miniSpace = DEFAULT_MIN_SPACE;// 最小间隔
//    private final int maxSpace = DEFAULT_MAX_SPACE;// 最大间隔

    /**
     * 字母相对背景的padding，这个值只会决定bg的大小
     */
    private float mBgPadding;

    private float mTextSize;

    private float mLetterSpaceBetween;

    /**
     * 文字选中和非选中状态下的颜色
     */
    private int mSelectedTextColor;
    private int mUnSelectedTextColor;
    private int mSelectedBgColor;

    private OnLetterSelectedListener mLetterSelectedListener;// 字母选中监听

    public LetterIndexView(Context context) {
        this(context, null);
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf(context, attrs, defStyleAttr);
    }

    private void checkLetterSpaceBetween(float mLetterSpaceBetween) {
        if (mLetterSpaceBetween < DEFAULT_MIN_SPACE) {
            mLetterSpaceBetween = DEFAULT_MIN_SPACE;
            return;
        }
        if (mLetterSpaceBetween > DEFAULT_MAX_SPACE) {
            mLetterSpaceBetween = DEFAULT_MAX_SPACE;
            return;
        }

    }

    private void initSelf(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LetterIndexView, defStyleAttr, 0);
        mTextSize = a.getDimensionPixelSize(R.styleable.LetterIndexView_textSize, DEFAULT_TEXT_SIZE);
        mBgPadding = a.getDimensionPixelSize(R.styleable.LetterIndexView_letterPadding, DEFAULT_BG_PADDING);
        mSelectedBgColor = a.getColor(R.styleable.LetterIndexView_letterSelectedBgColor, DEFAULT_BG_COLOR);
        mSelectedTextColor = a.getColor(R.styleable.LetterIndexView_letterSelectedColor, DEFAULT_SELECTED_TEXT_COLOR);
        mUnSelectedTextColor = a.getColor(R.styleable.LetterIndexView_unSelectedLetterColor, DEFAULT_UNSELECTED_TEXT_COLOR);
        mLetterSpaceBetween = a.getDimensionPixelSize(R.styleable.LetterIndexView_letterSpaceBetween, DEFAULT_MIN_SPACE);
        checkLetterSpaceBetween(mLetterSpaceBetween);
        a.recycle();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = computeMyRequestHeight();
        int width = computeMyRequestWidth();
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    private int computeMyRequestWidth() {
        if (mLetters.size() <= 0) {
            return 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : mLetters) {
            stringBuilder.append(letter);

        }
        float[] widths = new float[mLetters.size()];
        mTextPaint.getTextWidths(stringBuilder.toString(), widths);
        Arrays.sort(widths);
        float padding = getPaddingStart() + getPaddingEnd();
        return (int) (widths[widths.length - 1] + padding + 0.5);
    }

    private int computeMyRequestHeight() {
        if (mLetters.size() <= 0) {
            return 0;
        }
        float singleTextHeight = mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent;
        float textHeight = mLetters.size() * singleTextHeight;
        float space = (mLetters.size() - 1) * mLetterSpaceBetween;
        float padding = getPaddingTop() + getPaddingBottom();
        return (int) (textHeight + space + padding + 0.5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mLetters.size() > 0) {
            layoutLetter();
        }
    }

    /**
     * 设置文字大小
     *
     * @param textSize 使用dp为单位
     */
    public LetterIndexView setTextSize(int textSize) {
        mTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                textSize,
                getResources().getDisplayMetrics());
        mTextPaint.setTextSize(mTextSize);
        if (getHeight() > 0) {
            layoutLetter();
            requestLayout();
        }
        return this;
    }

    /**
     * 设置选中后背景中字母的padding,如果不设置，那么背景正好包裹。
     * 设置后，背景的半径 = padding+文字宽度/2；
     *
     * @param padding
     */
    public LetterIndexView setBgPadding(int padding) {
        this.mBgPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding,
                getResources().getDisplayMetrics());
        if (getHeight() > 0) {
            // 只需要重新绘制，不需要重新计算大小
            postInvalidateOnAnimation();
        }
        return this;
    }

    /**
     * 设置选中后背景中颜色
     *
     * @param color
     */
    public LetterIndexView setSelectedBgColor(int color) {
        this.mSelectedBgColor = color;
        if (getHeight() > 0) {
            // 只需要重新绘制，不需要重新计算大小
            postInvalidateOnAnimation();
        }
        return this;
    }

    /**
     * 设置选中后文字的颜色
     *
     * @param color
     */
    public LetterIndexView setLetterSelectedColor(int color) {
        this.mSelectedTextColor = color;
        if (getHeight() > 0) {
            // 只需要重新绘制，不需要重新计算大小
            postInvalidateOnAnimation();
        }
        return this;
    }

    /**
     * 设置非选中字母颜色
     *
     * @param color
     */
    public LetterIndexView setLetterUnSelectedColor(int color) {
        this.mUnSelectedTextColor = color;
        if (getHeight() > 0) {
            // 只需要重新绘制，不需要重新计算大小
            postInvalidateOnAnimation();
        }
        return this;
    }

    /**
     * 跟新字母列表数据，并刷新UI
     *
     * @param letters
     */
    public void setLetterDataAndRefresh(List<String> letters) {
        mLetters.clear();
        mLetters.addAll(letters);
        if (getHeight() > 0) {
            layoutLetter();
            requestLayout();
        }
    }

    private void layoutLetter() {
        // 计算文字高度
        mTextHeight = mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent;
        //根据尺寸计算 字母的排列位置，根据字母大小和垂直方向间隔
        // 1、确定字母之间的间隔，根据高度计算字母间隔，
//        double space = computeSpace(mTextHeight, miniSpace, mLetters.size(), maxSpace);
        //2、计算每个字母的绘制位置
        mLetterPositionList = computeLetterPosition(mLetterSpaceBetween, mTextHeight, getWidth() / 2, mLetters);
    }

    /**
     * 计算文字位置参见：https://www.cnblogs.com/tianzhijiexian/p/4297664.html
     */
    private List<LetterPosition> computeLetterPosition(double space, float textHeight, int x, List<String> letters) {
        List<LetterPosition> letterPositions = new ArrayList<>();
        // 让字母布局尽量剧中显示
        int currentYPosition = getPaddingTop();
        for (String letter : letters) {
            float centerY = currentYPosition + textHeight / 2;
            float y = centerY - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
            RectF bounds = new RectF(0, centerY - textHeight / 2, getWidth(), centerY + textHeight / 2);
            letterPositions.add(new LetterPosition(x, y, letter, centerY, bounds));
            currentYPosition += (textHeight + space);
        }
        mCurrentSelectedLetterPosition = letterPositions.get(0);
        return letterPositions;
    }

//    /**
//     * 计算字母垂直间隔
//     *
//     * @param textHeight
//     * @param letterCount
//     * @param miniSpace
//     * @param maxSpace
//     * @return
//     */
//    private double computeSpace(float textHeight, int letterCount, int miniSpace, int maxSpace) {
//        if (letterCount <= 1) {
//            return 0;
//        }
//        double space = (getHeight() - (textHeight * letterCount + getPaddingTop() + getPaddingBottom())) / (letterCount - 1);
//        if (space < miniSpace) {
//            space = miniSpace;
//        } else if (space > maxSpace) {
//            space = maxSpace;
//        }
//        return space;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制选中的字母背景
        drawLetterBg(canvas, mCurrentSelectedLetterPosition, mTextHeight);
        // 绘制字母
        drawLetter(mLetterPositionList, canvas);
    }

    private void drawLetterBg(Canvas canvas, LetterPosition letterPosition, float textHeight) {
        if (letterPosition == null) {
            return;
        }
        mBgPaint.setColor(mSelectedBgColor);
        canvas.drawCircle(letterPosition.x, letterPosition.centerY, textHeight / 2 + mBgPadding, mBgPaint);
    }

    private void drawLetter(List<LetterPosition> letterPositionList, Canvas canvas) {
        for (LetterPosition letterPosition : letterPositionList) {
            if (letterPosition == mCurrentSelectedLetterPosition) {
                mTextPaint.setColor(mSelectedTextColor);
            } else {
                mTextPaint.setColor(mUnSelectedTextColor);
            }
            canvas.drawText(letterPosition.letter, letterPosition.x, letterPosition.y, mTextPaint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                findCurrentLetter(event.getY(), event.getX(), mLetterPositionList, mTextHeight);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mLetterSelectedListener != null) {
                    mLetterSelectedListener.onLetterUp();
                }
                break;
        }
        return true;
    }

    private void findCurrentLetter(float y, float x, List<LetterPosition> letterPositionList, float textHeight) {
        for (int i = 0; i < letterPositionList.size(); i++) {
            LetterPosition letterPosition = letterPositionList.get(i);
            if (letterPosition.mBounds.contains(x, y)) {
                if (mCurrentSelectedLetterPosition != letterPosition) {
                    if (mLetterSelectedListener != null) {
                        mLetterSelectedListener.onLetterSelectedChange(mCurrentSelectedLetterPosition != null ? mCurrentSelectedLetterPosition.letter : ""
                                , letterPosition.letter, i, letterPosition.centerY);
                    }
                    mCurrentSelectedLetterPosition = letterPosition;
                    postInvalidateOnAnimation();
                }
                break;
            }
        }

    }

    public void setOnLetterSelectedListener(OnLetterSelectedListener letterSelectedListener) {
        this.mLetterSelectedListener = letterSelectedListener;
    }


    public interface OnLetterSelectedListener {
        void onLetterSelectedChange(String oldLetter, String newLetter, int index, double centerY);

        void onLetterUp();
    }

    private static class LetterPosition {
        //文字的绘制坐标
        private final float x;
        private final float y;
        private final String letter;
        private final float centerY;//中心位置，可用来绘制背景
        private final RectF mBounds;//字母绘制的边界

        public LetterPosition(float x, float y, String letter, float centerY, RectF bounds) {
            this.x = x;
            this.y = y;
            this.letter = letter;
            this.centerY = centerY;
            this.mBounds = bounds;
        }
    }
}
