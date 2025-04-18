package com.haohai.platform.mapmodel.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class CheckPlanListViewBinder extends ItemViewProvider<CheckPlanList, CheckPlanListViewBinder.ViewHolder> {
    private String status;
    public OnPlanItemClick listener;

    public void setListener(OnPlanItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_check_plan_list, parent, false);
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

        holder.textView4.setText("开始时间: " + new CommonUtils().parseDate(checkPlanList.getStartTime()));
        holder.textView4mid.setText("结束时间: " + new CommonUtils().parseDate(checkPlanList.getEndTime()));

        holder.textView5.setText("检查人: "+checkPlanList.getCheckUserName());
        holder.textView6.setText("描述: " + checkPlanList.getDescription());
        RxViewAction.clickNoDouble(holder.planView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onPlanItemClickListener(checkPlanList.getId());
                    }
                });
        if(checkPlanList.type == 2 && checkPlanList.planResourceDTOS.size()==0){
            holder.order_choose.setVisibility(View.VISIBLE);
            RxViewAction.clickNoDouble(holder.order_choose).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    listener.onOrderCheckClickListener(checkPlanList);
                }
            });
        }else{
            holder.order_choose.setVisibility(View.GONE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private final TextView textView4;
        private final TextView textView4mid;
        private final TextView textView5;
        private final TextView textView6;
        private final LinearLayout planView;
        private final FrameLayout order_choose;
        private final TextView order_check;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.plan_name));
            textView2 = ((TextView) itemView.findViewById(R.id.jiedao_text));
            textView3 = ((TextView) itemView.findViewById(R.id.zhuangtai_text));
            textView4 = ((TextView) itemView.findViewById(R.id.shijian_text));
            textView4mid = ((TextView) itemView.findViewById(R.id.shijian2_text));
            textView5 = ((TextView) itemView.findViewById(R.id.jcr_text));
            textView6 = ((TextView) itemView.findViewById(R.id.beizhu_text));
            planView=  itemView.findViewById(R.id.plan_view);
            order_choose=  itemView.findViewById(R.id.order_choose);
            order_check=  itemView.findViewById(R.id.order_check);
        }

    }
    public interface OnPlanItemClick{
        void onPlanItemClickListener(String id);
        void onOrderCheckClickListener(CheckPlanList checkPlanList);
    }

}
