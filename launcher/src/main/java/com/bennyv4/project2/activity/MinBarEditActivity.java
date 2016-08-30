package com.bennyv4.project2.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bennyv4.project2.R;
import com.bennyv4.project2.util.LauncherAction;
import com.bennyv4.project2.util.LauncherSettings;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MinBarEditActivity extends AppCompatActivity{
    RecyclerView recyclerView;
    boolean edited = false;
    private MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_minbaredit);
        setSupportActionBar((Toolbar) findViewById(R.id.tb));

        int i = 0;
        ArrayList<Item> mItems = new ArrayList<>();
        final ArrayList<String> minBarArrangement = LauncherSettings.getInstance(this).generalSettings.minBarArrangement;
        for (String act : minBarArrangement) {
            LauncherAction.ActionItem item = LauncherAction.getActionItemFromString(act);
            mItems.add(new Item(i,item));
            i++;
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.drawable.list_divider_h), true));

        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(mItems);
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(adapter));

        dragMgr.attachRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        if(edited)
            setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        LauncherSettings.getInstance(this).generalSettings.minBarArrangement.clear();
        for (Item item : adapter.mItems)
            LauncherSettings.getInstance(this).generalSettings.minBarArrangement.add(item.item.label.toString());
        super.onPause();
    }

    private static class Item {
        public final long id;
        public final LauncherAction.ActionItem item;

        public Item(long id, LauncherAction.ActionItem item) {
            this.id = id;
            this.item = item;
        }
    }

    private static class MyViewHolder extends AbstractDraggableItemViewHolder {
        TextView tv;
        TextView tv2;
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            tv2 = (TextView) itemView.findViewById(R.id.tv2);
            iv = (ImageView) itemView.findViewById(R.id.iv);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements DraggableItemAdapter<MyViewHolder> {
        List<Item> mItems;

        public MyAdapter(List<Item> items) {
            setHasStableIds(true); // this is required for D&D feature.

            mItems = items;
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).id; // need to return stable (= not change even after reordered) value
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minbaredit, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Item item = mItems.get(position);
            holder.tv.setText(item.item.label.toString());
            holder.tv2.setText(item.item.des);
            holder.iv.setImageResource(item.item.icon);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            MinBarEditActivity.this.edited = true;
            Item movedItem = mItems.remove(fromPosition);
            mItems.add(toPosition, movedItem);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }
    }
}
