package com.haohai.platform.mapmodel.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/12/3.
 */
public class CompresiveListViewBinder extends ItemViewProvider<CheckPlanList, CompresiveListViewBinder.ViewHolder> {
    private String status;
    public OnPlanItemClick listener;

    public void setListener(OnPlanItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_compresive_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final CheckPlanList checkPlanList) {
        switch (checkPlanList.getStatus()){
            case 1:
                status="未开始";
                break;
            case 2:
                status="进行中";
                break;
            case 3:
                status="已结束";
                break;
        }
        holder.textView1.setText(checkPlanList.getName());
        holder.textView2.setText(checkPlanList.getGridName());
        holder.textView3.setText(status);
        if (checkPlanList.getEndTime()!=null) {
            holder.textView4.setText("截止时间:" + checkPlanList.getEndTime());
        }
        holder.textView5.setText("检查人:"+checkPlanList.getCheckUserName());
        holder.textView6.setText(checkPlanList.getDescription());
        holder.textView7.setText("检查时间:"+checkPlanList.getCreateTime().substring(0,checkPlanList.getCreateTime().indexOf("T")));
        RxViewAction.clickNoDouble(holder.planView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onPlanItemClickListener(checkPlanList.getId());
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private final TextView textView4;
        private final TextView textView5;
        private final TextView textView6;
        private final TextView textView7;
        private final LinearLayout planView;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.plan_name));
            textView2 = ((TextView) itemView.findViewById(R.id.jiedao_text));
            textView3 = ((TextView) itemView.findViewById(R.id.zhuangtai_text));
            textView4 = ((TextView) itemView.findViewById(R.id.shijian_text));
            textView5 = ((TextView) itemView.findViewById(R.id.jcr_text));
            textView6 = ((TextView) itemView.findViewById(R.id.beizhu_text));
            textView7= ((TextView) itemView.findViewById(R.id.create_time_text));
            planView=  itemView.findViewById(R.id.plan_view);
        }

    }
    public interface OnPlanItemClick{
        void onPlanItemClickListener(String id);
    }

}
