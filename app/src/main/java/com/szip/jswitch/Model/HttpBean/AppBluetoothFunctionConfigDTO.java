package com.szip.jswitch.Model.HttpBean;

import java.io.Serializable;

/**
 * Date: 2020-07-31 18:04:23
 *
 * @author JoeChou
 */

public class AppBluetoothFunctionConfigDTO implements Serializable {

	/**
	 * 是否支持天气，1支持，0不支持
	 */
	private boolean weather;

	/**
	 * 是否支持蓝牙通话，1支持，0不支持
	 */
	private boolean bluetoothCall;

	/**
	 * 是否支持蓝牙音乐，1支持，0不支持
	 */
	private boolean bluetoothMusic;

	public boolean getWeather() {
		return this.weather;
	}

	public void setWeather(boolean weather) {
		this.weather = weather;
	}

	public boolean getBluetoothCall() {
		return this.bluetoothCall;
	}

	public void setBluetoothCall(boolean bluetoothCall) {
		this.bluetoothCall = bluetoothCall;
	}

	public boolean getBluetoothMusic() {
		return this.bluetoothMusic;
	}

	public void setBluetoothMusic(boolean bluetoothMusic) {
		this.bluetoothMusic = bluetoothMusic;
	}

}
