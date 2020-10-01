package com.szip.sportwatch.Model.HttpBean;

import java.io.Serializable;

/**
 * Date: 2020-07-31 18:05:06
 *
 * @author JoeChou
 */
public class AppGpsFunctionConfigDTO implements Serializable {

	/**
	 * 是否支持定位，1支持，0不支持
	 */
	private boolean location;

	/**
	 * 是否支持轨迹，1支持，0不支持
	 */
	private boolean track;

	/**
	 * 是否支持脱困，1支持，0不支持
	 */
	private boolean rescue;

	public boolean getLocation() {
		return this.location;
	}

	public void setLocation(boolean location) {
		this.location = location;
	}

	public boolean getTrack() {
		return this.track;
	}

	public void setTrack(boolean track) {
		this.track = track;
	}

	public boolean getRescue() {
		return this.rescue;
	}

	public void setRescue(boolean rescue) {
		this.rescue = rescue;
	}

}
