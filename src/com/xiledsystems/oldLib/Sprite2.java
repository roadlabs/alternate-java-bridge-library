package com.xiledsystems.AlternateJavaBridgelib;

import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.util.Log;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.SpriteComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.android.util.TimerInternal;
import com.google.devtools.simple.runtime.errors.IllegalArgumentError;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public abstract class Sprite2 extends SpriteComponent implements Deleteable {
	  protected final Canvas2 canvas;
	  private TimerInternal timerInternal;
	  private Handler androidUIHandler;

	  // Keeps track of which other sprites are currently colliding with this one.
	  // That way, we don't raise CollidedWith() more than once for each collision.
	  // Events are only raised when sprites are added to this collision set.  They
	  // are removed when they no longer collide.
	  private Set<SpriteComponent> registeredCollisions;

	  // This variable prevents events from being raised before construction of
	  // all components has taken place.  This was added to fix bug 2262218.
	  protected boolean initialized = false;

	  /**
	   * Creates a new Sprite component.
	   *
	   * @param container where the component will be placed
	   */
	  public Sprite2(ComponentContainer container) {
	    super();

	    // Note that although this is creating a new Handler there is
	    // only one UI thread in an Android app and posting to this
	    // handler queues up a Runnable for execution on that thread.
	    androidUIHandler = new Handler();

	    // Add to containing Canvas.
	    if (!(container instanceof Canvas2)) {
	      throw new IllegalArgumentError("Sprite constructor called with container " + container);
	    }
	    this.canvas = (Canvas2) container;
	    this.canvas.addSprite(this);

	    // Set in motion.
	    timerInternal = new TimerInternal(this);
	    Heading(0);  // Default initial heading

	    // Maintain a list of collisions.
	    registeredCollisions = new HashSet<SpriteComponent>();
	  }

	  @Override
	  protected void requestEvent(final SpriteComponent sprite,
	                              final String eventName,
	                              final Object... args) {
	    androidUIHandler.post(new Runnable() {
	        public void run() {
	          EventDispatcher.dispatchEvent(sprite, eventName, args);
	        }});
	  }

	  public void Initialize() {
	    initialized = true;
	    canvas.registerChange(this);
	  }

	  // Methods to launch event handlers

	  /**
	   * Handler for CollidedWith events, called when two sprites collide.
	   * Note that checking for collisions with a rotated ImageSprite currently
	   * achecks against the sprite's unrotated position.  Therefore, collision
	   * checking will be inaccurate for tall narrow or short wide sprites that are rotated.
	   *
	   * @param other the other sprite in the collision
	   */
	  // This is defined in {@code Sprite} rather than in
	  // {@link com.google.devtools.simple.runtime.components.SpriteComponent} so
	  // the argument can be of type {@code Sprite}.  This also registers the
	  // collision to a private variable {@link #registeredCollisions} so that
	  // this event is not raised multiple times for one collision.
	  
	  public void CollidedWith(Sprite2 other) {
	    if (registeredCollisions.contains(other)) {
	      Log.e("Sprite", "Collision between sprites " + this + " and "
	          + other + " re-registered");
	      return;
	    }
	    registeredCollisions.add(other);
	    requestEvent(this, "CollidedWith", other);
	  }

	  /**
	   * Handler for NoLongerCollidingWith events, called when a pair of sprites
	   * cease colliding.  This also registers the removal of the collision to a
	   * private variable {@link #registeredCollisions} so that
	   * {@link #CollidedWith(Sprite)} and this event are only raised once per
	   * beginning and ending of a collision.
	   *
	   * @param other the sprite formerly colliding with this sprite
	   */
	  
	  public void NoLongerCollidingWith(Sprite2 other) {
	    if (!registeredCollisions.contains(other)) {
	      Log.e("Sprite", "Collision between sprites " + this + " and "
	          + other + " removed but not present");
	    }
	    registeredCollisions.remove(other);
	  }

	  // Methods providing Simple functions

	  // This is primarily used to enforce raising only
	  // one {@link #CollidedWith(Sprite)} event per collision but is also
	  // made available to the Simple programmer.
	  /**
	   * Indicates whether a collision has been registered between this sprite
	   * and the passed sprite.
	   *
	   * @param other the sprite to check for collision with this sprite
	   * @return {@code true} if a collision event has been raised for the pair of
	   *         sprites and they still are in collision, {@code false} otherwise.
	   */
	  
	  public boolean CollidingWith(Sprite2 other) {
	    return registeredCollisions.contains(other);
	  }

	  /**
	   * Moves the sprite back in bounds if part of it extends out of bounds,
	   * having no effect otherwise. If the sprite is too wide to fit on the
	   * canvas, this aligns the left side of the sprite with the left side of the
	   * canvas. If the sprite is too tall to fit on the canvas, this aligns the
	   * top side of the sprite with the top side of the canvas.
	   */
	  @Override
	  
	  public void MoveIntoBounds() {
	    moveIntoBounds(canvas.Width(), canvas.Height());
	  }

	  // Implementation of AlarmHandler interface

	  /**
	   * Moves and redraws sprite, registering changes.
	   */
	  public void alarm() {
	    // This check on initialized is currently redundant, since registerChange()
	    // checks it too.
	    if (initialized && speed != 0) {
	      updateCoordinates();
	      registerChange();
	    }
	  }

	  // Methods supporting properties related to AlarmHandler

	  /**
	   * Interval property getter method.
	   *
	   * @return  timer interval in ms
	   */
	  @Override
	  
	  public int Interval() {
	    return timerInternal.Interval();
	  }

	  /**
	   * Interval property setter method: sets the interval between timer events.
	   *
	   * @param interval  timer interval in ms
	   */
	  @Override
	  
	  public void Interval(int interval) {
	    timerInternal.Interval(interval);
	  }

	  /**
	   * Enabled property getter method.
	   *
	   * @return  {@code true} indicates a running timer, {@code false} a stopped
	   *          timer
	   */
	  @Override
	  
	  public boolean Enabled() {
	    return timerInternal.Enabled();
	  }

	  /**
	   * Enabled property setter method: starts or stops the timer.
	   *
	   * @param enabled  {@code true} starts the timer, {@code false} stops it
	   */
	  @Override
	  
	  public void Enabled(boolean enabled) {
	    timerInternal.Enabled(enabled);
	  }

	  // Methods supporting move-related functionality

	  @Override
	  public void registerChange() {
	    // This was added to fix bug 2262218, where Ball.CollidedWith() was called
	    // before all components had been constructed.
	    if (!initialized) {
	      // During REPL, components are not initalized, but we still want to repaint the canvas.
	      canvas.getView().invalidate();
	      return;
	    }
	    super.registerChange();
	    canvas.registerChange(this);
	  }

	  /**
	   * Specifies which edge of the canvas has been hit by the SpriteComponent, if
	   * any, moving the sprite back in bounds.
	   *
	   * @return {@link Component#DIRECTION_NONE} if no edge has been hit, or a
	   *         direction (e.g., {@link Component#DIRECTION_NORTHEAST}) if that
	   *         edge of the canvas has been hit
	   */
	  @Override
	  protected int hitEdge() {
	    if (!canvas.ready()) {
	      return Component.DIRECTION_NONE;
	    }

	    return hitEdge(canvas.Width(), canvas.Height());
	  }

	  // Component implementation

	  @Override
	  public HandlesEventDispatching getDispatchDelegate() {
	    return canvas.$form();
	  }

	  // Deleteable implementation

	  @Override
	  public void onDelete() {
	    timerInternal.Enabled(false);
	    canvas.removeSprite(this);
	  }

	  // Abstract methods that must be defined by subclasses

	  /**
	   * Draws the sprite on the given canvas
	   *
	   * @param canvas the canvas on which to draw
	   */
	  protected abstract void onDraw(android.graphics.Canvas canvas);
	}
