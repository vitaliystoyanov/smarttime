package com.stoyanov.developer.apptracker.views;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stoyanov.developer.apptracker.adapters.MvpViewHolder;
import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.presenters.ApplicationsUsedPresenter;

public class ApplicationUsedViewHolder extends MvpViewHolder<ApplicationsUsedPresenter>
        implements ApplicationsUsedView {

    private static final String TAG = "AppViewHolder";

    private Context context;
    private ImageView appIcon;
    private TextView appName;
    private TextView time;

    public ApplicationUsedViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        appName = (TextView) itemView.findViewById(R.id.textview_name_used_app);
        time = (TextView) itemView.findViewById(R.id.textview_spend_time);
        appIcon = (ImageView) itemView.findViewById(R.id.imageview_app_icon);
    }

    @Override
    public void setApplicationName(String name) {
        setApplicationInfo(name);
    }

    public void setApplicationInfo(String packageName) {

        PackageManager manager = context.getPackageManager();
        appIcon.setImageDrawable(null);
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, 0);
            appIcon.setImageDrawable(manager.getApplicationIcon(applicationInfo));
            appName.setText(String.valueOf(manager.getApplicationLabel(applicationInfo)));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "setApplicationInfo: ", e);
            appName.setText("NameNotFound");
            appIcon.setImageResource(R.drawable.question);
        }

    }

    public void setTime(String time) {
        this.time.setText(time);
    }
}
