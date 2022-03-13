package com.szip.jswitch.Model.HttpBean;

import java.io.Serializable;

/**
 * Date: 2020-07-31 18:03:26
 *
 * @author JoeChou
 */

public class AppHealthMonitorFunctionConfigDTO implements Serializable {

	/**
	 * 是否支持心率，1支持，0不支持
	 */
	private boolean heartRate;

	/**
	 * 是否支持心电，1支持，0不支持
	 */
	private boolean ecg;

	/**
	 * 是否支持血氧，1支持，0不支持
	 */
	private boolean bloodOxygen;

	/**
	 * 是否支持血压，1支持，0不支持
	 */
	private boolean bloodPressure;

	/**
	 * 是否支持计步，1支持，0不支持
	 */
	private boolean stepCounter;

	/**
	 * 是否支持测温，1支持，0不支持
	 */
	private boolean temperature;

	/**
	 * 是否支持睡眠监测，1支持，0支持
	 */
	private boolean sleep;

	public boolean getHeartRate() {
		return this.heartRate;
	}

	public void setHeartRate(boolean heartRate) {
		this.heartRate = heartRate;
	}

	public boolean getEcg() {
		return this.ecg;
	}

	public void setEcg(boolean ecg) {
		this.ecg = ecg;
	}

	public boolean getBloodOxygen() {
		return this.bloodOxygen;
	}

	public void setBloodOxygen(boolean bloodOxygen) {
		this.bloodOxygen = bloodOxygen;
	}

	public boolean getBloodPressure() {
		return this.bloodPressure;
	}

	public void setBloodPressure(boolean bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public boolean getStepCounter() {
		return this.stepCounter;
	}

	public void setStepCounter(boolean stepCounter) {
		this.stepCounter = stepCounter;
	}

	public boolean getTemperature() {
		return this.temperature;
	}

	public void setTemperature(boolean temperature) {
		this.temperature = temperature;
	}

	public boolean getSleep() {
		return this.sleep;
	}

	public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}

}
