package com.javonharper.tempo;

import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class TempoActivity extends Activity {
	BpmCalculator bpmCalculator;
	Timer timer;
	Vibrator vibes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tempo);

		bpmCalculator = new BpmCalculator();
		vibes = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initialize();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		bpmCalculator.clearTimes();
		super.onDestroy();
	}

	private void initialize() {
		TextView bpmTextView = (TextView) findViewById(R.id.bpmTextView);
		bpmTextView.setText(getString(R.string.initial_bpm_value));
		setupTouchListener();
	}

	private void setupTouchListener() {
		View tapButton = (View) findViewById(R.id.tapButtonView);
		tapButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					handleTouch();
					return true;
				}
				return false;
			}
		});
	}

	public void handleTouch() {
		vibrate();
		bpmCalculator.recordTime();
		restartResetTimer();
		updateView();
	}

	private void updateView() {
		String displayValue;

		if (bpmCalculator.times.size() >= 2) {
			int bpm = bpmCalculator.getBpm();
			displayValue = Integer.valueOf(bpm).toString();
		} else {
			displayValue = getString(R.string.tap_again);
		}

		TextView bpmTextView = (TextView) findViewById(R.id.bpmTextView);
		bpmTextView.setText(displayValue);
	}

	private void restartResetTimer() {
		stopResetTimer();
		startResetTimer();
	}

	private void startResetTimer() {
		timer = new Timer("reset-bpm-calculator", true);
		timer.schedule(new BpmCalculatorResetTimer(bpmCalculator),
				BpmCalculatorResetTimer.RESET_DURATION);
	}

	private void stopResetTimer() {
		timer.cancel();
	}

	private void vibrate() {
		vibes.vibrate(50);
	}
}