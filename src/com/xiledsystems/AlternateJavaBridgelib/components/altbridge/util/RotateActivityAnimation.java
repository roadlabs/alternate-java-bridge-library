package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class RotateActivityAnimation implements AnimationListener {

		
	public RotateActivityAnimation(ComponentContainer container) {
		
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
		onAnimationFinished();
		
	}

	private void onAnimationFinished() {
		
		
	}

}
