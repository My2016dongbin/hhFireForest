package com.haohai.platform.platformmodel.ui.Multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.model.Person;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;

public class PersonViewBinder extends ItemViewProvider<Person, PersonViewBinder.ViewHolder> {
    public OnPersonClick listener;

    public void setListener(OnPersonClick listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_person, parent, false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Person person) {
        RxViewAction.clickNoDouble(holder.ll_item).subscribe(unused -> {
            listener.onPersonClickListener(person);
        });
        holder.tv_title.setText(person.getFullName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ll_item;
        private final TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
        }
    }

    public interface OnPersonClick{
        void onPersonClickListener(Person person);
    }
}
