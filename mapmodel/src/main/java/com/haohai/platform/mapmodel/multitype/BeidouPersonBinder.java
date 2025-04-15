package com.haohai.platform.mapmodel.multitype;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.BeidouPerson;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


public class BeidouPersonBinder extends ItemViewProvider<BeidouPerson, BeidouPersonBinder.ViewHolder> {

    public OnBeiDouItemClickListener listener;

    public void setListener(OnBeiDouItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_grid_userslist, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final BeidouPerson beidouPerson) {

        holder.tv_name.setText(beidouPerson.getName());
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = format.format(date);
        try{
            if(formatDate.contains(beidouPerson.getLastTime().substring(0,10))){
                holder.tv_time.setText(beidouPerson.getLastTime().substring(11,19));
            }else{
                holder.tv_time.setText(beidouPerson.getLastTime());
            }
        }catch (Exception e){
        }
        holder.tv_content.setText(beidouPerson.getLastNews());
        holder.tv_unread.setText(beidouPerson.getUnRead()+"");
        if(beidouPerson.getUnRead()==0){
            holder.tv_unread.setVisibility(View.GONE);
        }else{
            holder.tv_unread.setVisibility(View.VISIBLE);
        }
        RxViewAction.clickNoDouble(holder.fl_item).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                listener.OnBeiDouItemClick(beidouPerson);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_user;
        private final FrameLayout fl_item;
        private final TextView tv_name;
        private final TextView tv_time;
        private final TextView tv_content;
        private final TextView tv_unread;

        ViewHolder(View itemView) {
            super(itemView);
            fl_item = itemView.findViewById(R.id.fl_item);
            iv_user = itemView.findViewById(R.id.iv_user);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_unread = itemView.findViewById(R.id.tv_unread);
        }
    }

    public interface OnBeiDouItemClickListener{
        void OnBeiDouItemClick(BeidouPerson beidouPerson);
    }
}
