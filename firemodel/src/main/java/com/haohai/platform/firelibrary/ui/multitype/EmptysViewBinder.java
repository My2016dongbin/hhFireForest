package com.haohai.platform.firelibrary.ui.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haohai.platform.firelibrary.R;

import me.drakeet.multitype.ItemViewProvider;

public class EmptysViewBinder extends ItemViewProvider<Emptys, EmptysViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_emptys, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Emptys empty) {
        holder.emptyView.setText(empty.getStr());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView emptyView;

        ViewHolder(View itemView) {
            super(itemView);
            emptyView = ((TextView) itemView.findViewById(R.id.empty_view));
        }
    }
}
