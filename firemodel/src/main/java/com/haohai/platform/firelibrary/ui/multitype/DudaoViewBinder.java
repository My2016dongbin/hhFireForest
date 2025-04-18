package com.haohai.platform.firelibrary.ui.multitype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haohai.platform.firelibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CircleImageView;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/12/2.
 */
public class DudaoViewBinder extends ItemViewProvider<DuDao, DudaoViewBinder.ViewHolder> {

    public OnDuDaoItemClick listener;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(OnDuDaoItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_dudao, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull DuDao duDao) {
        holder.title.setText(duDao.getSupervisoryContent());
        holder.date.setText(new CommonUtils().parseDate(duDao.getSupervisoryStartTime()));
        Glide.with(context).load(duDao.getSupervisoryImg()).placeholder(R.drawable.dd_img).into(holder.image);
        //任务状态status，0未开始，1进行中，2已结束，3已延期
        if("0".equals(duDao.getStatus())){
            holder.state.setText("未开始");
            holder.state.setBackground(context.getResources().getDrawable(R.drawable.dd_start));
            holder.state.setTextColor(context.getResources().getColor(R.color.dd_start));
        }else if("1".equals(duDao.getStatus())){
            holder.state.setText("进行中");
            holder.state.setBackground(context.getResources().getDrawable(R.drawable.dd_ing));
            holder.state.setTextColor(context.getResources().getColor(R.color.dd_ing));
        }else if("2".equals(duDao.getStatus())){
            holder.state.setText("已结束");
            holder.state.setBackground(context.getResources().getDrawable(R.drawable.dd_end));
            holder.state.setTextColor(context.getResources().getColor(R.color.dd_end));
        }else if("3".equals(duDao.getStatus())){
            holder.state.setText("已延期");
            holder.state.setBackground(context.getResources().getDrawable(R.drawable.dd_delay));
            holder.state.setTextColor(context.getResources().getColor(R.color.dd_delay));
        }else{
            holder.state.setText("即将延期");
            holder.state.setBackground(context.getResources().getDrawable(R.drawable.dd_soon));
            holder.state.setTextColor(context.getResources().getColor(R.color.dd_soon));
        }
        RxViewAction.clickNoDouble(holder.layout).subscribe(unused -> {
            listener.onDuDaoItemClickListener(duDao);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView date;
        private final ImageView image;
        private final TextView state;
        private final FrameLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            title = ((TextView) itemView.findViewById(R.id.dd_title));
            date = ((TextView) itemView.findViewById(R.id.dd_date));
            image = ((CircleImageView) itemView.findViewById(R.id.dd_image));
            state = ((TextView) itemView.findViewById(R.id.dd_state));
            layout = ((FrameLayout) itemView.findViewById(R.id.dd_layout));
        }
    }

    public interface OnDuDaoItemClick{
        void onDuDaoItemClickListener(DuDao DuDao);
    }
}
