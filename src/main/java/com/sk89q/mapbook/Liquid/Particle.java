package com.sk89q.mapbook.Liquid;

public class Particle {
	double[] gy;
	double[] gx;
	double[] py;
	double[] px;
	int cy;
	int cx;
	double dvdy;
	double dvdx;
	double dudy;
	double dudx;
	double v;
	double u;
	double y;
	double x;
	Material mat;

	public Particle(Material mat, int x, int y, double u, double v) {

		this.mat = mat;
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;

		this.dudx = 0.0f;
		this.dudy = 0.0f;
		this.dvdx = 0.0f;
		this.dvdy = 0.0f;
		this.cx = 0;
		this.cy = 0;

		this.px = new double[] { 0, 0, 0 };
		this.py = new double[] { 0, 0, 0 };
		this.gx = new double[] { 0, 0, 0 };
		this.gy = new double[] { 0, 0, 0 };
	}
}