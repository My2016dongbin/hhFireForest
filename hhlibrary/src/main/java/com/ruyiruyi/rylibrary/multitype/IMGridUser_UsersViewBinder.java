package com.ruyiruyi.rylibrary.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.model.IMGridUsers;
import com.ruyiruyi.rylibrary.utils.CircleImageView;

import me.drakeet.multitype.ItemViewProvider;


public class IMGridUser_UsersViewBinder extends ItemViewProvider<IMGridUsers.Users, IMGridUser_UsersViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_im_grid_user, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull IMGridUsers.Users empty) {
        holder.tv_names.setText(empty.getFullName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView iv_header;
        private final TextView tv_names;

        ViewHolder(View itemView) {
            super(itemView);
            tv_names = ((TextView) itemView.findViewById(R.id.tv_names));
            iv_header = ((CircleImageView) itemView.findViewById(R.id.iv_header));
        }
    }
}
