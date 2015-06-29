package com.lr.ghp.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jimubox on 5/19/2015.
 */
public class LoanFlowVPIndicator extends LinearLayout
{

    /**
     * 绘制圆图的画笔
     */
    private Paint mPaint;
    /**
     * path构成一个圆图
     */
    private Path mPath;
    /**
     * 圆图的宽度
     */
    private int mTriangleWidth=20;
    /**
     * 圆图的高度
     */
    private int mTriangleHeight=20;

    /**
     * 圆图的宽度
     */
    private static final float RADIO_TRIANGEL = 1.0f / 5*3;

    /**
     * 初始时，圆图指示器的偏移量
     */
    private int mInitTranslationX;
    /**
     * 手指滑动时的偏移量
     */
    private float mTranslationX;

    /**
     * 默认的Tab数量
     */
    private static int COUNT_DEFAULT_TAB = 4;
    /**
     * tab数量
     */
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    /**
     * tab上的内容
     */
    private List<String> mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;

    private Context context;
    private AttributeSet attrs;
    public LoanFlowVPIndicator(Context context)
    {

        this(context, null);
        this.context=context;
        resetTextViewColor();
    }

    public LoanFlowVPIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        resetTextViewColor();
    }

    /**
     * 使用之前可以设置默认tab数量
     */
    public void initView(){
        // 获得自定义属性，tab的数量
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_item_count,
                COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0)
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        a.recycle();

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.line_color));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 修改默认的tab数量
     * @param num
     */
    public void setTabNum(int num){
        COUNT_DEFAULT_TAB=num;
    }
    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        canvas.save();
        //画线
        gline(canvas,5,25,getScreenWidth()-5,25,getResources().getColor(R.color.line_color));
        //画背景默认的几个圆点
        Drawable imgbg=getResources().getDrawable(R.mipmap.details_attention_account_out);
        BitmapDrawable bdbg = (BitmapDrawable) imgbg;
        canvas.drawBitmap(bdbg.getBitmap(), mInitTranslationX, 15, mPaint);
        canvas.drawBitmap(bdbg.getBitmap(), getWidth() / mTabVisibleCount+mInitTranslationX, 15, mPaint);
        canvas.drawBitmap(bdbg.getBitmap(), getWidth() / mTabVisibleCount*2+mInitTranslationX, 15, mPaint);
        canvas.drawBitmap(bdbg.getBitmap(), getWidth() / mTabVisibleCount*3+mInitTranslationX, 15, mPaint);

        // 画笔平移到正确的位置
        canvas.translate(mInitTranslationX + mTranslationX, 15);//(初始偏移量＋手指滑动量，图距离顶部5）
        Drawable img=getResources().getDrawable(R.mipmap.details_attention_account_in);
        BitmapDrawable bd = (BitmapDrawable) img;
        canvas.drawBitmap(bd.getBitmap(), 0, 0, mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 初始化圆图的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        // 初始化圆图
        initTriangle();
        // 初始时的偏移量
        mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth / 2;
    }

    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count)
    {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas)
    {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0)
        {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles)
            {
                // 添加view
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }

    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author zhy
     *
     */
    public interface PageChangeListener
    {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    // 对外的ViewPager的回调接口的设置
    public void setOnPageChangeListener(PageChangeListener pageChangeListener)
    {
        this.onPageChangeListener = pageChangeListener;
    }

    // 设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int pos)
    {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    public void highLightTextView(int position)
    {
        View view = getChildAt(position);
        if (view instanceof TextView)
        {
            ((TextView) view).setTextColor(getResources().getColor(R.color.high_color));
        }

    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            View view = getChildAt(i);
            if (view instanceof TextView)
            {
                ((TextView) view).setTextColor(getResources().getColor(R.color.nomal_color));
            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent()
    {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text)
    {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.setMargins(0,15,0,0);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
//        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setTextColor(getResources().getColor(R.color.nomal_color));
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 初始化圆图指示器
     */
    private void initTriangle()
    {
        mPath = new Path();

        //mTriangleHeight = (int) (mTriangleWidth / 5 / Math.sqrt(2))/3;

        mPath.moveTo(0, 0);

        mPath.lineTo(mTriangleWidth, 0);

        mPath.lineTo(mTriangleWidth, -mTriangleHeight);

//        mPath.lineTo(mTriangleWidth/2, 0);横屏的时候
//
//        mPath.lineTo(mTriangleWidth/2 , -mTriangleHeight);

        mPath.lineTo(0, -mTriangleHeight);

        mPath.close();
    }

    /**
     * 指示器跟随手指滚动，以及容器滚动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset)
    {
        /**
         * <pre>
         *  0-1:position=0 ;1-0:postion=0;
         * </pre>
         */
        // 不断改变偏移量，invalidate
        mTranslationX = getWidth() / mTabVisibleCount * (position + offset);

        int tabWidth = getScreenWidth() / mTabVisibleCount;

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (offset > 0 && position >= (mTabVisibleCount - 2)
                && getChildCount() > mTabVisibleCount&&position<(getChildCount()-2))
        {
            if (mTabVisibleCount != 1)
            {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth
                        + (int) (tabWidth * offset), 0);
            } else
            // 为count为1时 的特殊处理
            {
                this.scrollTo(
                        position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }

        invalidate();
    }

    /**
     * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate()
    {
        Log.e("TAG", "onFinishInflate");
        super.onFinishInflate();

        int cCount = getChildCount();

        if (cCount == 0)
            return;

        for (int i = 0; i < cCount; i++)
        {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view
                    .getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();

    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth()
    {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
    private void gline(Canvas canvas,float startX, float startY, float stopX, float stopY,int color){
        Paint paint=new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);//设置非填充
        paint.setStrokeWidth(1);//笔宽像素
        paint.setColor(color);//设笔色
        paint.setAntiAlias(true);//锯齿不显示
        canvas.drawLine(startX, startY, stopX, stopY, paint);//画线
    }
}
