package com.ruyiruyi.rylibrary.cell;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by dongbin on 2022/3/3.
 * 用于聊天窗口父布局去除顶部padding
 */

public class ChatFrameLayout extends FrameLayout {
    /**
     * 首次打开时获取底部的padding，用于之后的计算
     */
    private int initializerPaddingBottom = -1;

    public ChatFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ChatFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChatFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (initializerPaddingBottom == -1 && insets.bottom >= 0) {
            //初始化 initializerPaddingBottom 底部padding值
            initializerPaddingBottom = insets.bottom;
        }
        insets.top = 0;
        insets.bottom = insets.bottom - initializerPaddingBottom;
        return super.fitSystemWindows(insets);
    }
}
