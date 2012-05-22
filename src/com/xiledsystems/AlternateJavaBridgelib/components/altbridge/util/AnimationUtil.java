package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * This class supplies some support for pre-defined component animation.
 *
 * <p>For more information about animation on the Android platform, see
 * the <a href="http://code.google.com/android/documentation.html">
 * Android documentation</a> on code.google.com.
 *
 */
public final class AnimationUtil {

	public static final int LEFT_TO_RIGHT = 0;
	public static final int RIGHT_TO_LEFT = 1;
	public static final int TOP_TO_BOTTOM = 2;
	public static final int BOTTOM_TO_TOP = 3;	
	public static final int ANIM_REPEAT_INFINITE = -1;
	
  private AnimationUtil() {
  }

  /*
   * Animates a component moving it horizontally.
   */
  private static void ApplyHorizontalScrollAnimation(View view, boolean left, int speed) {
    float sign = left ? 1f : -1f;
    AnimationSet animationSet = new AnimationSet(true);
    animationSet.setRepeatCount(Animation.INFINITE);
    animationSet.setRepeatMode(Animation.RESTART);

    TranslateAnimation move = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, sign * 0.70f,
        Animation.RELATIVE_TO_PARENT, sign * -0.70f, Animation.RELATIVE_TO_PARENT, 0,
        Animation.RELATIVE_TO_PARENT, 0);
    move.setStartOffset(0);
    move.setDuration(speed);
    move.setFillAfter(true);
    animationSet.addAnimation(move);
    view.startAnimation(animationSet);
  }

  /**
   * Animates a component (using pre-defined animation kinds).
   *
   * @param view  component to animate
   * @param animation  animation kind
   */
  public static void ApplyAnimation(View view, String animation) {
    // TODO(user): These string constants need to be extracted and defined somewhere else!
    // TODO(user): Also, the endless else-if is inefficient
    if (animation.equals("ScrollRightSlow")) {
      ApplyHorizontalScrollAnimation(view, false, 8000);
    } else if (animation.equals("ScrollRight")) {
      ApplyHorizontalScrollAnimation(view, false, 4000);
    } else if (animation.equals("ScrollRightFast")) {
      ApplyHorizontalScrollAnimation(view, false, 1000);
    } else if (animation.equals("ScrollLeftSlow")) {
      ApplyHorizontalScrollAnimation(view, true, 8000);
    } else if (animation.equals("ScrollLeft")) {
      ApplyHorizontalScrollAnimation(view, true, 4000);
    } else if (animation.equals("ScrollLeftFast")) {
      ApplyHorizontalScrollAnimation(view, true, 1000);
    }
  }
}
