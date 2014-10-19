package com.birin.infinityprogressbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

//http://en.wikipedia.org/wiki/Lemniscate_of_Bernoulli
//http://mathworld.wolfram.com/EightCurve.html

public class InfinityProgressBar extends View {

	private static final int FIRST = 1;
	private static final int SECOND = 2;
	private static final int THIRD = 3;
	private static final int FOURTH = 4;

	final float radius = 100;
	final float startX = 200;
	final float startY = 200;

	Paint p1 = new Paint();
	Paint p2 = new Paint();
	Paint p3;

	public InfinityProgressBar(Context context) {
		super(context);
		init(null, -1);
	}

	public InfinityProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, -1);
	}

	public InfinityProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		p1.setStrokeWidth(10);
		p1.setAntiAlias(true);
		p1.setDither(true);
		p1.setFilterBitmap(true);
		p1.setColor(Color.RED);
		p1.setStyle(Style.FILL);
		p1.setFlags(Paint.ANTI_ALIAS_FLAG);
		p1.setStrokeCap(Paint.Cap.ROUND);

		p2.setStrokeWidth(20);
		p2.setColor(Color.BLUE);
		p2.setStyle(Style.STROKE);
		p2.setAntiAlias(true);
		p2.setDither(true);
		p2.setFilterBitmap(true);
		p2.setFlags(Paint.ANTI_ALIAS_FLAG);
		p2.setStrokeCap(Paint.Cap.ROUND);

		p3 = new Paint(p2);
		p3.setColor(Color.CYAN);

		preCompute();
	}

	int progress = 0;

	class MinToMaxComparator implements Comparator<PointF> {

		@Override
		public int compare(PointF lhs, PointF rhs) {
			return Float.valueOf(lhs.x).compareTo(rhs.x);
		}

	}

	private void preCompute() {
		long start = System.currentTimeMillis();
		for (int t = 0; t <= 720; t++) {
			double xDouble = (float) ((radius * Math.sqrt(2) * Math.cos(t)) / (Math
					.sin(t) * Math.sin(t) + 1));
			float x = (float) xDouble;
			float y = (float) (xDouble * Math.sin(t));
			// x = a * sin(t)
			// y = x * cos(t)
			x += startX;
			y += startY;
			if (isForthQuadrant(x, y)) {
				Q4.add(new PointF(x, y));
			}
		}
		Collections.sort(Q4, new MinToMaxComparator());
		pathLength = Q4.size() * 4;
		System.out.println("birajt finally after"
				+ (System.currentTimeMillis() - start));
	}

	ArrayList<PointF> Q4 = new ArrayList<PointF>();

	int pathLength;

	boolean isForthQuadrant(float x, float y) {
		return x >= startX && y >= startY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawPath(canvas);
		int ball1Quadrant = getQuadrantFromProgress(progress);
		int ball1Index = getBallIndex(progress, ball1Quadrant);
		PointF ballPoint1 = getBallPoint(ball1Index, ball1Quadrant);
		canvas.drawPoint(ballPoint1.x, ballPoint1.y, p2);

		int ball2Progress = 720 - progress;
		int ball2Quadrant = getQuadrantFromProgress(ball2Progress);
		int ball2Index = getBallIndex(ball2Progress, ball2Quadrant);
		PointF ballPoint2 = getBallPoint(ball2Index, ball2Quadrant);
		canvas.drawPoint(ballPoint2.x, ballPoint2.y, p3);
		progress += 10;
		if (progress >= pathLength) {
			progress = 0;
		}
		System.out.println("biraj refresher.. " + progress);
		postInvalidateDelayed(50);
	}

	private void drawPath(Canvas canvas) {
		for (PointF p : Q4) {
			float x = p.x;
			float y = p.y;
			float negativeX = getNegativeX(x);
			float negativeY = getNegativeX(y);
			canvas.drawPoint(x, y, p1);
			canvas.drawPoint(x, negativeY, p1);
			canvas.drawPoint(negativeX, y, p1);
			canvas.drawPoint(negativeX, negativeY, p1);
		}
	}

	private int getBallIndex(int progress, int ballQuadrant) {
		int index = 0;
		switch (ballQuadrant) {
		case 1:
			index = 180 - (progress - 540);
			break;
		case 2:
			index = 180 - (progress - 180);
			break;
		case 3:
			index = (progress - 360);
			break;
		case 4:
			index = progress;
			break;
		default:
			break;
		}
		return index;
	}

	private int getQuadrantFromProgress(int progress) {
		int quadrant;
		if (progress > 540) {
			quadrant = FIRST;// (-,-)
		} else if (progress > 360) {
			quadrant = THIRD;// (-,+)
		} else if (progress > 180) {
			quadrant = SECOND;// (+,-)
		} else {
			quadrant = FOURTH;// (+,+)
		}
		return quadrant;
	}

	private PointF getBallPoint(int index, int quadrant) {
		PointF drawing = Q4.get(index);
		float ballX = drawing.x;
		float ballY = drawing.y;
		float ballNegativeX = getNegativeX(ballX);
		float ballNegativeY = getNegativeY(ballY);
		if (quadrant == SECOND) {
			ballY = ballNegativeY;
		} else if (quadrant == THIRD) {
			ballX = ballNegativeX;
		} else if (quadrant == FIRST) {
			ballY = ballNegativeY;
			ballX = ballNegativeX;
		}
		return new PointF(ballX, ballY);
	}

	private float getNegativeY(float y) {
		return (2 * startY - y);
	}

	private float getNegativeX(float x) {
		return (2 * startX - x);
	}

}