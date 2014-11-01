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
	private static final int PRECOMPUTE_LENGTH = 720;
	private static final int DEFAULT_RADIUS = 100;
	private int totalPathLength;
	private int singleQuadrantPathLength;
	private float radius = 0;
	private float startX = 0;
	private float startY = 0;

	Paint p1 = new Paint();
	Paint p2 = new Paint();
	Paint p3;
	Paint p4;
	Paint p5;
	Paint p6;
	Paint p7;
	Paint p8;
	Paint p9;

	public InfinityProgressBar(Context context) {
		super(context);
	}

	public InfinityProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InfinityProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		p1.setStrokeWidth(5);
		p1.setAntiAlias(true);
		p1.setDither(true);
		p1.setFilterBitmap(true);
		p1.setColor(Color.BLACK);
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
		p3.setColor(Color.GREEN);

		p4 = new Paint(p2);
		p4.setColor(Color.YELLOW);

		p5 = new Paint(p2);
		p5.setColor(Color.BLACK);

		p6 = new Paint(p2);
		p6.setColor(Color.MAGENTA);

		p7 = new Paint(p2);
		p7.setColor(Color.LTGRAY);

		p8 = new Paint(p2);
		p8.setColor(Color.rgb(255, 192, 0));

		p9 = new Paint(p2);
		p9.setColor(Color.RED);

		preCompute();
	}

	int progress = 0;

	class MinToMaxComparator implements Comparator<PointF> {

		@Override
		public int compare(PointF lhs, PointF rhs) {
			return Float.valueOf(lhs.x).compareTo(rhs.x);
		}

	}

	float maxX = Float.MIN_VALUE;
	float maxY = Float.MIN_VALUE;
	int xPos = -1;
	int yPos = -1;

	private void preCompute() {
		long start = System.currentTimeMillis();
		for (int t = 0; t <= PRECOMPUTE_LENGTH; t++) {
			double xDouble = (float) ((radius * Math.sqrt(2) * Math.cos(t)) / (Math
					.sin(t) * Math.sin(t) + 1));
			float x = (float) xDouble;
			float y = (float) (xDouble * Math.sin(t));
			// x = a * sin(t)
			// y = x * cos(t)
			x += startX;
			y += startY;
			if (isForthQuadrant(x, y)) {
				if (maxX < x) {
					maxX = x;
					xPos = t;
				}
				if (maxY < y) {
					maxY = y;
					yPos = t;
				}
				Q4.add(new Point(x, y));
			}
		}
		System.out.println("biraj xMax " + (maxX - startX) + " maxY " + (maxY - startY) + " x "
				+ xPos + " y " + yPos);
		Collections.sort(Q4, new MinToMaxComparator());
		System.out.println("biraj precompute "
				+ (System.currentTimeMillis() - start));
	}

	ArrayList<Point> Q4 = new ArrayList<Point>();

	boolean isForthQuadrant(float x, float y) {
		return x >= startX && y >= startY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// drawPath(canvas);
		drawBall(canvas, p2, 0);
		// drawBall(canvas, p3, 1);
		// drawBall(canvas, p4, 2);
		// drawBall(canvas, p5, 3);
		//
		// drawBall(canvas, p6, 4);
		// drawBall(canvas, p7, 5);
		// drawBall(canvas, p8, 6);
		// drawBall(canvas, p9, 7);
		progress += 10;
		if (progress >= totalPathLength) {
			progress = 0;
		}
		postInvalidateDelayed(50);
	}

	int totalBalls = 1;

	private void drawBall(Canvas canvas, Paint ballPaint, int quadrantOffset) {
		final int trailLength = totalPathLength / totalBalls;

		for (int i = progress, j = trailLength; i > (progress - trailLength); i--, j--) {
			int ballProgress = ((i + quadrantOffset * trailLength) % totalPathLength);
			if (ballProgress < 0) {
				ballProgress += totalPathLength;
			}
			if (trailLength != 1) {
				int alpha = (int) (40 * ((j * 1.0) / trailLength));
				ballPaint.setAlpha(alpha);
			}
			int ballQuadrant = getQuadrantFromProgress(ballProgress);
			Point fourthQuadrantBallPoint = pickCorrespondingFourthQuadrantPoint(
					ballProgress, ballQuadrant);
			final float ballX;
			final float ballY;
			if (ballQuadrant == SECOND) {
				ballX = fourthQuadrantBallPoint.x;
				ballY = fourthQuadrantBallPoint.ny;
			} else if (ballQuadrant == THIRD) {
				ballX = fourthQuadrantBallPoint.nx;
				ballY = fourthQuadrantBallPoint.y;
			} else if (ballQuadrant == FIRST) {
				ballX = fourthQuadrantBallPoint.nx;
				ballY = fourthQuadrantBallPoint.ny;
			} else {
				ballX = fourthQuadrantBallPoint.x;
				ballY = fourthQuadrantBallPoint.y;
			}
			canvas.drawPoint(ballX, ballY, ballPaint);
		}
	}

	private void drawPath(Canvas canvas) {
		for (Point p : Q4) {
			canvas.drawPoint(p.x, p.y, p1);
			canvas.drawPoint(p.x, p.ny, p1);
			canvas.drawPoint(p.nx, p.y, p1);
			canvas.drawPoint(p.nx, p.ny, p1);
		}
	}

	private Point pickCorrespondingFourthQuadrantPoint(int progress,
			int quadrant) {
		int relativeIndexForData = 0;
		switch (quadrant) {
		case FIRST:
			relativeIndexForData = (4 * singleQuadrantPathLength) - progress
					- 1;
			break;
		case SECOND:
			relativeIndexForData = (2 * singleQuadrantPathLength) - progress
					- 1;
			break;
		case THIRD:
			relativeIndexForData = (progress - (2 * singleQuadrantPathLength));
			break;
		case FOURTH:
			relativeIndexForData = progress;
			break;
		default:
			break;
		}
		return Q4.get(relativeIndexForData);
	}

	private int getQuadrantFromProgress(int progress) {
		int quadrant;
		if (progress >= (3 * singleQuadrantPathLength)) {
			quadrant = FIRST;// (-,-)
		} else if (progress >= (2 * singleQuadrantPathLength)) {
			quadrant = THIRD;// (-,+)
		} else if (progress >= singleQuadrantPathLength) {
			quadrant = SECOND;// (+,-)
		} else {
			quadrant = FOURTH;// (+,+)
		}
		return quadrant;
	}

	class Point extends PointF {

		final float nx;
		final float ny;

		Point(float x, float y) {
			super(x, y);
			nx = (2 * startX - x);
			ny = (2 * startY - y);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = 0;
		final int sizeX;
		final int sizeY;

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (widthMode == MeasureSpec.UNSPECIFIED
				&& heightMode == MeasureSpec.UNSPECIFIED) {

		} else if (widthMode == MeasureSpec.UNSPECIFIED) {

		} else {

		}

		System.out.println("biraj MeasureSpec " + width + " Ht " + height);
		System.out.println("biraj Widthmode " + printMode(widthMode)
				+ " HtMode " + printMode(heightMode));
		if (width > height) {
			size = height;
		} else {
			size = width;
		}
		System.out.println("biraj onMeasure size " + size);
		int half = ((size - 20) / 2);
		int halfX;
		int halfY;
		radius = (float) (half / Math.sqrt(2));
		startX = half + 10;
		startY = half + 10;
		setMeasuredDimension(size, size);
	}

	private String printMode(int mode) {
		String modeS = "unknown";
		switch (mode) {
		case MeasureSpec.AT_MOST:
			modeS = "AT_MOST";
			break;
		case MeasureSpec.EXACTLY:
			modeS = "EXACTLY";
			break;
		case MeasureSpec.UNSPECIFIED:
			modeS = "UNSPECIFIED";
			break;
		default:
			break;
		}
		return modeS;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		System.out.println("biraj OnSizeChanged w" + w + " ht " + h);
		init(null, -1);
		singleQuadrantPathLength = Q4.size();
		totalPathLength = singleQuadrantPathLength * 4;
	}

}