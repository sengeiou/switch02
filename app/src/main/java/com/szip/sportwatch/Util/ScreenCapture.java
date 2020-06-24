package com.szip.sportwatch.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 截屏
 * 
 * */
public class ScreenCapture {
	/**
	 *  指定区域截屏
	 * @param Activity activity,int x,int y,int width,int height
	 * @return filePath 文件路径
	 * */
	public static String  getBitmap(Activity activity,View layout){
		//SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
		//设置系统UI元素的可见性
		layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		//启用或禁用绘图缓存
		layout.setDrawingCacheEnabled(true);
		//创建绘图缓存
		layout.buildDrawingCache();
		//拿到绘图缓存
		Bitmap bitmap = layout.getDrawingCache();
		bitmap = Bitmap.createBitmap(bitmap, 0,MathUitl.dipToPx(60,activity),layout.getWidth(),layout.getHeight()-MathUitl.dipToPx(60,activity));
		String filePath = null;
		try {
			// 获取内置SD卡路径

			String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iSmarport";
			File fileDir = new File(sdCardPath);
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}
			// 图片文件路径
			filePath= sdCardPath+"/"+getCurrentTime()+".jpg";
			File file = new File(filePath);
			FileOutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
		} catch (Exception e) {
		}
		layout.destroyDrawingCache();
		layout.setSystemUiVisibility(View.VISIBLE);
		return filePath;
	}
	/**
	 *获取当前时间
	 * @return
	 */
	private static  String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new java.util.Date());
	}
}
