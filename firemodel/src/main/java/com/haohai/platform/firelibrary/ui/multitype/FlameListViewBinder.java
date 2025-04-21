package com.haohai.platform.firelibrary.ui.multitype;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.Pic1Activity;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class FlameListViewBinder extends ItemViewProvider<FlameList, FlameListViewBinder.ViewHolder> {
    public OnFlameListItemClick listener;
    private Context mContext;

    public FlameListViewBinder(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(OnFlameListItemClick listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_flame, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FlameList flameList) {
        RxViewAction.clickNoDouble(holder.tv_look).subscribe(unused -> listener.lookClick(flameList));
        RxViewAction.clickNoDouble(holder.tv_edit).subscribe(unused -> listener.editClick(flameList));
        RxViewAction.clickNoDouble(holder.tv_delete).subscribe(unused -> listener.deleteClick(flameList));
        holder.tv_title.setText(flameList.getName());
        holder.tv_person.setText(flameList.getLeaderName());
        holder.tv_phone.setText(flameList.getLeaderPhone());
        holder.tv_start.setText(flameList.getStartTime());
        holder.tv_end.setText(flameList.getEndTime());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_look;
        private final TextView tv_edit;
        private final TextView tv_delete;
        private final TextView tv_person;
        private final TextView tv_phone;
        private final TextView tv_start;
        private final TextView tv_end;
        private final TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            tv_look = ((TextView) itemView.findViewById(R.id.tv_look));
            tv_edit = ((TextView) itemView.findViewById(R.id.tv_edit));
            tv_delete = ((TextView) itemView.findViewById(R.id.tv_delete));
            tv_person = ((TextView) itemView.findViewById(R.id.tv_person));
            tv_phone = ((TextView) itemView.findViewById(R.id.tv_phone));
            tv_start = ((TextView) itemView.findViewById(R.id.tv_start));
            tv_end = ((TextView) itemView.findViewById(R.id.tv_end));
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
        }
    }
    public interface OnFlameListItemClick{
        void lookClick(FlameList flameList);
        void editClick(FlameList flameList);
        void deleteClick(FlameList flameList);
    }
}
