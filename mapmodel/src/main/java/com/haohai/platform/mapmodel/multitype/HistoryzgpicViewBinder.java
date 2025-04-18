package com.haohai.platform.mapmodel.multitype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/2/6.
 */
public class HistoryzgpicViewBinder extends ItemViewProvider<Historyzgpic, HistoryzgpicViewBinder.ViewHolder> {
    public Context context;
    public OnChooseImageClickListener listener;
    public HistoryzgpicViewBinder(Context context) {
        this.context = context;
    }

    public void setListener(OnChooseImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_history_zgpic, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final Historyzgpic historyzgpic) {
        Glide.with(context).load(historyzgpic.getImgurl()).into(holder.imageView);
        RxViewAction.clickNoDouble(holder.imageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onZZImageClick(historyzgpic.getImgurl());
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView.findViewById(R.id.evaluate_item_image));
        }
    }
    public interface OnChooseImageClickListener {
        void onZZImageClick(String imgurl);
    }
}
