package com.szip.jswitch.Model.HttpBean;

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
	private int football;

	/**
	 * 是否支持篮球，1支持，0不支持
	 */
	private int basketball;

	/**
	 * 是否支持乒乓球，1支持，0不支持
	 */
	private int pingpongball;

	/**
	 * 是否支持跑步，1支持，0不支持
	 */
	private int run;

	/**
	 * 是否支持骑行，1支持，0不支持
	 */
	private int cycling;

	/**
	 * 是否支持登山，1支持，0不支持
	 */
	private int climbing;

	/**
	 * 是否支持跑步机，1支持，0不支持
	 */
	private int treadmill;

	/**
	 * 是否支持徒步，1支持，0不支持
	 */
	private int hiking;

	/**
	 * 是否支持高尔夫，1支持，0不支持
	 */
	private int golf;

	/**
	 * 是否支持游泳，1支持，0不支持
	 */
	private int swim;

	/**
	 * 是否支持冲浪，1支持，0不支持
	 */
	private int surfing;

	/**
	 * 是否支持滑雪，1支持，0不支持
	 */
	private int skiing;

	/**
	 * 是否支持攀岩，1支持，0不支持
	 */
	private int rockClimbing;

	/**
	 * 是否支持马拉松，1支持，0不支持
	 */
	private int marathon;

	/**
	 * 是否支持马拉松，1支持，0不支持
	 */
	private int sportSync;

	public int getFootball() {
		return this.football;
	}

	public void setFootball(int football) {
		this.football = football;
	}

	public int getBasketball() {
		return this.basketball;
	}

	public void setBasketball(int basketball) {
		this.basketball = basketball;
	}

	public int getPingpongball() {
		return this.pingpongball;
	}

	public void setPingpongball(int pingpongball) {
		this.pingpongball = pingpongball;
	}

	public int getRun() {
		return this.run;
	}

	public void setRun(int run) {
		this.run = run;
	}

	public int getCycling() {
		return this.cycling;
	}

	public void setCycling(int cycling) {
		this.cycling = cycling;
	}

	public int getClimbing() {
		return this.climbing;
	}

	public void setClimbing(int climbing) {
		this.climbing = climbing;
	}

	public int getTreadmill() {
		return this.treadmill;
	}

	public void setTreadmill(int treadmill) {
		this.treadmill = treadmill;
	}

	public int getHiking() {
		return this.hiking;
	}

	public void setHiking(int hiking) {
		this.hiking = hiking;
	}

	public int getGolf() {
		return this.golf;
	}

	public void setGolf(int golf) {
		this.golf = golf;
	}

	public int getSwim() {
		return this.swim;
	}

	public void setSwim(int swim) {
		this.swim = swim;
	}

	public int getSurfing() {
		return this.surfing;
	}

	public void setSurfing(int surfing) {
		this.surfing = surfing;
	}

	public int getSkiing() {
		return this.skiing;
	}

	public void setSkiing(int skiing) {
		this.skiing = skiing;
	}

	public int getRockClimbing() {
		return this.rockClimbing;
	}

	public void setRockClimbing(int rockClimbing) {
		this.rockClimbing = rockClimbing;
	}

	public int getMarathon() {
		return this.marathon;
	}

	public void setMarathon(int marathon) {
		this.marathon = marathon;
	}

	public int getSportSync() {
		return sportSync;
	}
}
