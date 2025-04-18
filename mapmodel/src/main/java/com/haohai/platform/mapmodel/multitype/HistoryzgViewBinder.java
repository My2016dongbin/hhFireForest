package com.haohai.platform.mapmodel.multitype;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;

import org.w3c.dom.Text;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/2/6.
 */
public class HistoryzgViewBinder extends ItemViewProvider<Historyzg, HistoryzgViewBinder.ViewHolder> {
    public Context context;
    public OnChooseImageClickListener listener;
    public HistoryzgViewBinder(Context context) {
        this.context = context;
    }

    public void setListener(OnChooseImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_history_zg, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final Historyzg historyzg) {
        holder.beizhuView.setText("审核意见："+historyzg.getDescription());
        holder.beizhuName.setText("第"+historyzg.getCishu()+"整治");
        holder.zzrText.setText("整治人："+historyzg.getUserName());
        holder.zzEdit.setText("整改意见:"+historyzg.getRegulation());
        holder.shrText.setText("审核人："+historyzg.getCreateUser());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView beizhuView;
        private final TextView beizhuName;
        private final TextView zzrText;
        private final TextView zzEdit;
        private final TextView shrText;

        ViewHolder(View itemView) {
            super(itemView);
            beizhuView = itemView.findViewById(R.id.beizhu_edit);
            beizhuName=itemView.findViewById(R.id.beizhu_name);
            zzrText=itemView.findViewById(R.id.zzr_text);
            zzEdit=itemView.findViewById(R.id.zz_edit);
            shrText=itemView.findViewById(R.id.shr_text);
        }
    }
    public interface OnChooseImageClickListener {
        void onZZImageClick(String imgurl);
    }
}
