package com.haohai.platform.mapmodel.multitype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.bean.UploadPost;
import com.haohai.platform.mapmodel.model.WeixingModel;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class UploadPostViewBinder extends ItemViewProvider<UploadPost, UploadPostViewBinder.ViewHolder> {
    public UploadPostViewBinder.OnPicClick listener;
    private Context context;

    public void setListener(UploadPostViewBinder.OnPicClick listener,Context context) {
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_upload_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull UploadPost uploadPost) {
        holder.tv_name.setText(uploadPost.getTaskType());
        holder.tv_latlng.setText(new CommonUtil().parseStrLength(uploadPost.getPosition().getLng()+"",10) + "," + new CommonUtil().parseStrLength(uploadPost.getPosition().getLat()+"",10));
        holder.tv_address.setText(uploadPost.getTaskContent());
        holder.tv_user.setText(uploadPost.getMemberName());
        holder.tv_time.setText(new CommonUtil().parseStrLength(uploadPost.getCreateTime(),19).replace("T"," "));
        holder.tv_info.setText(uploadPost.getDescription());

        holder.ll_pic.removeAllViews();
        String[] images = uploadPost.getTaskImg().split(",");
        for (int i = 0; i < images.length; i++) {
            String image = images[i];
            View picView = LayoutInflater.from(context).inflate(R.layout.pic_list_item, null);
            ImageView iv_show = picView.findViewById(R.id.iv_show);
            if(!image.contains("http")){
                image = "http://" + image;
            }
            Glide.with(context).load(image).into(iv_show);
            String finalImage = image;
            RxViewAction.clickNoDouble(iv_show).subscribe(unused -> {
                listener.OnPicClickListener(finalImage);
            });
            holder.ll_pic.addView(picView);
        }
        if(uploadPost.getReserve()!=null && !uploadPost.getReserve().isEmpty()){
            View videoView = LayoutInflater.from(context).inflate(R.layout.pic_list_item, null);
            ImageView iv_show = videoView.findViewById(R.id.iv_show);
            Glide.with(context).load(R.drawable.file_mp4).into(iv_show);
            RxViewAction.clickNoDouble(iv_show).subscribe(unused -> {
                listener.OnVideoClickListener(uploadPost.getReserve());
            });
            holder.ll_pic.addView(videoView);
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_name;
        private final TextView tv_latlng;
        private final TextView tv_address;
        private final TextView tv_user;
        private final TextView tv_time;
        private final TextView tv_info;
        private final LinearLayout ll_pic;

        ViewHolder(View itemView) {
            super(itemView);
            tv_name = ((TextView) itemView.findViewById(R.id.tv_name));
            tv_latlng = ((TextView) itemView.findViewById(R.id.tv_latlng));
            tv_address = ((TextView) itemView.findViewById(R.id.tv_address));
            tv_user = ((TextView) itemView.findViewById(R.id.tv_user));
            tv_time = ((TextView) itemView.findViewById(R.id.tv_time));
            tv_info = ((TextView) itemView.findViewById(R.id.tv_info));
            ll_pic = ((LinearLayout) itemView.findViewById(R.id.ll_pic));
        }
    }

    public interface OnPicClick{
        void OnPicClickListener(String picUrl);
        void OnVideoClickListener(String videoUrl);
    }
}
