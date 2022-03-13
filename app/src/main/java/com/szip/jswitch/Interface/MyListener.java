package com.szip.jswitch.Interface;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediatek.wearable.WearableManager;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.View.PullToRefreshLayout;


public class MyListener implements PullToRefreshLayout.OnRefreshListener
{

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
	{
		if (MainService.getInstance().getState()==WearableManager.STATE_CONNECTED) {
			if (MyApplication.getInstance().isMtk())
				EXCDController.getInstance().writeForCheckVersion();
			else
				BleClient.getInstance().writeForGetDeviceState();
			// 下拉刷新操作
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					// 千万别忘了告诉控件刷新完毕了哦！
					pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
			}.sendEmptyMessageDelayed(0, 2000);
		}else {
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					// 千万别忘了告诉控件刷新完毕了哦！
					pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}.sendEmptyMessageDelayed(0, 2000);
		}

	}

}
