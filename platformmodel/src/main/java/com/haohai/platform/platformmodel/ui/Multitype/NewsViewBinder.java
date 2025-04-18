package com.haohai.platform.platformmodel.ui.Multitype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.model.News;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class NewsViewBinder extends ItemViewProvider<News, NewsViewBinder.ViewHolder> {

    public OnNewsItemClick listener;
    public Context context;

    public void setListener(OnNewsItemClick listener,Context context) {
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_news, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull News news) {
        holder.tv_item.setText(news.getName());
        holder.tv_content.setText(news.getDescribes());
        String times = news.getCreateTime();
        try{
            times = times.replace("T"," ");
            times = times.substring(0,19);
        }catch (Exception e){

        }
        holder.tv_time.setText(times);
        holder.tv_file.setText(news.getRemark());

        RxViewAction.clickNoDouble(holder.ll_item).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                listener.OnNewsItemClickListener(news);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_item;
        private final TextView tv_item;
        private final TextView tv_time;
        private final TextView tv_file;
        private final TextView tv_content;
        private final LinearLayout ll_item;

        ViewHolder(View itemView) {
            super(itemView);
            iv_item = ((ImageView) itemView.findViewById(R.id.iv_item));
            tv_item = ((TextView) itemView.findViewById(R.id.tv_item));
            tv_time = ((TextView) itemView.findViewById(R.id.tv_time));
            tv_file = ((TextView) itemView.findViewById(R.id.tv_file));
            tv_content = ((TextView) itemView.findViewById(R.id.tv_content));
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
        }
    }
    public interface OnNewsItemClick{
        void OnNewsItemClickListener(News news);
    }
}
