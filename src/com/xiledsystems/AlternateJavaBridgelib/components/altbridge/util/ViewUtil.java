package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

/**
 * Helper methods for manipulating {@link View} objects.
 *
 */
public final class ViewUtil {

  private ViewUtil() {
  }

  public static void setChildWidthForHorizontalLayout(View view, int width) {
    // In a horizontal layout, if a child's width is set to fill parent, we must set the
    // LayoutParams width to 0 and the weight to 1. For other widths, we set the weight to 0
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof LinearLayout.LayoutParams) {
      LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
      switch (width) {
        case Component.LENGTH_PREFERRED:
          linearLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
          linearLayoutParams.weight = 0;
          break;
        case Component.LENGTH_FILL_PARENT:
          linearLayoutParams.width = 0;
          linearLayoutParams.weight = 1;
          break;
        default:
          linearLayoutParams.width = width;
          linearLayoutParams.weight = 0;
          break;
      }
      view.requestLayout();
    } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
    	RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
    	switch (width) {
    	case Component.LENGTH_PREFERRED:
    		relativeLayoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case Component.LENGTH_FILL_PARENT:
    		relativeLayoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT;
    		break;
    	default:
    		relativeLayoutParams.width = width;
    		break;
    	}
    	view.requestLayout();
    } else {
      Log.e("ViewUtil", "The view does not have linear layout or relative layout parameters "+layoutParams);
    }
  }

  public static void setChildHeightForHorizontalLayout(View view, int height) {
    // In a horizontal layout, if a child's height is set to fill parent, we can simply set the
    // LayoutParams height to fill parent.
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof LinearLayout.LayoutParams) {
      LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
      switch (height) {
        case Component.LENGTH_PREFERRED:
          linearLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
          break;
        case Component.LENGTH_FILL_PARENT:
          linearLayoutParams.height = LinearLayout.LayoutParams.FILL_PARENT;
          break;
        default:
          linearLayoutParams.height = height;
          break;
      }
      view.requestLayout();
    } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
    	RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
    	switch (height) {
    	case Component.LENGTH_PREFERRED:
    		relativeLayoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case Component.LENGTH_FILL_PARENT:
    		relativeLayoutParams.height = RelativeLayout.LayoutParams.FILL_PARENT;
    		break;
    	default:
    		relativeLayoutParams.height = height;
    		break;
    	}
    	view.requestLayout();
    } else {
    	
      Log.e("ViewUtil", "The view does not have linear layout or relative layout parameters "+layoutParams);
    }
  }

  public static void setChildWidthForVerticalLayout(View view, int width) {
    // In a vertical layout, if a child's width is set to fill parent, we can simply set the
    // LayoutParams width to fill parent.
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof LinearLayout.LayoutParams) {
      LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
      switch (width) {
        case Component.LENGTH_PREFERRED:
          linearLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
          break;
        case Component.LENGTH_FILL_PARENT:
          linearLayoutParams.width = LinearLayout.LayoutParams.FILL_PARENT;
          break;
        default:
          linearLayoutParams.width = width;
          break;
      }
      view.requestLayout();
    } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
    	RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
    	switch (width) {
    	case Component.LENGTH_PREFERRED:
    		relativeLayoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case Component.LENGTH_FILL_PARENT:
    		relativeLayoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT;
    		break;
    	default:
    		relativeLayoutParams.width = width;
    		break;
    	}
    	view.requestLayout();
    } else {
      Log.e("ViewUtil", "The view does not have linear layout or relative layout parameters "+layoutParams);
    }
  }

  public static void setChildHeightForVerticalLayout(View view, int height) {
    // In a vertical layout, if a child's height is set to fill parent, we must set the
    // LayoutParams height to 0 and the weight to 1. For other heights, we set the weight to 0
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof LinearLayout.LayoutParams) {
      LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
      switch (height) {
        case Component.LENGTH_PREFERRED:
          linearLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
          linearLayoutParams.weight = 0;
          break;
        case Component.LENGTH_FILL_PARENT:
          linearLayoutParams.height = 0;
          linearLayoutParams.weight = 1;
          break;
        default:
          linearLayoutParams.height = height;
          linearLayoutParams.weight = 0;
          break;
      }
      view.requestLayout();
    } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
    	RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
    	switch (height) {
    	case Component.LENGTH_PREFERRED:
    		relativeLayoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case Component.LENGTH_FILL_PARENT:
    		relativeLayoutParams.height = RelativeLayout.LayoutParams.FILL_PARENT;
    		break;
    	default:
    		relativeLayoutParams.height = height;
    		break;
    	}
    	view.requestLayout();
    }else {
      Log.e("ViewUtil", "The view does not have linear layout or relative layout parameters "+layoutParams);
    }
  }

  public static void setChildWidthForTableLayout(View view, int width) {
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof TableRow.LayoutParams) {
      TableRow.LayoutParams tableLayoutParams = (TableRow.LayoutParams) layoutParams;
      switch (width) {
        case Component.LENGTH_PREFERRED:
          tableLayoutParams.width = TableRow.LayoutParams.WRAP_CONTENT;
          break;
        case Component.LENGTH_FILL_PARENT:
          tableLayoutParams.width = TableRow.LayoutParams.FILL_PARENT;
          break;
        default:
          tableLayoutParams.width = width;
          break;
      }
      view.requestLayout();
    } else {
      Log.e("ViewUtil", "The view does not have table layout parameters");
    }
  }

  public static void setChildHeightForTableLayout(View view, int height) {
    Object layoutParams = view.getLayoutParams();
    if (layoutParams instanceof TableRow.LayoutParams) {
      TableRow.LayoutParams tableLayoutParams = (TableRow.LayoutParams) layoutParams;
      switch (height) {
        case Component.LENGTH_PREFERRED:
          tableLayoutParams.height = TableRow.LayoutParams.WRAP_CONTENT;
          break;
        case Component.LENGTH_FILL_PARENT:
          tableLayoutParams.height = TableRow.LayoutParams.FILL_PARENT;
          break;
        default:
          tableLayoutParams.height = height;
          break;
      }
      view.requestLayout();
    } else {
      Log.e("ViewUtil", "The view does not have table layout parameters");
    }
  }

  /**
   * Sets the background image for a view.
   */
  public static void setBackgroundImage(View view, Drawable drawable) {
    view.setBackgroundDrawable(drawable);
    view.requestLayout();
  }

  /**
   * Sets the image for an ImageView.
   */
  public static void setImage(ImageView view, Drawable drawable) {
    view.setImageDrawable(drawable);
    if (drawable != null) {
      view.setAdjustViewBounds(true);
    }
    view.requestLayout();
  }

  public static void setBackgroundDrawable(View view, Drawable drawable) {
    view.setBackgroundDrawable(drawable);
    view.invalidate();
  }
}
