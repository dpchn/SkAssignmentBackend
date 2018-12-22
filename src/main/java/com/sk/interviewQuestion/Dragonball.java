package com.sk.interviewQuestion;

import java.util.Random;

public class Dragonball {
	public static void main(String[] args) {
		Ball ball = new Ball();
		new FoundThread(ball);
		new LostThread(ball);
	}
}

class Ball {
	boolean flag = false;
	int ballCount = 0;

	public synchronized void iFoundaBall() {
		if (flag) {
			try {
				wait();
				ballCount++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		flag = true;
		notify();
	}

	public synchronized void iLostball() {
		if (!flag) {
			try {
				wait();
				int val = new Random().nextInt(ballCount);
				if ((ballCount - val) == 7) {
					System.out.println("Value has been reset to 0");
					ballCount = 0;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		flag = false;
		notify();
	}
}

class FoundThread implements Runnable {
	Ball ball;

	public FoundThread(Ball ball) {
		this.ball = ball;
		new Thread(this).start();
	}

	public void run() {
		for (int i = 0; i < 20; i++) {
			ball.iFoundaBall();
		}
	}
}

class LostThread implements Runnable {
	Ball ball;

	public LostThread(Ball ball) {
		this.ball = ball;
		new Thread(this).start();
	}

	public void run() {
		for (int i = 0; i < 20; i++) {
			ball.iLostball();
		}
	}
}
