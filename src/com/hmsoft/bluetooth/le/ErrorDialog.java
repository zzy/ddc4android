package com.hmsoft.bluetooth.le;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog extends AlertDialog {

	public ErrorDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("提示");
    	dialog.setMessage("账号密码错误，请重新输入！");
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
