package com.birin.infinityprogressbardemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.birin.infinityprogressbar.InfinityProgressBar;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View infynity = new InfinityProgressBar(this);
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		infynity.setBackgroundColor(Color.GRAY);
		layout.addView(infynity);
		setContentView(layout);
		infynity.getLayoutParams().width = 300;
		infynity.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
	}


}
