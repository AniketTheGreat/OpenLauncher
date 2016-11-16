package com.benny.openlauncher.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.util.LauncherSettings;
import com.benny.openlauncher.util.Tool;

import io.codetail.widget.RevealFrameLayout;

import static com.benny.openlauncher.widget.AppDrawer.DrawerMode.Grid;

/**
 * Created by BennyKok on 11/5/2016.
 */

public class AppDrawer extends RevealFrameLayout implements TextWatcher{

    private PagedAppDrawer drawerViewPaged;
    private GridAppDrawer drawerViewGrid;
    private DrawerMode drawerMode;
    private EditText searchBar;
    private CallBack openCallBack,closeCallBack;

    private Animator appDrawerAnimator;

    public AppDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AppDrawer(Context context) {
        super(context);

        init();
    }

    public void setCallBack(CallBack openCallBack,CallBack closeCallBack){
        this.openCallBack = openCallBack;
        this.closeCallBack = closeCallBack;
    }

    public void open(int cx,int cy,int finalRadius){
        appDrawerAnimator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, 0, finalRadius);
        appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        appDrawerAnimator.setDuration(180);
        appDrawerAnimator.setStartDelay(100L);
        openCallBack.onStart();
        appDrawerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator p1) {
                getChildAt(0).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator p1) {
                openCallBack.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator p1) {
            }

            @Override
            public void onAnimationRepeat(Animator p1) {
            }
        });

        appDrawerAnimator.start();
    }

    public void close(int cx,int cy,int finalRadius){
        if (appDrawerAnimator == null || appDrawerAnimator.isRunning())
            return;

        appDrawerAnimator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, finalRadius, 0);
        appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        appDrawerAnimator.setDuration(180);
        appDrawerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator p1) {
                closeCallBack.onStart();
            }

            @Override
            public void onAnimationEnd(Animator p1) {
                closeCallBack.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator p1) {
            }

            @Override
            public void onAnimationRepeat(Animator p1) {
            }
        });

        appDrawerAnimator.start();
    }

    public AppDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        drawerMode = LauncherSettings.getInstance(getContext()).generalSettings.drawerMode;
        switch (drawerMode) {
            case Paged:
                drawerViewPaged = (PagedAppDrawer) layoutInflater.inflate(R.layout.view_pageddrawer, this, false);
//                if (LauncherSettings.getInstance(getContext()).generalSettings.appDrawerSearchbar)
//                    ((LayoutParams) drawerViewPaged.getLayoutParams()).topMargin += Tool.dp2px(70, getContext());
                addView(drawerViewPaged);
                addView(layoutInflater.inflate(R.layout.view_drawerindicator, this, false));
                break;
            case Grid:
                drawerViewGrid = (GridAppDrawer) layoutInflater.inflate(R.layout.view_griddrawer, this, false);
                if (LauncherSettings.getInstance(getContext()).generalSettings.appDrawerSearchbar)
                    ((LayoutParams) drawerViewGrid.getLayoutParams()).topMargin += Tool.dp2px(60, getContext());
                addView(drawerViewGrid);
                break;
        }
        if (LauncherSettings.getInstance(getContext()).generalSettings.appDrawerSearchbar && drawerMode == Grid) {
            CardView cv = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.view_searchbar_app, this, false);
            addView(cv);
            searchBar = (EditText) cv.findViewById(R.id.et);
            searchBar.clearFocus();
            searchBar.addTextChangedListener(this);
        }
    }

    public void scrollToStart() {
        switch (drawerMode) {
            case Paged:
                drawerViewPaged.setCurrentItem(0, false);
                break;
            case Grid:
                drawerViewGrid.rv.scrollToPosition(0);
                break;
        }
    }

    public void setHome(Home home) {
        switch (drawerMode) {
            case Paged:
                drawerViewPaged.withHome(home, (PagerIndicator) findViewById(R.id.appDrawerIndicator));
                break;
            case Grid:
                break;
        }
    }


    public static void startStylePicker(final Context context) {
        final String[] items = new String[DrawerMode.values().length];
        for (int i = 0; i < DrawerMode.values().length; i++) {
            items[i] = DrawerMode.values()[i].name();
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("App drawer style")
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        LauncherSettings.getInstance(context).generalSettings.drawerMode = DrawerMode.valueOf(items[position]);
                    }
                }).show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        switch (drawerMode) {
            case Paged:
                break;
            case Grid:
                drawerViewGrid.fa.filter(charSequence);
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public enum DrawerMode {
        Paged, Grid
    }

    public interface CallBack{
        void onStart();
        void onEnd();
    }
}
