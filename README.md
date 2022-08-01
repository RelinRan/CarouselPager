# CarouselPager
广告轮播、Adapter + View模式、可循环轮播  
# 预览  
![效果](./ic_preview.png)
# 资源
|名字|资源| 
|-|-|
|AAR|[carousel_pager.aar](https://github.com/RelinRan/CarouselPager/blob/master/carousel_pager_2022.8.1.1.aar)|
|GitHub |[CarouselPager](https://github.com/RelinRan/CarouselPager)|
|Gitee|[CarouselPager](https://gitee.com/relin/CarouselPager)|
# Maven
1.build.grade | setting.grade
```
repositories {
	...
	maven { url 'https://jitpack.io' }
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:CarouselPager:2022.8.1.1'
}
```
# xml
~~~
<androidx.widget.CarouselPager
    android:id="@+id/carousel"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:autoPlay="true"
    app:cycle="true"
    app:indicatorSelected="@drawable/ui_core_carousel_indicator_selected"
    app:indicatorUnSelected="@drawable/ui_core_carousel_indicator_unselected"
    app:listItem="@layout/ui_core_carousel_item"
    app:itemCount="3" />
~~~
# attrs.xml
~~~
<declare-styleable name="CarouselPager">
<!--指示器选中资源-->
<attr name="indicatorSelected" format="reference" />
<!--指示器未选中资源-->
<attr name="indicatorUnSelected" format="reference" />
<!--是否自动播放-->
<attr name="autoPlay" format="boolean" />
<!--是否循环，对应Adapter必须设置相同参数-->
<attr name="cycle" format="boolean" />
<!--动画时长-->
<attr name="duration" />
<!--指示器位置-->
<attr name="indicatorGravity">
    <flag name="left" value="3" />
    <flag name="top" value="48" />
    <flag name="right" value="5" />
    <flag name="bottom" value="80" />
    <flag name="center" value="17" />
    <flag name="center_horizontal" value="1" />
    <flag name="center_vertical" value="16" />
</attr>
<!--指示器间距-->
<attr name="indicatorLayoutMargin" format="dimension" />
<!--指示器左边间距-->
<attr name="indicatorLayoutMarginLeft" format="dimension" />
<!--指示器上边间距-->
<attr name="indicatorLayoutMarginTop" format="dimension" />
<!--指示器右边间距-->
<attr name="indicatorLayoutMarginRight" format="dimension" />
<!--指示器下边间距-->
<attr name="indicatorLayoutMarginBottom" format="dimension" />
<!--指示器相互之间间距-->
<attr name="indicatorMargin" format="dimension" />
<!--指示器相互之间左边间距-->
<attr name="indicatorMarginLeft" format="dimension" />
<!--指示器相互之间上边间距-->
<attr name="indicatorMarginTop" format="dimension" />
<!--指示器相互之间右边间距-->
<attr name="indicatorMarginRight" format="dimension" />
<!--指示器相互之间底部间距-->
<attr name="indicatorMarginBottom" format="dimension" />
<!--轮播个数-->
<attr name="itemCount" format="integer"/>
<!--轮播item布局资源-->
<attr name="listItem" format="reference"/>
</declare-styleable>
~~~
# dimen.xml
~~~
<!--指示器圆点宽度-->
<dimen name="carousel_indicator_width">10dp</dimen>
<!--指示器圆点高度-->
<dimen name="carousel_indicator_height">10dp</dimen>
~~~
# color.xml
~~~
<!--指示器选中颜色-->
<color name="carousel_indicator_selected_color">#3DDB86</color>
<!--指示未选中颜色-->
<color name="carousel_indicator_unselected_color">#EBE7ED</color>
<!--默认轮播item背景颜色-->
<color name="carousel_item_background_color">#F2F2F2</color>
~~~
# Adapter
~~~
import android.content.Context;
import androidx.widget.carousel.CarouselAdapter;
import androidx.widget.ViewHolder;
import java.util.ArrayList;
import java.util.List;

public class CarouselImageAdapter extends CarouselAdapter<String> {

    public CarouselImageAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutResId() {
        return R.layout.item_image_carousel;
    }

    @Override
    public void onItemBindViewHolder(ViewHolder holder, int position) {
        holder.addItemClick(holder.itemView);
        ImageView iv_image = holder.find(R.id.iv_image);
        iv_image.setImageResource(getItem(position).getResId());
    }

}
~~~
# 使用
~~~

CarouselPager carousel = findViewById(R.id.carousel);
//xml属性值必须同步设置是否循环
carousel.setCycle(true);
CarouselImageAdapter adapter = new CarouselImageAdapter(this);
carousel.setAdapter(adapter);
//点击事件
adapter.setOnItemClickListener((apt, v, position) -> {

});
//设置数据源
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 3; i++) {
    list.add(R.mipmap.ui_core_carousel_background);
}
adapter.setItems(list);
~~~
