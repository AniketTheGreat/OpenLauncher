package com.benny.openlauncher.widget;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.R;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.DragAction;
import com.benny.openlauncher.util.GoodDragShadowBuilder;
import com.benny.openlauncher.util.GroupIconDrawable;
import com.benny.openlauncher.util.LauncherSettings;
import com.benny.openlauncher.util.Tool;

import java.util.UUID;

public class Dock extends CellContainer implements View.OnDragListener {

    public View previousItemView;
    public Desktop.Item previousItem;

    public Dock(Context c) {
        super(c);
        init();
    }

    public Dock(Context c, AttributeSet attr) {
        super(c, attr);
        init();
    }

    @Override
    public void init() {
        if (isInEditMode()) return;

        setGridSize(LauncherSettings.getInstance(getContext()).generalSettings.dockGridx, 1);
        setOnDragListener(this);

        super.init();
    }

    public void initDockItem() {
        removeAllViews();
        for (Desktop.Item item : LauncherSettings.getInstance(getContext()).dockData) {
            addAppToPosition(item);
        }
    }

    @Override
    public boolean onDrag(View p1, DragEvent p2) {
        switch (p2.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                switch (((DragAction) p2.getLocalState()).action) {
                    case ACTION_APP:
                    case ACTION_GROUP:
                    case ACTION_APP_DRAWER:
                        return true;
                }
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                return true;

            case DragEvent.ACTION_DROP:
                Intent intent = p2.getClipData().getItemAt(0).getIntent();
                intent.setExtrasClassLoader(Desktop.Item.class.getClassLoader());
                Desktop.Item item = intent.getParcelableExtra("mDragData");
                if (item.type == Desktop.Item.Type.APP || item.type == Desktop.Item.Type.GROUP) {
                    if (addAppToDock(item, (int) p2.getX(), (int) p2.getY())) {
                        Home.desktop.consumeRevert();
                        Home.dock.consumeRevert();
                    } else {
                        Home.dock.revertLastDraggedItem();
                        Home.desktop.revertLastDraggedItem();
                    }
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                return true;
        }
        return false;
    }

    public void consumeRevert() {
        previousItem = null;
        previousItemView = null;
    }

    public void revertLastDraggedItem() {
        if (previousItemView != null) {
            addViewToGrid(previousItemView);

            LauncherSettings.getInstance(getContext()).dockData.add(previousItem);

            previousItem = null;
            previousItemView = null;
        }
    }

    public void addAppToPosition(final Desktop.Item item) {
        View itemView = null;
        if (item.type == Desktop.Item.Type.APP)
            itemView = getAppItemView(item);
        else if (item.type == Desktop.Item.Type.GROUP)
            itemView = getGroupItemView(item);
        if (itemView == null) {
            LauncherSettings.getInstance(getContext()).dockData.remove(item);
        } else
            addViewToGrid(itemView, item.x, item.y, item.spanX, item.spanY);
    }

    public boolean addAppToDock(final Desktop.Item item, int x, int y) {
        CellContainer.LayoutParams positionToLayoutPrams = positionToLayoutPrams(x, y, item.spanX, item.spanY);
        if (positionToLayoutPrams != null) {

            //Add the item to settings
            item.x = positionToLayoutPrams.x;
            item.y = positionToLayoutPrams.y;
            LauncherSettings.getInstance(getContext()).dockData.add(item);
            //end

            View itemView = null;
            if (item.type == Desktop.Item.Type.APP)
                itemView = getAppItemView(item);
            else if (item.type == Desktop.Item.Type.GROUP)
                itemView = getGroupItemView(item);

            if (itemView != null) {
                itemView.setLayoutParams(positionToLayoutPrams);
                addView(itemView);
            }

            return true;
        } else {
            Toast.makeText(getContext(), R.string.toast_notenoughspace, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private View getAppItemView(final Desktop.Item item) {
        final AppManager.App app = AppManager.getInstance(getContext()).findApp(item.actions[0].getComponent().getPackageName(), item.actions[0].getComponent().getClassName());
        if (app == null) {
            return null;
        }
        AppItemView view = new AppItemView.Builder(getContext())
                .setAppItem(app)
                .withOnClickLaunchApp(app)
                .withOnTouchGetPosition()
                .setNoLabel()
                .vibrateWhenLongPress()
                .withOnLongClickDrag(item, DragAction.Action.ACTION_APP, new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //Remove the item from settings
                        LauncherSettings.getInstance(getContext()).dockData.remove(item);
                        //end

                        previousItemView = v;
                        previousItem = item;
                        removeView(v);
                        return false;
                    }
                })
                .setTextColor(Color.WHITE)
                .getView();


        view.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case ACTION_APP:
                            case ACTION_APP_DRAWER:
                                return true;
                        }
                        return false;
                    case DragEvent.ACTION_DROP:
                        Intent intent = dragEvent.getClipData().getItemAt(0).getIntent();
                        intent.setExtrasClassLoader(Desktop.Item.class.getClassLoader());
                        Desktop.Item dropitem = intent.getParcelableExtra("mDragData");
                        if (dropitem.type == Desktop.Item.Type.APP || dropitem.actions.length < GroupPopupView.GroupDef.maxItem) {
                            LauncherSettings.getInstance(getContext()).dockData.remove(item);
                            removeView(view);

                            item.addActions(dropitem.actions[0]);
                            item.name = "Unnamed";
                            item.type = Desktop.Item.Type.GROUP;
                            LauncherSettings.getInstance(getContext()).dockData.add(item);
                            addAppToPosition(item);

                            Home.desktop.consumeRevert();
                            Home.dock.consumeRevert();
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }
                return false;
            }
        });

        return view;
    }

    private View getGroupItemView(final Desktop.Item item) {
        final AppItemView view = new AppItemView.Builder(getContext())
                .withOnLongClickDrag(item, DragAction.Action.ACTION_GROUP, new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //Remove the item from settings
                        LauncherSettings.getInstance(getContext()).dockData.remove(item);
                        //end

                        previousItemView = v;
                        previousItem = item;
                        removeView(v);
                        return false;
                    }
                })
                .setNoLabel()
                .vibrateWhenLongPress()
                .setTextColor(Color.WHITE)
                .withOnTouchGetPosition()
                .getView();

        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        final float iconSize = Tool.convertDpToPixel(LauncherSettings.getInstance(getContext()).generalSettings.iconSize, getContext());
        AppManager.App[] apps = new AppManager.App[item.actions.length];
        for (int i = 0; i < item.actions.length; i++) {
            apps[i] = AppManager.getInstance(getContext()).findApp(item.actions[i].getComponent().getPackageName(), item.actions[i].getComponent().getClassName());
            if (apps[i] == null)
                return null;
        }
        final Bitmap[] icons = new Bitmap[4];
        for (int i = 0; i < 4; i++) {
            if (i < apps.length)
                icons[i] = Tool.drawableToBitmap(apps[i].icon);
            else
                icons[i] = Tool.drawableToBitmap(new ColorDrawable(Color.TRANSPARENT));
        }
        view.setIcon(new GroupIconDrawable(icons, iconSize),false);
        view.setLabel((item.name));

        view.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case ACTION_APP:
                            case ACTION_APP_DRAWER:
                                return true;
                        }
                        return false;
                    case DragEvent.ACTION_DROP:
                        Intent intent = dragEvent.getClipData().getItemAt(0).getIntent();
                        intent.setExtrasClassLoader(Desktop.Item.class.getClassLoader());
                        Desktop.Item dropitem = intent.getParcelableExtra("mDragData");
                        if (dropitem.type == Desktop.Item.Type.APP && item.actions.length < GroupPopupView.GroupDef.maxItem) {
                            LauncherSettings.getInstance(getContext()).dockData.remove(item);
                            removeView(view);

                            item.addActions(dropitem.actions[0]);
                            item.type = Desktop.Item.Type.GROUP;
                            LauncherSettings.getInstance(getContext()).dockData.add(item);
                            addAppToPosition(item);

                            Home.desktop.consumeRevert();
                            Home.dock.consumeRevert();
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }
                return false;
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Home.groupPopup.showWindowV(item, view, true)) {
                    ((GroupIconDrawable)view.getIcon()).popUp();
                }
            }
        });

        return view;
    }

}
