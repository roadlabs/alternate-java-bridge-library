package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabWidget;

/**
 * Linear layout for placing components horizontally or vertically.
 *
 */

public final class LinearLayout implements Layout {

  private final android.widget.LinearLayout layoutManager;

  /**
   * Creates a new linear layout.
   *
   * @param context  view context
   * @param orientation one of
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_HORIZONTAL}.
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_VERTICAL}
   */
  LinearLayout(Context context, int orientation) {
    this(context, orientation, null, null);
  }

  /**
   * Creates a new linear layout with a preferred empty width/height.
   *
   * @param context  view context
   * @param orientation one of
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_HORIZONTAL}.
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_VERTICAL}
   * @param preferredEmptyWidth the preferred width of an empty layout
   * @param preferredEmptyHeight the preferred height of an empty layout
   */
  LinearLayout(Context context, int orientation, final Integer preferredEmptyWidth,
      final Integer preferredEmptyHeight) {
    if (preferredEmptyWidth == null && preferredEmptyHeight != null ||
        preferredEmptyWidth != null && preferredEmptyHeight == null) {
      throw new IllegalArgumentException("LinearLayout - preferredEmptyWidth and " +
          "preferredEmptyHeight must be either both null or both not null");
    }

    // Create an Android LinearLayout, but override onMeasure so that we can use our preferred
    // empty width/height.
    layoutManager = new android.widget.LinearLayout(context) {
      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If there was no preferred empty width/height specified (see constructors above), just
        // call super. (This is the case for the Form component.)
        if (preferredEmptyWidth == null || preferredEmptyHeight == null) {
          super.onMeasure(widthMeasureSpec, heightMeasureSpec);
          return;
        }

        // If the layout has any children, just call super.
        if (getChildCount() != 0) {
          super.onMeasure(widthMeasureSpec, heightMeasureSpec);
          return;
        }

        setMeasuredDimension(getSize(widthMeasureSpec, preferredEmptyWidth),
                             getSize(heightMeasureSpec, preferredEmptyHeight));
      }

      private int getSize(int measureSpec, int preferredSize) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
        // We were told how big to be
          result = specSize;
        } else {
        // Use the preferred size.
          result = preferredSize;
          if (specMode == MeasureSpec.AT_MOST) {
          // Respect AT_MOST value if that was what is called for by measureSpec
            result = Math.min(result, specSize);
          }
        }

        return result;
      }
    };

    layoutManager.setOrientation(
        orientation == ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL ?
        android.widget.LinearLayout.HORIZONTAL : android.widget.LinearLayout.VERTICAL);
  }

  // Layout implementation

  public ViewGroup getLayoutManager() {
    return layoutManager;
  }

  public void add(AndroidViewComponent component) {
    layoutManager.addView(component.getView(), new android.widget.LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,  // width
        ViewGroup.LayoutParams.WRAP_CONTENT,  // height
        0f));                                 // weight
  }
  
  public void add(TabWidget component) {
	    android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
	    		android.widget.LinearLayout.LayoutParams.FILL_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
	    layoutManager.addView(component, lp);
	    FrameLayout framelayout = new FrameLayout(component.getContext());
	    FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(
	    		FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
	    component.addView(framelayout, lp2);
  }
    
  
}
