package com.haohai.platform.firelibrary.ui.activity.copy.other;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.firelibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class peopleViewBinder extends ItemViewProvider<people, peopleViewBinder.ViewHolder> {
    public OnPeopleLsitItemClick listener;

    public void setListener(OnPeopleLsitItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_peoplelist, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final people people) {
        holder.nameView.setText(people.getFullName());
        if (people.isCheck()){
            holder.checkView.setChecked(true);
        }else {
            holder.checkView.setChecked(false);
        }
        RxViewAction.clickNoDouble(holder.checkView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onPeopleListItemClickListener(people);
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkView;
        private final TextView nameView;
        private final LinearLayout resourceLayout;

        ViewHolder(View itemView) {
            super(itemView);
            checkView = ((CheckBox) itemView.findViewById(R.id.check_view));
            nameView = ((TextView) itemView.findViewById(R.id.tv_name));
            resourceLayout = ((LinearLayout) itemView.findViewById(R.id.resource_layout));
        }
    }
    public interface OnPeopleLsitItemClick{
        void onPeopleListItemClickListener(people people);

    }

}
