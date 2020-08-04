package com.szip.sportwatch.Model.HttpBean;

import java.io.Serializable;

/**
 * Date: 2020-07-31 18:03:57
 *
 * @author JoeChou
 */

public class AppMultiSportFunctionConfigDTO implements Serializable {

	/**
	 * 是否支持足球，1支持，0不支持
	 */
	private boolean football;

	/**
	 * 是否支持篮球，1支持，0不支持
	 */
	private boolean basketball;

	/**
	 * 是否支持乒乓球，1支持，0不支持
	 */
	private boolean pingpongball;

	/**
	 * 是否支持跑步，1支持，0不支持
	 */
	private boolean run;

	/**
	 * 是否支持骑行，1支持，0不支持
	 */
	private boolean cycling;

	/**
	 * 是否支持登山，1支持，0不支持
	 */
	private boolean climbing;

	/**
	 * 是否支持跑步机，1支持，0不支持
	 */
	private boolean treadmill;

	/**
	 * 是否支持徒步，1支持，0不支持
	 */
	private boolean hiking;

	/**
	 * 是否支持高尔夫，1支持，0不支持
	 */
	private boolean golf;

	/**
	 * 是否支持游泳，1支持，0不支持
	 */
	private boolean swim;

	/**
	 * 是否支持冲浪，1支持，0不支持
	 */
	private boolean surfing;

	/**
	 * 是否支持滑雪，1支持，0不支持
	 */
	private boolean skiing;

	/**
	 * 是否支持攀岩，1支持，0不支持
	 */
	private boolean rockClimbing;

	/**
	 * 是否支持马拉松，1支持，0不支持
	 */
	private boolean marathon;

	public boolean getFootball() {
		return this.football;
	}

	public void setFootball(boolean football) {
		this.football = football;
	}

	public boolean getBasketball() {
		return this.basketball;
	}

	public void setBasketball(boolean basketball) {
		this.basketball = basketball;
	}

	public boolean getPingpongball() {
		return this.pingpongball;
	}

	public void setPingpongball(boolean pingpongball) {
		this.pingpongball = pingpongball;
	}

	public boolean getRun() {
		return this.run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean getCycling() {
		return this.cycling;
	}

	public void setCycling(boolean cycling) {
		this.cycling = cycling;
	}

	public boolean getClimbing() {
		return this.climbing;
	}

	public void setClimbing(boolean climbing) {
		this.climbing = climbing;
	}

	public boolean getTreadmill() {
		return this.treadmill;
	}

	public void setTreadmill(boolean treadmill) {
		this.treadmill = treadmill;
	}

	public boolean getHiking() {
		return this.hiking;
	}

	public void setHiking(boolean hiking) {
		this.hiking = hiking;
	}

	public boolean getGolf() {
		return this.golf;
	}

	public void setGolf(boolean golf) {
		this.golf = golf;
	}

	public boolean getSwim() {
		return this.swim;
	}

	public void setSwim(boolean swim) {
		this.swim = swim;
	}

	public boolean getSurfing() {
		return this.surfing;
	}

	public void setSurfing(boolean surfing) {
		this.surfing = surfing;
	}

	public boolean getSkiing() {
		return this.skiing;
	}

	public void setSkiing(boolean skiing) {
		this.skiing = skiing;
	}

	public boolean getRockClimbing() {
		return this.rockClimbing;
	}

	public void setRockClimbing(boolean rockClimbing) {
		this.rockClimbing = rockClimbing;
	}

	public boolean getMarathon() {
		return this.marathon;
	}

	public void setMarathon(boolean marathon) {
		this.marathon = marathon;
	}

}
