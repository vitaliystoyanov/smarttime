package com.stoyanov.developer.apptracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.presenters.ApplicationsUsedPresenter;
import com.stoyanov.developer.apptracker.views.ApplicationUsedViewHolder;

public class ApplicationsUsedAdapter extends MvpRecyclerListAdapter<ApplicationUsed,
        ApplicationsUsedPresenter, ApplicationUsedViewHolder> {

    public ApplicationsUsedAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected ApplicationsUsedPresenter createPresenter(@NonNull ApplicationUsed model) {
        ApplicationsUsedPresenter presenter = new ApplicationsUsedPresenter();
        presenter.setModel(model);
        return presenter;
    }

    @NonNull
    @Override
    protected Object getModelId(@NonNull ApplicationUsed model) {
        return model.getId();
    }

    @Override
    public ApplicationUsedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ApplicationUsedViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_used_application, parent, false), parent.getContext());
    }
}
