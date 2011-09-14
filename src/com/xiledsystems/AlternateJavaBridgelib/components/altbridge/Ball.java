package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.PaintUtil;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Simple ball, based on Sprite implementation.
 *
 */

public final class Ball extends Sprite {
  private int radius;
  private int paintColor;
  private Paint paint;
  static final int DEFAULT_RADIUS = 5;

  public Ball(ComponentContainer container) {
    super(container);
    paint = new Paint();

    // Set default properties.
    PaintColor(Component.COLOR_BLACK);
    Radius(DEFAULT_RADIUS);
  }

  // Implement or override methods

  @Override
  protected void onDraw(Canvas canvas) {
    if (visible) {
      canvas.drawCircle((float) xLeft + radius, (float) yTop + radius, radius, paint);
    }
  }

  // The following four methods are required by abstract superclass
  // VisibleComponent.  Because we don't want to expose them to the Simple
  // programmer, we omit the SimpleProperty and DesignerProperty pragmas.
  @Override
  public int Height() {
    return 2 * radius;
  }

  @Override
  public void Height(int height) {
    // ignored
  }

  @Override
  public int Width() {
    return 2 * radius;
  }

  @Override
  public void Width(int width) {
    // ignored
  }

  @Override
  public boolean containsPoint(double qx, double qy) {
    double xCenter = xLeft + radius;
    double yCenter = yTop + radius;
    return ((qx - xCenter) * (qx - xCenter) + (qy - yCenter) * (qy - yCenter))
        <= radius * radius;
  }


  // Additional properties

  
  public void Radius(int radius) {
    this.radius = radius;
    registerChange();
  }

  
  public int Radius() {
    return radius;
  }

  /**
   * PaintColor property getter method.
   *
   * @return  paint RGB color with alpha
   */
  
  public int PaintColor() {
    return paintColor;
  }

  /**
   * PaintColor property setter method.
   *
   * @param argb  paint RGB color with alpha
   */
  
  public void PaintColor(int argb) {
    paintColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
      PaintUtil.changePaint(paint, argb);
    } else {
      // The default paint color is black.
      PaintUtil.changePaint(paint, Component.COLOR_BLACK);
    }
    registerChange();
  }
}
