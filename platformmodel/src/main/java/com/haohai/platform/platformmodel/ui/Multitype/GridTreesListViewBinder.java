package com.haohai.platform.platformmodel.ui.Multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.model.GridTrees;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class GridTreesListViewBinder extends ItemViewProvider<GridTrees, GridTreesListViewBinder.ViewHolder> {
    private String status;
    public OnGridTreesItemClick listener;

    public void setListener(OnGridTreesItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_gridtrees, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final GridTrees gridTrees) {
        Log.e("binder", "onBindViewHolder: bingo gridNew binder" + gridTrees );
        holder.tv_gridtrees.setText(gridTrees.getName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_gridtrees;

        ViewHolder(View itemView) {
            super(itemView);
            tv_gridtrees = ((TextView) itemView.findViewById(R.id.tv_gridtrees));
        }

    }
    public interface OnGridTreesItemClick{
        void onGridTreesItemClickListener(String id);
    }

}
