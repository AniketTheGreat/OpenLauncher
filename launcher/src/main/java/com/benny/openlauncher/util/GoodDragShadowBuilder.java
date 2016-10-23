package com.benny.openlauncher.util;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.widget.AppItemView;

public class GoodDragShadowBuilder extends View.DragShadowBuilder {
    int x;
    int y;

    public GoodDragShadowBuilder(View view) {
        super(view);
        this.x = Home.touchX;
        this.y = Home.touchY;
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        shadowTouchPoint.set(x, y);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        getView().draw(canvas);
    }
}
