package com.szip.sportwatch.Interface;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.View.PullToRefreshLayout;


public class MyListener implements PullToRefreshLayout.OnRefreshListener
{

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
	{
		if (WearableManager.getInstance().getConnectState()==WearableManager.STATE_CONNECTED) {
			EXCDController.getInstance().writeForCheckVersion();
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
