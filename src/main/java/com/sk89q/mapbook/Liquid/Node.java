package com.sk89q.mapbook.Liquid;

public class Node {
	public Node() {
		this.m = 0.0f;
		this.d = 0.0f;
		this.gx = 0.0f;
		this.gy = 0.0f;
		this.u = 0.0f;
		this.v = 0.0f;
		this.ax = 0.0f;
		this.ay = 0.0f;
		this.active = false;

	}

	double m = 0;
	double d = 0;
	double gx = 0;
	double gy = 0;
	double u = 0;
	double v = 0;
	double ax = 0;
	double ay = 0;
	boolean active = false;

	public void clear() {
		this.m = this.d = this.gx = this.gy = this.u = this.v = this.ax = this.ay = 0.0f;
		this.active = false;
	}
}