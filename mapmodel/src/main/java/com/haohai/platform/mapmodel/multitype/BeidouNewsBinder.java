package com.haohai.platform.mapmodel.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.BeidouNews;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


public class BeidouNewsBinder extends ItemViewProvider<BeidouNews, BeidouNewsBinder.ViewHolder> {

    public OnBeiDouNewsClickListener listener;
    public Context context;

    public void setListener(OnBeiDouNewsClickListener listener) {
        this.listener = listener;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_beidou_newslist, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final BeidouNews beidouNews) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = format.format(date);
        try{
            if(formatDate.contains(beidouNews.getSendTime().substring(0,10))){
                holder.tv_time.setText(beidouNews.getSendTime().substring(11,19));
            }else{
                holder.tv_time.setText(beidouNews.getSendTime());
            }
        }catch (Exception e){
        }
        if(beidouNews.getSend() == 0){
            //0 接收
            holder.ll_send.setVisibility(View.GONE);
            holder.fl_receive.setVisibility(View.VISIBLE);
            holder.tv_receive_content.setText(beidouNews.getContent());
            if(beidouNews.getLocation()==null||beidouNews.getLocation().isEmpty()){
                holder.ll_location.setVisibility(View.GONE);
            }else{
                holder.ll_location.setVisibility(View.VISIBLE);
                holder.tv_receive_location.setText(beidouNews.getLocation());
            }
            holder.fl_receive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.OnBeiDouNewsLongClick(beidouNews);
                    return true;
                }
            });
        }else{
            //1 发送
            holder.fl_receive.setVisibility(View.GONE);
            holder.ll_send.setVisibility(View.VISIBLE);
            holder.tv_send_content.setText(beidouNews.getContent());
            if(beidouNews.getState() == -1){
                //发送状态未回执
                holder.tv_send_state.setVisibility(View.GONE);
                holder.fl_state.setVisibility(View.VISIBLE);
                holder.iv_send_state.setImageDrawable(context.getDrawable(R.drawable.loadings));

            }else if(beidouNews.getState() == -2){
                //发送状态 失败
                holder.tv_send_state.setVisibility(View.GONE);
                holder.fl_state.setVisibility(View.VISIBLE);
                holder.iv_send_state.setImageDrawable(context.getDrawable(R.drawable.errors));

            }else{
                holder.fl_state.setVisibility(View.GONE);
                holder.tv_send_state.setVisibility(View.VISIBLE);
                holder.tv_send_state.setText(beidouNews.getState()==0?"已发送":"已送达");
            }
            holder.ll_send.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.OnBeiDouNewsLongClick(beidouNews);
                    return true;
                }
            });
        }
        RxViewAction.clickNoDouble(holder.ll_news).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                listener.OnBeiDouNewsClick(beidouNews);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ll_news;
        private final TextView tv_time;
        private final FrameLayout fl_receive;
        private final TextView tv_receive_content;
        private final TextView tv_receive_location;
        private final LinearLayout ll_send;
        private final TextView tv_send_content;
        private final TextView tv_send_state;
        private final FrameLayout fl_state;
        private final LinearLayout ll_location;
        private final ImageView iv_send_state;

        ViewHolder(View itemView) {
            super(itemView);
            ll_news = itemView.findViewById(R.id.ll_news);
            tv_time = itemView.findViewById(R.id.tv_time);
            fl_receive = itemView.findViewById(R.id.fl_receive);
            tv_receive_content = itemView.findViewById(R.id.tv_receive_content);
            tv_receive_location = itemView.findViewById(R.id.tv_receive_location);
            ll_send = itemView.findViewById(R.id.ll_send);
            tv_send_content = itemView.findViewById(R.id.tv_send_content);
            tv_send_state = itemView.findViewById(R.id.tv_send_state);
            fl_state = itemView.findViewById(R.id.fl_state);
            ll_location = itemView.findViewById(R.id.ll_location);
            iv_send_state = itemView.findViewById(R.id.iv_send_state);
        }
    }

    public interface OnBeiDouNewsClickListener{
        void OnBeiDouNewsClick(BeidouNews beidouNews);
        void OnBeiDouNewsLongClick(BeidouNews beidouNews);
    }
}
