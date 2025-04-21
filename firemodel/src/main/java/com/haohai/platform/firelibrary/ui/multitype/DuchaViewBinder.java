package com.haohai.platform.firelibrary.ui.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haohai.platform.firelibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/12/2.
 */
public class DuchaViewBinder extends ItemViewProvider<Ducha, DuchaViewBinder.ViewHolder> {

    public OnDuchaItemClick listener;

    public void setListener(OnDuchaItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_ducha, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Ducha ducha) {
        holder.textView1.setText("监控点名称: " + ducha.getName() );
        holder.textView2.setText("发现时间: " + ducha.getDiscover() );
        holder.textView3.setText("经度、纬度: " + ducha.getLatitude()+","+ducha.getLongitude());
        holder.textView4.setText("地址: " + ducha.getAddress());

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private final TextView textView4;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.text_view1));
            textView2 = ((TextView) itemView.findViewById(R.id.text_view2));
            textView3 = ((TextView) itemView.findViewById(R.id.text_view3));
            textView4 = ((TextView) itemView.findViewById(R.id.text_view4));
        }
    }

    public interface OnDuchaItemClick{
        void onDuchaItemClickListener(Ducha ducha);
    }
}
