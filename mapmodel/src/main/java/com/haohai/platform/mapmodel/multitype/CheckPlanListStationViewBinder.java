package com.haohai.platform.mapmodel.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class CheckPlanListStationViewBinder extends ItemViewProvider<CheckPlanList.PlanResourceDTOSFirejd, CheckPlanListStationViewBinder.ViewHolder> {
    public OnOneBodyItemClick listener;

    public void setListener(OnOneBodyItemClick listener) {
        this.listener = listener;
    }
    public String resourceType;
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_check_plan_list_station, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final CheckPlanList.PlanResourceDTOSFirejd planResourceDTOSFirejd) {
        switch (planResourceDTOSFirejd.getResourceType()){
            case "checkStation":
                resourceType="护林检查站";
                break;
            case "helicopterPoint":
                resourceType = "直升机机降点";
                break;
            case "team":
                resourceType = "队伍驻防点";
                break;
            case "dangerSource":
                resourceType = "危险源";
                break;
            case "materialRepository":
                resourceType = "物资储备库";
                break;
            case "waterSource":
                resourceType = "水源地";
                break;
            case "cemetery":
                resourceType = "墓地";
                break;
            case "watchTower":
                resourceType = "瞭望塔";
                break;
            case "monitor":
                resourceType = "视频监控点";
                break;
            case "fireCommand":
                resourceType = "森林防火监测中心";
                break;
        }
        holder.textView1.setText(planResourceDTOSFirejd.getName());
        holder.textView2.setText(resourceType);
        if (planResourceDTOSFirejd.getStatus()==1){
            holder.checkView.setVisibility(View.VISIBLE);
            holder.checkGoneView.setVisibility(View.GONE);
            holder.shenheView.setVisibility(View.GONE);
            holder.checkPassView.setVisibility(View.GONE);
        }else if (planResourceDTOSFirejd.getStatus()==2){
            holder.checkView.setVisibility(View.GONE);
            holder.checkGoneView.setVisibility(View.GONE);
            holder.shenheView.setVisibility(View.VISIBLE);
            holder.checkPassView.setVisibility(View.GONE);
        }else if (planResourceDTOSFirejd.getStatus()==3){
            holder.checkView.setVisibility(View.GONE);
            holder.checkGoneView.setVisibility(View.VISIBLE);
            holder.shenheView.setVisibility(View.GONE);
            holder.checkPassView.setVisibility(View.GONE);
        }else if (planResourceDTOSFirejd.getStatus()==4){
            holder.checkView.setVisibility(View.GONE);
            holder.checkGoneView.setVisibility(View.GONE);
            holder.shenheView.setVisibility(View.GONE);
            holder.checkPassView.setVisibility(View.VISIBLE);
        }

        if(planResourceDTOSFirejd.getStatus()==3){
            Log.e("TAG", "onBindViewHolder: planResourceDTOSFirejd = " + planResourceDTOSFirejd.toString()  );
            holder.textView3.setVisibility(View.VISIBLE);
            holder.textView3.setText("整治截止时间:" + new CommonUtils().parseDate((planResourceDTOSFirejd.getEndTime()!=null?planResourceDTOSFirejd.getEndTime():"")));
        }else{
            holder.textView3.setVisibility(View.GONE);
            holder.textView3.setText("");
        }

        RxViewAction.clickNoDouble(holder.checkView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onChecklistItemClickListener(planResourceDTOSFirejd,"jiancha");
                    }
                });
        RxViewAction.clickNoDouble(holder.shenheView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onChecklistItemClickListener(planResourceDTOSFirejd,"shenhe");
                    }
                });
        RxViewAction.clickNoDouble(holder.checkGoneView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onChecklistItemClickListener(planResourceDTOSFirejd,"zhengzhi");
                    }
                });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private TextView checkView;
        private TextView checkGoneView;
        private TextView shenheView;
        private TextView checkPassView;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.text_view1));
            textView2 = ((TextView) itemView.findViewById(R.id.text_view2));
            textView3 = ((TextView) itemView.findViewById(R.id.text_view3));
            checkView = itemView.findViewById(R.id.check_view);
            checkGoneView= itemView.findViewById(R.id.check_gone_view);
            shenheView= itemView.findViewById(R.id.shenhe_view);
            checkPassView= itemView.findViewById(R.id.check_pass_view);
        }
    }
    public interface OnOneBodyItemClick{
        void onChecklistItemClickListener(CheckPlanList.PlanResourceDTOSFirejd planResourceDTOSFirejd,String type);
    }

}
