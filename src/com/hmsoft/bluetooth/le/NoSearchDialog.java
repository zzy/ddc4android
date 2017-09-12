package com.hmsoft.bluetooth.le;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class NoSearchDialog extends AlertDialog {

	public NoSearchDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("提示");
    	dialog.setMessage("为搜索到相关蓝牙设备，请打开设备蓝牙");
//    	dialog.setCancelable(false);
    	dialog.setPositiveButton("确认", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}

}
