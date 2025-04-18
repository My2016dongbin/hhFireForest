package com.haohai.platform.mapmodel.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.bean.planResourceDTOS;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/12/3.
 */
public class CompresiveStationViewBinder extends ItemViewProvider<planResourceDTOS, CompresiveStationViewBinder.ViewHolder> {
    private String type;
    public OnOneBodyItemClick listener;

    public void setListener(OnOneBodyItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_compresive_station, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final planResourceDTOS planResourceDTOS ) {
        holder.textView1.setText(planResourceDTOS.getName());;
        RxViewAction.clickNoDouble(holder.checkView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onChecklistItemClickListener(planResourceDTOS);
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private TextView checkView;
        private TextView checkGoneView;
        private TextView shenheView;
        private TextView checkPassView;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.text_view1));
            textView2 = ((TextView) itemView.findViewById(R.id.text_view2));
            checkView = itemView.findViewById(R.id.check_view);
            checkGoneView= itemView.findViewById(R.id.check_gone_view);
            shenheView= itemView.findViewById(R.id.shenhe_view);
            checkPassView= itemView.findViewById(R.id.check_pass_view);
        }
    }
    public interface OnOneBodyItemClick{
        void onChecklistItemClickListener(planResourceDTOS planResourceDTOS);
    }

}
