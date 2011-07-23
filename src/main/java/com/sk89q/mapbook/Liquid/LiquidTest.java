package com.sk89q.mapbook.Liquid;

import java.util.ArrayList;

import org.bukkit.maps.MapDrawer;

public class LiquidTest {
	private ArrayList<ArrayList<Node>> grid;
	private int gsizeX;
	private int gsizeY;
	private ArrayList<Particle> particles;
	private ArrayList<Node> active;
	private Material water;
	int ParticlesX;
	int ParticlesY;

	public LiquidTest(int gsizeX, int gsizeY, int particlesX, int particlesY) {
		ParticlesX = particlesX;
		ParticlesY = particlesY;

		this.particles = new ArrayList<Particle>();

		this.gsizeX = gsizeX;
		this.gsizeY = gsizeY;

		this.grid = new ArrayList<ArrayList<Node>>(); // Nodes
		this.active = new ArrayList<Node>(); // Nodes
		this.water = new Material(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);

	}

	public void init() {

		int i = 0, j = 0;
		this.grid = new ArrayList<ArrayList<Node>>();
		for (i = 0; i < this.gsizeX; i++) {
			this.grid.add(new ArrayList<Node>());
			for (j = 0; j < this.gsizeY; j++) {
				this.grid.get(i).add(new Node());
			}
		}

		Particle p;
		for (i = 0; i < ParticlesX; i++)
			for (j = 0; j < ParticlesY; j++) {
				p = new Particle(this.water, i + 4, j + 4, 0.0f, 0.0f);
				this.particles.add(p);
			}
	}

	public void paint(MapDrawer mapDrawer) {
		 
		ArrayList<Particle> particles = this.particles;
		int len = particles.size();
		mapDrawer.setForeColor((byte)19);
		for (int pi = 0; pi < len; pi++) {
			Particle p = particles.get(pi);
			mapDrawer.drawLine((int) (4.0 * p.x), (int) (4.0 * p.y), (int) (4.0 * (p.x - p.u)), (int) (4.0 * (p.y - p.v)),1);
		} 
	}

	public void simulate() {

		for (Node n : this.active)
			n.clear();
		this.active.clear();

		int i, j;
		double x, y, phi;
		double fx = 0.0f, fy = 0.0f;
		ArrayList<Particle> particles = this.particles;
		for (int pi = 0; pi < particles.size(); pi++) {
			Particle p = particles.get(pi);
			p.cx = (int) (p.x - 0.5);
			p.cy = (int) (p.y - 0.5);

			x = p.cx - p.x;
			p.px[0] = (0.5 * x * x + 1.5 * x + 1.125);
			p.gx[0] = (x + 1.5);
			x += 1.0;
			p.px[1] = (-x * x + 0.75);
			p.gx[1] = (-2.0 * x);
			x += 1.0;
			p.px[2] = (0.5 * x * x - 1.5 * x + 1.125);
			p.gx[2] = (x - 1.5);

			y = p.cy - p.y;
			p.py[0] = (0.5 * y * y + 1.5 * y + 1.125);
			p.gy[0] = (y + 1.5);
			y += 1.0;
			p.py[1] = (-y * y + 0.75);
			p.gy[1] = (-2.0 * y);
			y += 1.0;
			p.py[2] = (0.5 * y * y - 1.5 * y + 1.125);
			p.gy[2] = (y - 1.5);

			for (int i1 = 0; i1 < 3; i1++) {
				for (int j1 = 0; j1 < 3; j1++) {
					Node n = this.grid.get(p.cx + i1).get(p.cy + j1);
					if (!n.active) {
						this.active.add(n);
						n.active = true;
					}
					phi = p.px[i1] * p.py[j1];
					n.m += phi * p.mat.m;
					n.d += phi;
					n.gx += p.gx[i1] * p.py[j1];
					n.gy += p.px[i1] * p.gy[j1];
				}
			}
		}

		double density, pressure, weight;
		Node n01, n02;
		Node n11, n12;
		int cx, cy;
		int cxi, cyi;

		double pdx, pdy;
		double C20, C02, C30, C03;
		double csum1, csum2;
		double C21, C31, C12, C13, C11;

		double u, u2, u3;
		double v, v2, v3;

		ArrayList<Particle> particles1 = this.particles;
		ArrayList<ArrayList<Node>> grid = this.grid;
		for (int pi = 0, ln = particles1.size(); pi < ln; pi++) {
			Particle p = particles1.get(pi);

			cx = (int) (p.x);
			cy = (int) (p.y);
			cxi = cx + 1;
			cyi = cy + 1;

			n01 = grid.get(cx).get(cy);
			n02 = grid.get(cx).get(cyi);
			n11 = grid.get(cxi).get(cy);
			n12 = grid.get(cxi).get(cyi);
			double n11d = n11.d;
			double n11gx = n11.gx;
			double n11gy = n11.gy;
			double n12d = n11.d;
			double n12gy = n12.gy;
			double n12gx = n12.gx;
			double n02d = n02.d;
			double n02gx = n02.gx;
			double n02gy = n02.gy;
			double n01d = n01.d;
			double n01gx = n01.gx;
			double n01gy = n01.gy;

			pdx = n11d - n01d;
			pdy = n02d - n01d;
			C20 = 3.0 * pdx - n11gx - 2.0 * n01gx;
			C02 = 3.0 * pdy - n02gy - 2.0 * n01gy;
			C30 = -2.0 * pdx + n11gx + n01gx;
			C03 = -2.0 * pdy + n02gy + n01gy;
			csum1 = n01d + n01gy + C02 + C03;
			csum2 = n01d + n01gx + C20 + C30;
			C21 = 3.0 * n12d - 2.0 * n02gx - n12gx - 3.0 * csum1 - C20;
			C31 = -2.0 * n12d + n02gx + n12gx + 2.0 * csum1 - C30;
			C12 = 3.0 * n12d - 2.0 * n11gy - n12gy - 3.0 * csum2 - C02;
			C13 = -2.0 * n12d + n11gy + n12gy + 2.0 * csum2 - C03;
			C11 = n02gx - C13 - C12 - n01gx;

			u = p.x - cx;
			u2 = u * u;
			u3 = u * u2;
			v = p.y - cy;
			v2 = v * v;
			v3 = v * v2;
			density = n01d + n01gx * u + n01gy * v + C20 * u2 + C02 * v2 + C30
					* u3 + C03 * v3 + C21 * u2 * v + C31 * u3 * v + C12 * u
					* v2 + C13 * u * v3 + C11 * u * v;

			pressure = density - 1;
			if (pressure > 2.0)
				pressure = 2.0;

			fx = 0.0;
			fy = 0.0;

			if (p.x < 4.0)
				fx += p.mat.m * (4.0 - p.x);
			else if (p.x > this.gsizeX - 5)
				fx += p.mat.m * (this.gsizeX - 5 - p.x);

			if (p.y < 4.0)
				fy += p.mat.m * (4.0 - p.y);
			else if (p.y > this.gsizeY - 5)
				fy += p.mat.m * (this.gsizeY - 5 - p.y);

			for (int i11 = 0; i11 < 3; i11++) {
				for (int j11 = 0; j11 < 3; j11++) {
					Node n = grid.get(p.cx + i11).get(p.cy + j11);
					phi = p.px[i11] * p.py[j11];
					n.ax += -((p.gx[i11] * p.py[j11]) * pressure) + fx * phi;
					n.ay += -((p.px[i11] * p.gy[j11]) * pressure) + fy * phi;
				}
			}
		}
		ArrayList<Node> active = this.active;
		for (int ni = 0, ln = active.size(); ni < ln; ni++) {
			Node n = active.get(ni);
			double nm = n.m;
			if (nm > 0.0) {
				n.ax /= nm;
				n.ay /= nm;
				n.ay += 0.03;
			}
		}
		double mu, mv;
		ArrayList<Particle> particles11 = this.particles;
		ArrayList<ArrayList<Node>> grid1 = this.grid;
		for (int pi = 0, ln = particles11.size(); pi < ln; pi++) {
			Particle p = particles11.get(pi);
			Node n;
			for (int i1 = 0; i1 < 3; i1++) {
				for (int j1 = 0; j1 < 3; j1++) {
					n = grid1.get(p.cx + i1).get(p.cy + j1);
					phi = p.px[i1] * p.py[j1];
					p.u += phi * n.ax;
					p.v += phi * n.ay;
				}
			}
			mu = p.mat.m * p.u;
			mv = p.mat.m * p.v;
			for (int i1 = 0; i1 < 3; i1++) {
				for (int j1 = 0; j1 < 3; j1++) {
					n = grid1.get(p.cx + i1).get(p.cy + j1);
					phi = p.px[i1] * p.py[j1];
					n.u += phi * mu;
					n.v += phi * mv;
				}
			}
		}

		ArrayList<Node> active1 = this.active;
		for (int ni = 0, ln = active1.size(); ni < ln; ni++) {
			Node n = active1.get(ni);
			double m = n.m;
			if (m > 0.0) {
				n.u /= m;
				n.v /= m;
			}
		}

		double gu, gv;
		ArrayList<Particle> particles111 = this.particles;
		for (int pi = 0, ln = particles111.size(); pi < ln; pi++) {
			Particle p = particles111.get(pi);

			gu = 0.0;
			gv = 0.0;
			for (int i2 = 0; i2 < 3; i2++) {
				for (int j1 = 0; j1 < 3; j1++) {
					Node n = grid1.get(p.cx + i2).get(p.cy + j1);
					phi = p.px[i2] * p.py[j1];
					gu += phi * n.u;
					gv += phi * n.v;
				}
			}
			p.x += gu;
			p.y += gv;
			p.u += 1.0 * (gu - p.u);
			p.v += 1.0 * (gv - p.v);
			if (p.x < 1.0) {
				p.x = (1.0 + Math.random() * 0.01);
				p.u = 0.0;
			} else if (p.x > this.gsizeX - 2) {
				p.x = (this.gsizeX - 2 - Math.random() * 0.01);
				p.u = 0.0;
			}
			if (p.y < 1.0) {
				p.y = (1.0 + Math.random() * 0.01);
				p.v = 0.0;
			} else if (p.y > this.gsizeY - 2) {
				p.y = (this.gsizeY - 2 - Math.random() * 0.01);
				p.v = 0.0;
			}
		}
	}

}