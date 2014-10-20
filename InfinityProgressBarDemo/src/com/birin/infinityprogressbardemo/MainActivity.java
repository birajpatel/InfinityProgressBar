package com.birin.infinityprogressbardemo;

import com.birin.infinityprogressbar.InfinityProgressBar;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new InfinityProgressBar(this));
	}


}
