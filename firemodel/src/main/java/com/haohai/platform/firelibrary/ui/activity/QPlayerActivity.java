package com.haohai.platform.firelibrary.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.haohai.ledge.videolibrary.GSYVideoManager;
import com.haohai.ledge.videolibrary.utils.OrientationUtils;
import com.haohai.platform.firelibrary.R;
import com.ruyiruyi.rylibrary.base.BaseActivity;

public class QPlayerActivity extends BaseActivity {


    private String player_url;
    private OrientationUtils orientationUtils;
    private String player_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_video);

        Intent intent = getIntent();
        player_url = intent.getStringExtra("PLAYER_URL");
        player_name = intent.getStringExtra("PLAYER_NAME");
        initView();
    }


    private void initView() {

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
