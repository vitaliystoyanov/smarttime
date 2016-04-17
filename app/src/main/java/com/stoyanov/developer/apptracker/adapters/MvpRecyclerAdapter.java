package com.stoyanov.developer.apptracker.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.stoyanov.developer.apptracker.presenters.BasePresenter;

import java.util.HashMap;
import java.util.Map;

public abstract class MvpRecyclerAdapter<M, P extends BasePresenter, VH extends MvpViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final int ANIMATED_ITEMS_COUNT = 20;
    private int lastAnimatedPosition = -1;

    protected final Map<Object, P> presenters;
    private Context context;

    public MvpRecyclerAdapter(Context context) {
        presenters = new HashMap<>();
        this.context = context;
    }

    @NonNull
    protected P getPresenter(@NonNull M model) {
        return presenters.get(getModelId(model));
    }

    @NonNull
    protected abstract P createPresenter(@NonNull M model);

    @NonNull
    protected abstract Object getModelId(@NonNull M model);

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);

        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    private void runAnimation(View view, int position) { // FIXME: 4/11/2016
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }

    }

    public static int getScreenHeight(Context context) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(size);
        return size.y;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        //runAnimation(holder.itemView, position);
        holder.bindPresenter(getPresenter(getItem(position)));
    }

    protected abstract M getItem(int position);
}
