package com.haohai.platform.firelibrary.ui.multitype;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haohai.platform.firelibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class ProtectStationListBinder extends ItemViewProvider<ProtectStationList, ProtectStationListBinder.ViewHolder> {

    public OnProtectListItemClick listener;

    public void setListener(OnProtectListItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_protectstation_list, parent, false);
        return new ViewHolder(root);
    }

    //userType 用户类型: 1:检查人 2:整治人3：综合整治人 4 : 任务发布人
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ProtectStationList protectStationList) {
        holder.tv_title.setText(protectStationList.getTitle().replace("null",""));
        holder.tv_area.setText(protectStationList.getArea().replace("null",""));
        if(protectStationList.getStatus() == 2 && protectStationList.getUserType()==1){
            holder.tv_status.setText("待审核");
            holder.tv_status.setTextColor(Color.parseColor("#00BFFF"));
            holder.btn_shenhe.setVisibility(View.VISIBLE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
        } else if(protectStationList.getStatus() == 3 && (protectStationList.getUserType()==2||protectStationList.getUserType()==3)){
            Log.e("TAG", "onBindViewHolder: qc" +  protectStationList.getTitle() + " ， " + protectStationList.getUserType());
            holder.tv_status.setText("待整治");
            holder.tv_status.setTextColor(Color.parseColor("#FF0000"));
            holder.btn_zhengzhi.setVisibility(View.VISIBLE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
        }else if(protectStationList.getStatus() == 1 && protectStationList.getUserType()==1){
            holder.tv_status.setText("待检查");
            holder.btn_check.setVisibility(View.VISIBLE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }else if(protectStationList.getStatus() == 4){//已通过 4
            holder.tv_status.setText("已通过");
            holder.btn_pass.setVisibility(View.VISIBLE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }else{
            if(protectStationList.getStatus() == 1){
                holder.tv_status.setText("待检查");
            }else if(protectStationList.getStatus() == 2){
                holder.tv_status.setText("待审核");
                holder.tv_status.setTextColor(Color.parseColor("#00BFFF"));
            }else if(protectStationList.getStatus() == 3){
                holder.tv_status.setText("待整治");
                holder.tv_status.setTextColor(Color.parseColor("#FF0000"));
            }else if(protectStationList.getStatus() == 4){
                holder.tv_status.setText("已通过");
            }else if(protectStationList.getStatus() == 0){
                holder.tv_status.setText("未开始");
            }else{
                holder.tv_status.setText("数据异常");
            }
            holder.btn_pass.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }
        //检查计划不能在此操作
        if(protectStationList.getCheckTypeNet().equals("1") || protectStationList.getCheckTypeNet().equals("0")){
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
        }
        holder.tv_checkdate.setText("检查时间:"+ new CommonUtils().parseDate(protectStationList.getCheckDate()));
        holder.tv_worker.setText("执行人:" + protectStationList.getWorker().replace("null",""));
        holder.tv_enddate.setText("任务截止:"+new CommonUtils().parseDate(protectStationList.getEndDate()));
        holder.tv_remark.setText(protectStationList.getRemark().replace("null",""));

        RxViewAction.clickNoDouble(holder.btn_check)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onCheckClickListener(protectStationList);
                    }
                });
        RxViewAction.clickNoDouble(holder.btn_zhengzhi)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onZhengzhiClickListener(protectStationList);
                    }
                });
        RxViewAction.clickNoDouble(holder.btn_shenhe)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onShenheClickListener(protectStationList);
                    }
                });
        RxViewAction.clickNoDouble(holder.ll_item)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onProtectListItemClickListener(protectStationList);
                    }
                });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_title;
        private final TextView tv_area;
        private final TextView tv_status;
        private final TextView tv_checkdate;
        private final TextView tv_worker;
        private final TextView tv_enddate;
        private final TextView tv_remark;
        private final Button btn_check;
        private final Button btn_zhengzhi;
        private final Button btn_shenhe;
        private final Button btn_pass;
        private final LinearLayout ll_item;

        ViewHolder(View itemView) {
            super(itemView);
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
            tv_area = ((TextView) itemView.findViewById(R.id.tv_area));
            tv_status = ((TextView) itemView.findViewById(R.id.tv_status));
            tv_checkdate = ((TextView) itemView.findViewById(R.id.tv_checkdate));
            tv_worker = ((TextView) itemView.findViewById(R.id.tv_worker));
            tv_enddate = ((TextView) itemView.findViewById(R.id.tv_enddate));
            tv_remark = ((TextView) itemView.findViewById(R.id.tv_remark));
            btn_check = ((Button) itemView.findViewById(R.id.btn_check));
            btn_zhengzhi = ((Button) itemView.findViewById(R.id.btn_zhengzhi));
            btn_shenhe = ((Button) itemView.findViewById(R.id.btn_shenhe));
            btn_pass = ((Button) itemView.findViewById(R.id.btn_pass));
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
        }
    }

    public interface OnProtectListItemClick{
        void onProtectListItemClickListener(ProtectStationList protectStationList);
        void onCheckClickListener(ProtectStationList protectStationList);
        void onZhengzhiClickListener(ProtectStationList protectStationList);
        void onShenheClickListener(ProtectStationList protectStationList);
    }
}
