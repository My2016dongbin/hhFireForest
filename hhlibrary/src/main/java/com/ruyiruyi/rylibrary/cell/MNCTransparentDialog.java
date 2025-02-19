package com.ruyiruyi.rylibrary.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.ruyiruyi.rylibrary.R;


public class MNCTransparentDialog extends Dialog {
    public MNCTransparentDialog(Context context) {
        super(context, R.style.myTransparentDialog);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}