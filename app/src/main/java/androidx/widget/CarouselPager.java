package androidx.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图视图页控件，可以通过自定义的图片显示指示器的样式形状，shape图片或者
 * 设置数据通过{@link CarouselPager#setAdapter},同时支持设置指示器布局的边距
 * 和指示器本身的边距大小，指示器的位置 {@link CarouselPager#setIndicatorGravity}
 */
public class CarouselPager extends FrameLayout implements ViewPager.OnPageChangeListener, CarouselAdapter.OnDataSetChangeListener {

    public final String TAG = CarouselPager.class.getSimpleName();
    public static final int RES_IMAGE = 0x35795146;
    //上下文
    private Context context;
    //轮播页
    private CarouselViewPager pager;
    //轮播数据
    private CarouselAdapter adapter;
    //指示器布局
    private LinearLayout indicatorLayout;
    //轮播控制
    private CarouselMessenger messenger;
    //选中图资源
    private int indicatorSelectedResource = R.drawable.ui_core_carousel_indicator_selected;
    //未选中图资源
    private int indicatorUnSelectedResource = R.drawable.ui_core_carousel_indicator_unselected;
    //指示器布局间距
    private float indicatorLayoutMargin = 0;
    //指示器布局左间距
    private float indicatorLayoutMarginLeft = dpToPx(10);
    //指示器布局上间距
    private float indicatorLayoutMarginTop = dpToPx(10);
    //指示器布局右间距
    private float indicatorLayoutMarginRight = dpToPx(10);
    //指示器布局下间距
    private float indicatorLayoutMarginBottom = dpToPx(10);
    //指示器间距
    private float indicatorMargin = 0;
    //指示器左间距
    private float indicatorMarginLeft = dpToPx(5);
    //指示器上间距
    private float indicatorMarginTop = 0;
    //指示器右间距
    private float indicatorMarginRight = dpToPx(5);
    //指示器下间距
    private float indicatorMarginBottom = 0;
    //指示器位置
    private int indicatorGravity = Gravity.BOTTOM | Gravity.CENTER;
    //是否自动播放
    private boolean isAutoPlay = true;
    //轮播时间
    private int duration = 3 * 1000;
    //是否循环
    private boolean cycle = true;
    //页面改变监听
    private OnCarouselChangeListener onCarouselChangeListener;
    //个数
    private int itemCount = 0;
    //资源布局
    private int listItem = R.layout.ui_core_carousel_item;

    public CarouselPager(@NonNull Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public CarouselPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public CarouselPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CarouselPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化属性参数
     *
     * @param context 上下文
     * @param attrs   属性
     */
    protected void initAttributeSet(Context context, AttributeSet attrs) {
        this.context = context;
        messenger = new CarouselMessenger(this);
        pager = new CarouselViewPager(context);
        indicatorLayout = new LinearLayout(context);
        //自定义属性
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CarouselPager);
            indicatorSelectedResource = typedArray.getResourceId(R.styleable.CarouselPager_indicatorSelected, indicatorSelectedResource);
            indicatorUnSelectedResource = typedArray.getResourceId(R.styleable.CarouselPager_indicatorUnSelected, indicatorUnSelectedResource);
            isAutoPlay = typedArray.getBoolean(R.styleable.CarouselPager_autoPlay, isAutoPlay);
            cycle = typedArray.getBoolean(R.styleable.CarouselPager_cycle, cycle);
            duration = typedArray.getInt(R.styleable.CarouselPager_duration, duration);
            indicatorGravity = typedArray.getInt(R.styleable.CarouselPager_indicatorGravity, indicatorGravity);
            indicatorLayoutMargin = typedArray.getDimension(R.styleable.CarouselPager_indicatorLayoutMargin, indicatorLayoutMargin);
            indicatorLayoutMarginLeft = typedArray.getDimension(R.styleable.CarouselPager_indicatorLayoutMarginLeft, indicatorLayoutMarginLeft);
            indicatorLayoutMarginTop = typedArray.getDimension(R.styleable.CarouselPager_indicatorLayoutMarginTop, indicatorLayoutMarginTop);
            indicatorLayoutMarginRight = typedArray.getDimension(R.styleable.CarouselPager_indicatorLayoutMarginRight, indicatorLayoutMarginRight);
            indicatorLayoutMarginBottom = typedArray.getDimension(R.styleable.CarouselPager_indicatorLayoutMarginBottom, indicatorLayoutMarginBottom);
            indicatorMargin = typedArray.getDimension(R.styleable.CarouselPager_indicatorMargin, indicatorMargin);
            indicatorMarginLeft = typedArray.getDimension(R.styleable.CarouselPager_indicatorMarginLeft, indicatorMarginLeft);
            indicatorMarginTop = typedArray.getDimension(R.styleable.CarouselPager_indicatorMarginTop, indicatorMarginTop);
            indicatorMarginRight = typedArray.getDimension(R.styleable.CarouselPager_indicatorMarginRight, indicatorMarginRight);
            indicatorMarginBottom = typedArray.getDimension(R.styleable.CarouselPager_indicatorMarginBottom, indicatorMarginBottom);
            itemCount = typedArray.getInt(R.styleable.CarouselPager_itemCount, itemCount);
            listItem = typedArray.getResourceId(R.styleable.CarouselPager_listItem, listItem);
            typedArray.recycle();
        }
        onCreateIndicatorsLayout();
        initDefAdapter(context);
    }

    /**
     * 创建指示器布局
     */
    protected void onCreateIndicatorsLayout() {
        //图片
        LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pager.setLayoutParams(pagerParams);
        addView(pager);
        //指示器容器
        LayoutParams indicatorLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorLayoutParams.gravity = indicatorGravity;
        if (indicatorLayoutMargin != 0) {
            indicatorLayoutParams.leftMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.topMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.rightMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.bottomMargin = (int) indicatorLayoutMargin;
        } else {
            indicatorLayoutParams.leftMargin = (int) indicatorLayoutMarginLeft;
            indicatorLayoutParams.topMargin = (int) indicatorLayoutMarginTop;
            indicatorLayoutParams.rightMargin = (int) indicatorLayoutMarginRight;
            indicatorLayoutParams.bottomMargin = (int) indicatorLayoutMarginBottom;
        }
        indicatorLayout.setLayoutParams(indicatorLayoutParams);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(indicatorLayout);
    }

    /**
     * 初始化默认适配器
     *
     * @param context  上下文
     */
    protected void initDefAdapter(Context context) {
        if (itemCount == 0) {
            return;
        }
        DefAdapter adapter = new DefAdapter(context);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            list.add("");
        }
        adapter.setItems(list);
        setAdapter(adapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private float downX, downY, moveX, moveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = moveY = 0F;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += event.getX() - downX;
                moveY += event.getY() - downY;
                if (Math.abs(moveY) - Math.abs(moveX) > 0) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = moveY = 0F;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += event.getX() - downX;
                moveY += event.getY() - downY;
                if (Math.abs(moveY) - Math.abs(moveX) > 0) {
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置数据适配器
     *
     * @param adapter
     */
    public void setAdapter(CarouselAdapter adapter) {
        this.adapter = adapter;
        adapter.setOnDataSetChangeListener(this);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(adapter);
        updateIndicator();
    }

    /**
     * 更新指示器
     */
    protected void updateIndicator() {
        addIndicatorItems();
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setCurrentItem(adapter.isCycle() ? 1 : 0);
        if (isAutoPlay()) {
            play();
        }
    }

    @Override
    public void onDataSetChanged(CarouselAdapter adapter) {
        setAdapter(adapter);
    }

    /**
     * 设置指示器显示
     *
     * @param visibility
     */
    public void setIndicatorVisibility(int visibility) {
        if (indicatorLayout != null) {
            indicatorLayout.setVisibility(visibility);
        }
    }

    /**
     * 添加轮播改变监听
     *
     * @param onCarouselChangeListener
     */
    public void addOnCarouselChangeListener(OnCarouselChangeListener onCarouselChangeListener) {
        this.onCarouselChangeListener = onCarouselChangeListener;
    }

    public interface OnCarouselChangeListener {
        /**
         * 该方法会在当前页面滚动时被调用，或者作为一部分
         * 以编程方式启动的平滑滚动或用户启动的触摸滚动。
         *
         * @param position             当前显示的第一页的位置索引。
         *                             如果 positionOffset 不为零，则页面位置+1 将可见。
         * @param positionOffset       来自 [0, 1) 的值，表示从页面位置的偏移量。
         * @param positionOffsetPixels 以像素为单位的值，表示位置的偏移量。
         */
        void onCarouselScrolled(int position, float positionOffset, int positionOffsetPixels);

        /**
         * 选择新页面时，将调用此方法。 动画不是必须完整。
         *
         * @param position 新选择页面的位置索引。
         */
        void onCarouselSelected(int position);

        /**
         * 当滚动状态改变时调用。 有助于发现用户何时
         * 开始拖动，当寻呼机自动稳定到当前页面时，
         * 或当它完全停止/空闲时。
         *
         * @param state 新的滚动状态。
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         */
        void onCarouselScrollStateChanged(int state);
    }


    /**
     * @return 是否循环
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * 设置是否循环
     * @param cycle 是否循环
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * 滚动位置
     */
    private long scrolledPosition = 1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isCycle()) {
            if (pager.getCurrentItem() == 0) {
                setCurrentIndicator(adapter.getCount() - 2);
            }
            if (pager.getCurrentItem() == adapter.getCount() - 1) {
                setCurrentIndicator(1);
            }
        }
        //轮播滚动选中监听
        if (onCarouselChangeListener != null) {
            if (scrolledPosition != position) {
                scrolledPosition = position;
                if (isCycle()) {
                    if (position == adapter.getCount() - 1) {
                        position = adapter.getCount() - 3;
                    }
                    if (position > 0 && position < adapter.getCount() - 1) {
                        position -= 1;
                    }
                }
                onCarouselChangeListener.onCarouselScrolled(position, positionOffset, positionOffsetPixels);
            }
        }
    }


    @Override
    public void onPageSelected(int position) {
        setCurrentIndicator(position);
        if (!isCycle() && onCarouselChangeListener != null) {
            onCarouselChangeListener.onCarouselSelected(position);
        }
    }

    /**
     * 状态改变位置
     */
    private long changedPosition = 1;

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {

        }
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            stop();
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (isAutoPlay()) {
                play();
            }
            if (isCycle()) {
                if (pager.getCurrentItem() == 0) {
                    pager.setCurrentItem(adapter.getCount() - 2, false);
                }
                if (pager.getCurrentItem() == adapter.getCount() - 1) {
                    pager.setCurrentItem(1, false);
                }
            }
            //循环选择，选中监听
            if (isCycle() && onCarouselChangeListener != null) {
                int position = pager.getCurrentItem();
                if (changedPosition != position) {
                    changedPosition = position;
                    if (position == adapter.getCount() - 1) {
                        position = adapter.getCount() - 3;
                    }
                    if (position > 0 && position < adapter.getCount() - 1) {
                        position -= 1;
                    }
                    onCarouselChangeListener.onCarouselSelected(position);
                }
            }
        }
        if (onCarouselChangeListener != null) {
            onCarouselChangeListener.onCarouselScrollStateChanged(state);
        }
    }

    /**
     * 添加指示器Item
     */
    private void addIndicatorItems() {
        if (adapter == null || indicatorLayout == null) {
            return;
        }
        int childCount = indicatorLayout.getChildCount();
        int adapterCount = adapter.getCount();
        if (childCount != 0) {
            clearIndicator();
        }
        for (int i = 0; i < adapterCount; i++) {
            addIndicator();
        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            if (isCycle()) {
                ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
                if (i == 0 || i == indicatorLayout.getChildCount() - 1) {
                    indicator.setVisibility(GONE);
                } else {
                    indicator.setVisibility(View.VISIBLE);
                }
            }
        }
        setCurrentIndicator(isCycle() ? 1 : 0);
    }

    /**
     * 清空指示器
     */
    protected void clearIndicator() {
        int childCount = indicatorLayout.getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            indicatorLayout.removeAllViews();
        }
    }

    /**
     * 添加指示器
     */
    protected void addIndicator() {
        LinearLayout.LayoutParams indicatorImageParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (indicatorMargin != 0) {
            indicatorImageParams.setMargins((int) indicatorMargin, (int) indicatorMargin, (int) indicatorMargin, (int) indicatorMargin);
        } else {
            indicatorImageParams.setMargins((int) indicatorMarginLeft, (int) indicatorMarginTop, (int) indicatorMarginRight, (int) indicatorMarginBottom);
        }
        ImageView indicator = new ImageView(context);
        indicator.setLayoutParams(indicatorImageParams);
        //添加指示器到容器
        indicatorLayout.addView(indicator, indicatorImageParams);
    }

    /**
     * 设置当前指示器位置
     *
     * @param position 指示器位置
     */
    private void setCurrentIndicator(int position) {
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (indicator != null) {
                if (i == position) {
                    indicator.setImageResource(indicatorSelectedResource);
                } else {
                    indicator.setImageResource(indicatorUnSelectedResource);
                }
            }
        }
    }

    /**
     * 设置位置
     * 需要注意的是在setAdapter之后设置位置才行
     *
     * @param position
     */
    public void setPosition(int position) {
        if (adapter == null) {
            new Exception("The setPosition() method should be after the setAdapter() method.").printStackTrace();
            return;
        }
        if (pager == null) {
            new NullPointerException("Carousel pager is null,you can't set position.").printStackTrace();
            return;
        }
        if (pager != null) {
            pager.setCurrentItem(isCycle() ? position + 1 : position);
        }
    }

    /**
     * 播放跳转
     */
    public void play() {
        if (messenger != null && !messenger.hasMessages(1)) {
            messenger.sendEmptyMessageDelayed(1, duration);
        }
    }

    /**
     * 停止跳转
     */
    public void stop() {
        if (messenger != null) {
            messenger.removeMessages(1);
        }
    }

    /**
     * 销毁 - 防止内容泄露
     */
    public void destroy() {
        if (messenger != null) {
            messenger.removeCallbacksAndMessages(null);
            messenger = null;
        }
    }

    /**
     * 设置页面转变动画 - 同ViewPager
     *
     * @param reverseDrawingOrder
     * @param transformer
     */
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        pager.setPageTransformer(reverseDrawingOrder, transformer);
    }

    /**
     * 设置页面转变动画 - 同ViewPager
     *
     * @param reverseDrawingOrder
     * @param transformer
     * @param pageLayerType
     */
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer, int pageLayerType) {
        pager.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType);
    }

    /**
     * 设置选中的指示器的图片
     *
     * @param indicatorSelectedResource
     */
    public void setSelectedIndicatorResource(int indicatorSelectedResource) {
        this.indicatorSelectedResource = indicatorSelectedResource;
        updateIndicator();
    }

    /**
     * 设置未选中的指示器的图片
     *
     * @param indicatorUnSelectedResource
     */
    public void setUnIndicatorSelectedResource(int indicatorUnSelectedResource) {
        this.indicatorUnSelectedResource = indicatorUnSelectedResource;
        updateIndicator();
    }

    /**
     * 设置指示器布局的外间距
     *
     * @param indicatorLayoutMargin
     */
    public void setIndicatorLayoutMargin(float indicatorLayoutMargin) {
        this.indicatorLayoutMargin = dpToPx(indicatorLayoutMargin);
        updateIndicator();
    }

    /**
     * 设置指示器布局的左边间距
     *
     * @param indicatorLayoutMarginLeft
     */
    public void setIndicatorLayoutMarginLeft(float indicatorLayoutMarginLeft) {
        this.indicatorLayoutMarginLeft = dpToPx(indicatorLayoutMarginLeft);
        updateIndicator();
    }

    /**
     * 设置指示器布局的上边间距
     *
     * @param indicatorLayoutMarginTop
     */
    public void setIndicatorLayoutMarginTop(float indicatorLayoutMarginTop) {
        this.indicatorLayoutMarginTop = dpToPx(indicatorLayoutMarginTop);
        updateIndicator();
    }

    /**
     * 设置指示器布局的右边间距
     *
     * @param indicatorLayoutMarginRight
     */
    public void setIndicatorLayoutMarginRight(float indicatorLayoutMarginRight) {
        this.indicatorLayoutMarginRight = dpToPx(indicatorLayoutMarginRight);
        updateIndicator();
    }

    /**
     * 设置指示器布局的下边间距
     *
     * @param indicatorLayoutMarginBottom
     */
    public void setIndicatorLayoutMarginBottom(float indicatorLayoutMarginBottom) {
        this.indicatorLayoutMarginBottom = dpToPx(indicatorLayoutMarginBottom);
        updateIndicator();
    }

    /**
     * 设置指示器之间的间距
     *
     * @param indicatorMargin
     */
    public void setIndicatorMargin(float indicatorMargin) {
        this.indicatorMargin = dpToPx(indicatorMargin);
        updateIndicator();
    }

    /**
     * 设置指示器之间的间距
     *
     * @param indicatorMarginLeft
     */
    public void setIndicatorMarginLeft(float indicatorMarginLeft) {
        this.indicatorMarginLeft = dpToPx(indicatorMarginLeft);
        updateIndicator();
    }

    /**
     * 设置指示器之间的上间距
     *
     * @param indicatorMarginTop
     */
    public void setIndicatorMarginTop(float indicatorMarginTop) {
        this.indicatorMarginTop = indicatorMarginTop;
        updateIndicator();
    }

    /**
     * 设置指示器之间的右间距
     *
     * @param indicatorMarginRight
     */
    public void setIndicatorMarginRight(float indicatorMarginRight) {
        this.indicatorMarginRight = indicatorMarginRight;
        updateIndicator();
    }

    /**
     * 设置指示器之间的下间距
     *
     * @param indicatorMarginBottom
     */
    public void setIndicatorMarginBottom(float indicatorMarginBottom) {
        this.indicatorMarginBottom = indicatorMarginBottom;
        updateIndicator();
    }

    /**
     * 设置指示器的位置
     *
     * @param gravity
     */
    public void setIndicatorGravity(int gravity) {
        this.indicatorGravity = gravity;
        updateIndicator();
    }

    /**
     * 是否自动播放
     *
     * @return
     */
    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    /**
     * 设置自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        updateIndicator();
    }

    /**
     * 获取Pager对象
     *
     * @return
     */
    public ViewPager getPager() {
        return pager;
    }

    /**
     * 获取数据适配器
     *
     * @return
     */
    public CarouselAdapter getAdapter() {
        return adapter;
    }

    /**
     * 设置轮播时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @param dp  密度
     * @return 英寸
     */
    public static float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    /**
     * @return 屏幕密度
     */
    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 默认适配器
     */
    private class DefAdapter extends CarouselAdapter<String> {

        public DefAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutResId() {
            return listItem;
        }

        @Override
        public void onItemBindViewHolder(ViewHolder holder, int position) {

        }
    }

}
