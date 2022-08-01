package androidx.widget;

import android.os.Handler;
import android.os.Message;

/**
 * 播放控制器
 */
public class CarouselMessenger extends Handler {

    private CarouselPager carousel;

    public CarouselMessenger(CarouselPager carousel) {
        this.carousel = carousel;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int index = carousel.getPager().getCurrentItem() + 1;
        carousel.getPager().setCurrentItem(index);
        carousel.play();
    }

}