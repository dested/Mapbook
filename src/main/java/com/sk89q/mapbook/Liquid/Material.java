package com.sk89q.mapbook.Liquid;

public class Material {
	double m;
	double k;
	double rd;
	double v;
	double d;
	double g;

	public Material(double m, double rd, double k, double v, double d, double g) {
		this.m = m;
		this.rd = rd;
		this.k = k;
		this.v = v;
		this.d = d;
		this.g = g;
	}
}