package com.haohai.platform.mapmodel.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.db.CheckField;

import me.drakeet.multitype.ItemViewProvider;


/**
 * Created by geyang on 2019/11/25.
 */
public class CheckDetailBinder extends ItemViewProvider<CheckDetail, CheckDetailBinder.ViewHolder> {

    public OnCheckFieldItemClick listener;

    public void setListener(OnCheckFieldItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_check_detail, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final CheckDetail checkDetail) {
        Log.e("TAG", "onBindViewHolder: "+checkDetail.isNow() );
        holder.nameView.setText(checkDetail.getStatusName());
        if (checkDetail.getStatus().equals("1")){
            holder.topLine.setVisibility(View.INVISIBLE);
            //holder.statusImg.setBackgroundResource(R.drawable.check_status_ing);
        }
        if (checkDetail.isNow()){
            if (checkDetail.getStatus().equals("4")){
                holder.statusImg.setBackgroundResource(R.drawable.check_status_no);
                holder.endline.setVisibility(View.INVISIBLE);
            }else {
                holder.statusImg.setBackgroundResource(R.drawable.check_status_ing);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final View topLine;
        private final View endline;
        private final View bottomLine;
        private final ImageView statusImg;
        ViewHolder(View itemView) {
            super(itemView);
            nameView = ((TextView) itemView.findViewById(R.id.check_field_name_view));
            topLine=  itemView.findViewById(R.id.top_line);
            endline=  itemView.findViewById(R.id.bottom_line);
            bottomLine=  itemView.findViewById(R.id.bottom_line);
            statusImg=itemView.findViewById(R.id.status_img);
        }
    }

    public interface OnCheckFieldItemClick{
        void onCheckFieldItemClickLinstener(String id, int state);
    }
}
