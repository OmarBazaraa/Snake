package com.bazaraa.snake.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bazaraa.snake.R;
import com.bazaraa.snake.Utility;

import java.util.ArrayList;
import java.util.Random;


public class SnakeView extends View {
    //region VARIABLES & CONSTANTS & INTERFACES

    // DIMENSION VARIABLES
    private int mWidth;
    private int mHeight;
    private int mTileSize;
    private int mTilesInX;
    private int mTilesInY;
    private float mPaddingX;
    private float mPaddingY;

    // GRAPHICS & COLORS VARIABLES
    private Paint mPaint;
    private boolean mShowBorder = true;
    private int mBackgroundColor;
    private int mBorderColor;
    private int mBodyColor;
    private int mHeadColor;
    private int mFoodColor;

    // SNAKE VARIABLES
    private ArrayList<Coordinate> mSnakeBody;
    private Coordinate mSnakeFood;
    private int mMode;
    private int mControlMode;
    private int mDirection;
    private int mLastDirection;
    private int mStartMoveInterval;
    private int mMoveInterval;
    private int mStartX;
    private int mStartY;
    private int mStartLength;
    private int mDuration;
    private int mScore;

    // HELPER VARIABLES
    private Handler mMoveHandler;
    private Handler mDurationHandler;
    private Random mRnd;
    private GestureDetector mDetector;

    // ACTION LISTENER VARIABLES
    private OnSnakeMovedListener mOnSnakeMovedListener;
    private OnSnakeSecondPassedListener mOnSnakeSecondPassedListener;
    private OnSnakeFoodEatenListener mOnSnakeFoodEatenListener;
    private OnSnakeModeChangedListener mOnSnakeModeChangedListener;
    private OnSnakeDirectionChangedListener mOnSnakeDirectionChangedListener;
    private OnSnakeRestartedListener mOnSnakeRestartedListener;

    // MODE CONSTANTS
    public static final int MODE_RUNNING = 100;
    public static final int MODE_PAUSED = 101;
    public static final int MODE_DEAD = 102;

    // CONTROL MODE CONSTANTS
    public static final int CONTROL_MODE_TOUCH = 50;
    public static final int CONTROL_MODE_SWIPE = 51;

    // DIRECTION CONSTANTS
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 4;
    public static final int DIRECTION_RIGHT = 5;

    // STATE KEYS CONSTANTS
    public static final String KEY_SNAKE_BODY = "snake_body";
    public static final String KEY_SNAKE_FOOD = "snake_food";
    public static final String KEY_MODE = "snake_mode";
    public static final String KEY_DIRECTION = "snake_direction";
    public static final String KEY_LAST_DIRECTION = "snake_last_direction";
    public static final String KEY_MOVE_INTERVAL = "snake_move_interval";
    public static final String KEY_DURATION = "snake_duration";
    public static final String KEY_SCORE = "snake_score";

    // LISTENER INTERFACES
    public interface OnSnakeMovedListener {
        void onSnakeMoved();
    }

    public interface OnSnakeSecondPassedListener {
        void onSnakeSecondPassed(int duration);
    }

    public interface OnSnakeFoodEatenListener {
        void onSnakeFoodEaten(int score);
    }

    public interface OnSnakeModeChangedListener {
        void onSnakeModeChanged(int mode);
    }

    public interface OnSnakeDirectionChangedListener {
        void onSnakeDirectionChanged();
    }

    public interface OnSnakeRestartedListener {
        void onSnakeRestarted();
    }
    //endregion

    //region GETTERS & SETTERS

    public int getTileSize() {
        return mTileSize;
    }

    public void setTileSize(int tileSize) {
        mTileSize = tileSize;
        calculateDimensions();
        invalidate();
    }

    public int getTilesInX() {
        return mTilesInX;
    }

    public int getTilesInY() {
        return mTilesInY;
    }

    public boolean showBorder() {
        return mShowBorder;
    }

    public void setShowBorder(boolean showBorder) {
        if (mShowBorder != showBorder) {
            mShowBorder = showBorder;
            invalidate();
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            super.setBackgroundColor(backgroundColor);
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor;
            invalidate();
        }
    }

    public int getBodyColor() {
        return mBodyColor;
    }

    public void setBodyColor(int bodyColor) {
        if (mBodyColor != bodyColor) {
            mBodyColor = bodyColor;
            invalidate();
        }
    }

    public int getHeadColor() {
        return mHeadColor;
    }

    public void setHeadColor(int headColor) {
        if (mHeadColor != headColor) {
            mHeadColor = headColor;
            invalidate();
        }
    }

    public int getFoodColor() {
        return mFoodColor;
    }

    public void setFoodColor(int foodColor) {
        if (mFoodColor != foodColor) {
            mFoodColor = foodColor;
            invalidate();
        }
    }

    public ArrayList<Coordinate> getSnakeBody() {
        return mSnakeBody;
    }

    public Coordinate getSnakeHead() {
        if (mSnakeBody != null)
            return mSnakeBody.get(mSnakeBody.size() - 1);
        else
            return null;
    }

    public Coordinate getSnakeTail() {
        if (mSnakeBody != null)
            return mSnakeBody.get(0);
        else
            return null;
    }

    public Coordinate getSnakeFood() {
        if (mSnakeFood != null)
            return mSnakeFood;
        else
            return null;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        if (mode != mMode) {
            mMode = mode;

            if (mode == MODE_RUNNING) {
                if (mSnakeFood == null) {
                    mSnakeFood = getFreeTile();
                }

                mMoveHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mMode == MODE_RUNNING) {
                            onSnakeMove();
                            invalidate();
                            mMoveHandler.postDelayed(this, mMoveInterval);
                        }
                    }
                }, mMoveInterval);

                mDurationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mMode == MODE_RUNNING) {
                            mDuration += 1;

                            if (mOnSnakeSecondPassedListener != null) {
                                mOnSnakeSecondPassedListener.onSnakeSecondPassed(mDuration);
                            }

                            mDurationHandler.postDelayed(this, 1000);
                        }
                    }
                }, 1000);
            }

            if (mOnSnakeModeChangedListener != null) {
                mOnSnakeModeChangedListener.onSnakeModeChanged(mode);
            }
        }
    }

    public int getControlMode() {
        return mControlMode;
    }

    public void setControlMode(int mode) {
        if (mControlMode != mode) {
            mControlMode = mode;
        }
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        if (mMode == MODE_RUNNING && Math.abs(direction - mLastDirection) != 1) {
            mDirection = direction;

            if (mOnSnakeDirectionChangedListener != null) {
                mOnSnakeDirectionChangedListener.onSnakeDirectionChanged();
            }
        }
    }

    public int getStartMoveInterval() {
        return mStartMoveInterval;
    }

    public void setStartMoveInterval(int startMoveInterval) {
        mStartMoveInterval = startMoveInterval;
    }

    public int getMoveInterval() {
        return mMoveInterval;
    }

    public int getStartX() {
        return mStartX;
    }

    public void setStartX(int startX) {
        mStartX = startX;
    }

    public int getStartY() {
        return mStartY;
    }

    public void setStartY(int startY) {
        mStartY = startY;
    }

    public int getStartLength() {
        return mStartLength;
    }

    public void setStartLength(int startLength) {
        mStartLength = startLength;
    }

    public int getLength() {
        if (mSnakeBody != null)
            return mSnakeBody.size();
        else
            return 0;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getScore() {
        return mScore;
    }

    public void setOnSnakeMovedListener(OnSnakeMovedListener listener) {
        mOnSnakeMovedListener = listener;
    }

    public void setOnSnakeSecondPassedListener(OnSnakeSecondPassedListener listener) {
        mOnSnakeSecondPassedListener = listener;
    }

    public void setOnSnakeFoodEatenListener(OnSnakeFoodEatenListener listener) {
        mOnSnakeFoodEatenListener = listener;
    }

    public void setOnSnakeModeChangedListener(OnSnakeModeChangedListener listener) {
        mOnSnakeModeChangedListener = listener;
    }

    public void setOnSnakeDirectionChangedListener(OnSnakeDirectionChangedListener listener) {
        mOnSnakeDirectionChangedListener = listener;
    }

    public void setOnSnakeRestartedListener(OnSnakeRestartedListener listener) {
        mOnSnakeRestartedListener = listener;
    }
    //endregion

    //region CONSTRUCTORS & INITIALIZERS

    public SnakeView(Context context) {
        this(context, null);
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SnakeView, 0, 0);

        try {
            // DIMENSION VARIABLES
            mTileSize = a.getInt(R.styleable.SnakeView_tileSize, 32);

            // GRAPHICS & COLOR VARIABLES
            Resources res = getResources();
            mShowBorder = a.getBoolean(R.styleable.SnakeView_showBorder, true);
            mBackgroundColor = a.getColor(R.styleable.SnakeView_backgroundColor, res.getColor(R.color.amber_50));
            mBorderColor = a.getColor(R.styleable.SnakeView_borderColor, res.getColor(R.color.amber_900));
            mBodyColor = a.getColor(R.styleable.SnakeView_bodyColor, res.getColor(R.color.amber_500));
            mHeadColor = a.getColor(R.styleable.SnakeView_headColor, res.getColor(R.color.amber_800));
            mFoodColor = a.getColor(R.styleable.SnakeView_foodColor, res.getColor(R.color.red_500));

            // SNAKE VARIABLES
            mStartMoveInterval = a.getInt(R.styleable.SnakeView_startMoveInterval, 150);
            mStartX = a.getInt(R.styleable.SnakeView_startX, 2);
            mStartY = a.getInt(R.styleable.SnakeView_startY, 5);
            mStartLength = a.getInt(R.styleable.SnakeView_startLength, 3);
        }
        finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        super.setBackgroundColor(mBackgroundColor);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSnakeBody = new ArrayList<>();
        initSnakeBody();
        mMode = MODE_PAUSED;
        mControlMode = CONTROL_MODE_TOUCH;
        mDirection = DIRECTION_RIGHT;
        mLastDirection = mDirection;
        mMoveInterval = mStartMoveInterval;
        mMoveHandler = new Handler();
        mDurationHandler = new Handler();
        mRnd = new Random();
        mDetector = new GestureDetector(getContext(), new GesturesListener());
    }

    private void initSnakeBody() {
        mSnakeBody.clear();

        for (int i = mStartX; i < mStartX + mStartLength; i++) {
            mSnakeBody.add(new Coordinate(i, mStartY));
        }
    }

    public void restart() {
        initSnakeBody();
        mSnakeFood = getFreeTile();
        setMode(MODE_RUNNING);
        mDirection = DIRECTION_RIGHT;
        mLastDirection = mDirection;
        mMoveInterval = mStartMoveInterval;
        mDuration = 0;
        mScore = 0;

        if (mOnSnakeRestartedListener != null) {
            mOnSnakeRestartedListener.onSnakeRestarted();
        }
    }

    public Bundle saveStates() {
        Bundle args = new Bundle();

        args.putIntArray(KEY_SNAKE_BODY, coordinateArrayListToArray(mSnakeBody));
        args.putIntArray(KEY_SNAKE_FOOD, coordinateToArray(mSnakeFood));
        args.putInt(KEY_MODE, mMode);
        args.putInt(KEY_DIRECTION, mDirection);
        args.putInt(KEY_LAST_DIRECTION, mLastDirection);
        args.putInt(KEY_MOVE_INTERVAL, mMoveInterval);
        args.putInt(KEY_DURATION, mDuration);
        args.putInt(KEY_SCORE, mScore);

        return args;
    }

    public void restoreStates(Bundle args) {
        mSnakeBody = coordinateArrayToArrayList(args.getIntArray(KEY_SNAKE_BODY));
        mSnakeFood = arrayToCoordinate(args.getIntArray(KEY_SNAKE_FOOD));
        mMode = args.getInt(KEY_MODE);
        mDirection = args.getInt(KEY_DIRECTION);
        mLastDirection = args.getInt(KEY_LAST_DIRECTION);
        mMoveInterval = args.getInt(KEY_MOVE_INTERVAL);
        mDuration = args.getInt(KEY_DURATION);
        mScore = args.getInt(KEY_SCORE);
    }
    //endregion

    //region CONTROL METHODS

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private boolean detectTouch(float x, float y) {
        final float centerX = mWidth / 2f;
        final float centerY = mHeight - mHeight / 4f;

            if (Math.abs(centerX - x) > Math.abs(centerY - y)) {
                if (x > centerX) {
                    setDirection(DIRECTION_RIGHT);
                    return true;
                } else if (x < centerX) {
                    setDirection(DIRECTION_LEFT);
                    return true;
                }
            } else if (Math.abs(centerX - x) < Math.abs(centerY - y)) {
                if (y > centerY) {
                    setDirection(DIRECTION_DOWN);
                    return true;
                } else if (y < centerY) {
                    setDirection(DIRECTION_UP);
                    return true;
                }
            }

        return false;
    }

    public boolean detectSwipe(float dX, float dY, float velocityX, float velocityY) {
        final float minDistance = 10;
        final float minVelocity = 10;

        if (Math.abs(dX) > Math.abs(dY)) {
            if (Math.abs(dX) > minDistance && Math.abs(velocityX) > minVelocity) {
                if (dX > 0) {
                    SnakeView.this.setDirection(DIRECTION_RIGHT);
                    return true;
                }
                else {
                    SnakeView.this.setDirection(DIRECTION_LEFT);
                    return true;
                }
            }
        }
        else {
            if (Math.abs(dY) > minDistance && Math.abs(velocityY) > minVelocity) {
                if (dY > 0) {
                    SnakeView.this.setDirection(DIRECTION_DOWN);
                    return true;
                }
                else {
                    SnakeView.this.setDirection(DIRECTION_UP);
                    return true;
                }
            }
        }

        return false;
    }

    private void onSnakeMove() {
        if (mMode == MODE_RUNNING && mSnakeBody != null) {
            Coordinate tempHead = getNextStep();
            Coordinate tempTail = getSnakeTail();

            mSnakeBody.remove(0);

            if (mSnakeBody.contains(tempHead)) {
                mSnakeBody.add(0, tempTail);
                setMode(MODE_DEAD);
            }
            else {
                mSnakeBody.add(tempHead);

                if (mOnSnakeMovedListener != null) {
                    mOnSnakeMovedListener.onSnakeMoved();
                }

                if (mSnakeFood.equals(tempHead)) {
                    onSnakeGrowUp(tempTail);
                    mSnakeFood = getFreeTile();
                }
            }

            mLastDirection = mDirection;
        }
    }

    private void onSnakeGrowUp(Coordinate tail) {
        mSnakeBody.add(0, tail);
        mScore += 5000 / mMoveInterval;
        mMoveInterval *= 0.99;

        if (mOnSnakeFoodEatenListener != null) {
            mOnSnakeFoodEatenListener.onSnakeFoodEaten(mScore);
        }
    }

    private Coordinate getNextStep() {
        Coordinate resultPoint = null;
        int headX = getSnakeHead().getX();
        int headY = getSnakeHead().getY();

        switch (mDirection) {
            case DIRECTION_UP:
                if (headY - 1 >= 0) {
                    resultPoint = new Coordinate(headX, headY - 1);
                }
                else {
                    resultPoint = new Coordinate(headX, mTilesInY - 1);
                }
                break;
            case DIRECTION_DOWN:
                if (headY + 1 <= mTilesInY - 1) {
                    resultPoint = new Coordinate(headX, headY + 1);
                }
                else {
                    resultPoint = new Coordinate(headX, 0);
                }
                break;
            case DIRECTION_LEFT:
                if (headX - 1 >= 0) {
                    resultPoint = new Coordinate(headX - 1, headY);
                }
                else {
                    resultPoint = new Coordinate(mTilesInX - 1, headY);
                }
                break;
            case DIRECTION_RIGHT:
                if (headX + 1 <= mTilesInX - 1) {
                    resultPoint = new Coordinate(headX + 1, headY);
                }
                else {
                    resultPoint = new Coordinate(0, headY);
                }
                break;
        }

        return resultPoint;
    }

    private Coordinate getFreeTile() {
        if (mTilesInX == 0 || mTilesInY == 0) {
            return null;
        }

        Coordinate resultPoint = null;

        if (getLength() < mTilesInX * mTilesInY) {
            do {
                int x = mRnd.nextInt(mTilesInX);
                int y = mRnd.nextInt(mTilesInY);

                resultPoint = new Coordinate(x, y);
            } while (mSnakeBody.contains(resultPoint));
        }

        return resultPoint;
    }
    //endregion

    //region MEASURE & DRAW METHODS

    private void calculateDimensions() {
        mTilesInX = mWidth / mTileSize;
        mTilesInY = mHeight / mTileSize;

        mPaddingX = ((float) mWidth - mTilesInX * mTileSize) / 2;
        mPaddingY = ((float) mHeight - mTilesInY * mTileSize) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = mTileSize * 6;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                mWidth = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                mWidth = Math.min(desiredWidth, widthSize);
                break;
            default:
                mWidth = desiredWidth;
                break;
        }

        int desiredHeight = mTileSize * 10;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                mHeight = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                mHeight = Math.min(desiredHeight, heightSize);
                break;
            default:
                mHeight = desiredHeight;
                break;
        }

        calculateDimensions();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawSnakeAndFood(canvas);

        if (mShowBorder) {
            onDrawBorders(canvas);
        }
    }

    private void onDrawSnakeAndFood(Canvas canvas) {
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(mBodyColor);

        if (mSnakeBody != null) {
            for (Coordinate p : mSnakeBody) {
                canvas.drawRect(p.getRect(), mPaint);
            }

            mPaint.setColor(mHeadColor);
            canvas.drawRect(getSnakeHead().getRect(), mPaint);
        }

        if (mSnakeFood != null) {
            mPaint.setColor(mFoodColor);
            canvas.drawRect(mSnakeFood.getRect(), mPaint);
        }
    }

    private void onDrawBorders(Canvas canvas) {
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(mBorderColor);

        // VERTICAL LINES
        for (float x = mPaddingX; x <= mWidth - mPaddingX; x += mTileSize) {
            canvas.drawLine(x, mPaddingY, x, mHeight - mPaddingY, mPaint);
        }

        // HORIZONTAL LINES
        for (float y = mPaddingY; y <= mHeight - mPaddingY; y += mTileSize) {
            canvas.drawLine(mPaddingX, y, mWidth - mPaddingX, y, mPaint);
        }
    }
    //endregion

    //region HELPER METHODS & CLASSES

    public int[] coordinateArrayListToArray(ArrayList<Coordinate> arrayList) {
        if (arrayList == null) {
            return null;
        }

        int size = arrayList.size();
        int[] returnArray = new int[size * 2];

        for (int i = 0; i < size; i++) {
            Coordinate point = arrayList.get(i);

            returnArray[2 * i] = point.getX();
            returnArray[2 * i + 1] = point.getY();
        }

        return returnArray;
    }

    public ArrayList<Coordinate> coordinateArrayToArrayList(int[] array) {
        if (array == null) {
            return null;
        }

        ArrayList<Coordinate> returnArrayList = new ArrayList<>();

        for (int i = 0; i < array.length; i += 2) {
            Coordinate point = new Coordinate(array[i], array[i + 1]);

            returnArrayList.add(point);
        }

        return returnArrayList;
    }

    public int[] coordinateToArray(Coordinate point) {
        if (point == null) {
            return null;
        }

        return new int[]{point.getX(), point.getY()};
    }

    public Coordinate arrayToCoordinate(int[] array) {
        if (array == null) {
            return null;
        }

        return new Coordinate(array[0], array[1]);
    }

    private class GesturesListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (mControlMode == CONTROL_MODE_TOUCH) {
                detectTouch(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mMode == MODE_PAUSED) {
                setMode(MODE_RUNNING);
                return true;
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mControlMode == CONTROL_MODE_SWIPE) {
                float dX = e2.getX() - e1.getX();
                float dY = e2.getY() - e1.getY();

                return detectSwipe(dX, dY, velocityX, velocityY);
            }

            return false;
        }
    }

    private class Coordinate {
        private int mX;
        private int mY;

        public Coordinate(int x, int y) {
            mX = x;
            mY = y;
        }

        public int getX() {
            return mX;
        }

        public int getY() {
            return mY;
        }

        public RectF getRect() {
            return new RectF(
                    mPaddingX + mX * mTileSize,
                    mPaddingY + mY * mTileSize,
                    mPaddingX + (mX + 1) * mTileSize,
                    mPaddingY + (mY + 1) * mTileSize
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Coordinate) {
                Coordinate point = (Coordinate) obj;

                return (mX == point.getX() && mY == point.getY());
            }
            else {
                return false;
            }
        }
    }
    //endregion
}