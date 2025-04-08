package com.ruyiruyi.rylibrary.model;

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

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;


public class IMOnlineUsersBinder extends ItemViewProvider<IMOnlineUsers, IMOnlineUsersBinder.ViewHolder> {

    public OnBeiDouItemClickListener listener;
    public Context context;

    public void setListener(OnBeiDouItemClickListener listener) {
        this.listener = listener;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_beidou_gridlist, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final IMOnlineUsers imGridUsers) {

        holder.tv_name.setText(imGridUsers.getName());
        RxViewAction.clickNoDouble(holder.fl_grid).subscribe(unused -> {
            imGridUsers.setExpand(!imGridUsers.isExpand());
            if(imGridUsers.isExpand()){
                holder.ll_users.setVisibility(View.VISIBLE);
                holder.iv_down.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_downs));
            }else{
                holder.ll_users.setVisibility(View.GONE);
                holder.iv_down.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_ups));
            }
        });
        if(imGridUsers.isExpand()){
            holder.ll_users.setVisibility(View.VISIBLE);
            holder.iv_down.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_downs));
        }else{
            holder.ll_users.setVisibility(View.GONE);
            holder.iv_down.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_ups));
        }
        RxViewAction.clickNoDouble(holder.iv_status).subscribe(unused -> {
            imGridUsers.setStatus(!imGridUsers.isStatus());
            if(imGridUsers.isStatus()){
                holder.iv_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_se));
            }else{
                holder.iv_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_uns));
            }
            drawUsers(holder,imGridUsers,false);
            listener.OnBeiDouGridSelect(imGridUsers.getUserList(),imGridUsers.isStatus());
        });
        if(imGridUsers.isStatus()){
            holder.iv_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_se));
        }else{
            holder.iv_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_uns));
        }

        drawUsers(holder,imGridUsers,true);

    }

    private void drawUsers(ViewHolder holder, IMOnlineUsers imGridUsers,boolean auto) {
        if(imGridUsers.getUserList()!=null){
            holder.ll_users.removeAllViews();
            for (int i = 0; i < imGridUsers.getUserList().size(); i++) {
                IMOnlineUsers.Users users = imGridUsers.getUserList().get(i);
                View item = LayoutInflater.from(context).inflate(R.layout.item_grid_userslist,null);
                ImageView item_status = item.findViewById(R.id.item_status);
                LinearLayout ll_items = item.findViewById(R.id.ll_items);
                CircleImageView iv_user = item.findViewById(R.id.iv_user);
                TextView tv_name = item.findViewById(R.id.tv_name);
                TextView tv_content = item.findViewById(R.id.tv_content);
                TextView tv_unread = item.findViewById(R.id.tv_unread);
                if(users.isStatus()){
                    item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_se));
                }else{
                    item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_uns));
                }
                //父级网格点击事件
                if(!auto){
                    if(imGridUsers.isStatus()){
                        item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_se));
                    }else{
                        item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_uns));
                    }
                }
                RxViewAction.clickNoDouble(/*item_status*/ll_items).subscribe(unused -> {
                    users.setStatus(!users.isStatus());
                    if(users.isStatus()){
                        item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_se));
                    }else{
                        item_status.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_uns));
                    }
                    listener.OnBeiDouUsersSelect(users,users.isStatus());
                });
                tv_name.setText(users.getFullName());
                tv_content.setText(users.getId());
                holder.ll_users.addView(item);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout fl_grid;
        private final ImageView iv_status;
        private final TextView tv_name;
        private final LinearLayout ll_users;
        private final ImageView iv_down;

        ViewHolder(View itemView) {
            super(itemView);
            fl_grid = itemView.findViewById(R.id.fl_grid);
            iv_status = itemView.findViewById(R.id.iv_status);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_users = itemView.findViewById(R.id.ll_users);
            iv_down = itemView.findViewById(R.id.iv_down);
        }
    }

    public interface OnBeiDouItemClickListener{
        void OnBeiDouItemClick(IMOnlineUsers imGridUsers);
        void OnBeiDouGridSelect(List<IMOnlineUsers.Users> list, boolean status);
        void OnBeiDouUsersSelect(IMOnlineUsers.Users users,boolean status);
    }
}
