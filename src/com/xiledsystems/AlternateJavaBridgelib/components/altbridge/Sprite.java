package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.SpriteComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TimerInternal;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

/**
 * Superclass of sprites able to move and interact with other sprites.
 *
 * This contains logic to ensure that user events never interrupt each other.
 *
 */

public abstract class Sprite extends SpriteComponent implements Deleteable, OnInitializeListener {
  protected final Canvas canvas;
  protected final AnimCanvas aCanvas;
  private TimerInternal timerInternal;
  private Handler androidUIHandler;
  private boolean autoResize= false;
  private double widthMultiplier;
  private double heightMultiplier;
  private double zLayer = 1.0;
  

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
  public Sprite(ComponentContainer container) {
    super();

    // Note that although this is creating a new Handler there is
    // only one UI thread in an Android app and posting to this
    // handler queues up a Runnable for execution on that thread.
    androidUIHandler = new Handler();

    // Add to containing Canvas.
    if ((container instanceof Canvas) || (container instanceof AnimCanvas)) {
      
    } else {
    	throw new IllegalArgumentException("Sprite constructor called with container " + container);
    }
    if (container instanceof Canvas) {
    	this.canvas = (Canvas) container;
    	this.canvas.addSprite(this);
    	this.canvas.$form().registerForOnInitialize(this);
    	this.aCanvas = null;
    } else {
    	this.canvas = null;
    	this.aCanvas = (AnimCanvas) container;
    	this.aCanvas.addSprite(this);
    	this.aCanvas.$form().registerForOnInitialize(this);
    }
    // Set in motion.
    timerInternal = new TimerInternal(this);
    Heading(0);  // Default initial heading

    // Maintain a list of collisions.
    registeredCollisions = new HashSet<SpriteComponent>();
  }
  
  public Sprite(ComponentContainer container, int resourceId) {
	    super();

	    // Note that although this is creating a new Handler there is
	    // only one UI thread in an Android app and posting to this
	    // handler queues up a Runnable for execution on that thread.
	    androidUIHandler = new Handler();

	    // Add to containing Canvas.
	    if ((container instanceof Canvas) || (container instanceof AnimCanvas)) {
	      
	    } else {
	    	throw new IllegalArgumentException("Sprite constructor called with container " + container);
	    }
	    if (container instanceof Canvas) {
	    	this.canvas = (Canvas) container;
	    	this.canvas.addSprite(this);
	    	this.canvas.$form().registerForOnInitialize(this);
	    	this.aCanvas = null;
	    } else {
	    	this.canvas = null;
	    	this.aCanvas = (AnimCanvas) container;
	    	this.aCanvas.addSprite(this);
	    	this.aCanvas.$form().registerForOnInitialize(this);
	    }
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

  public void Z(double layer) {
	  this.zLayer = layer;
	  canvas.changeSpriteLayer(this);
  }
  
  public double Z() {
	  return zLayer;
  }
  
  public void Initialize() {
    initialized = true;
    if (canvas != null) {
    	canvas.registerChange(this);
    } else {
    	aCanvas.registerChange(this);
    }
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
  
  public void CollidedWith(Sprite other) {
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
  
  public void NoLongerCollidingWith(Sprite other) {
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
  
  public boolean CollidingWith(Sprite other) {
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
	  if (canvas != null) {
		  moveIntoBounds(canvas.Width(), canvas.Height());
	  } else {
		  moveIntoBounds(aCanvas.Width(), aCanvas.Height());
	  }
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
    	if (canvas != null) {
    		canvas.getView().invalidate();
    		return;
    	} else {
    		aCanvas.getView().invalidate();
    		return;
    	}
    }
    super.registerChange();
    if (canvas != null) {
    	canvas.registerChange(this);
    } else {
    	aCanvas.registerChange(this);
    }
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
	  if (canvas != null) {
		  if (!canvas.ready()) {
			  return Component.DIRECTION_NONE;
		  } else {
		if (aCanvas != null) {
			if (!aCanvas.ready()) {		
				return Component.DIRECTION_NONE;
				}
			}
		}
	  }

	  if (canvas != null) {
		  return hitEdge(canvas.Width(), canvas.Height());
	  } else {		  
		  return hitEdge(aCanvas.Width(), aCanvas.Height());		  
	  }
  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
	  if (canvas !=null ) {
		  return canvas.$form();
	  } else {
		  return aCanvas.$form();
	  }
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    timerInternal.Enabled(false);
    if (canvas != null) {
    	canvas.removeSprite(this);
    } else {
    	aCanvas.removeSprite(this);
    }
  }
  
  @Override
	public void onInitialize() {
		
		if (autoResize) {
			Form form;
			if (canvas !=null) {
				form = canvas.$form();
			} else {
				form = aCanvas.$form();
			}
			this.Width((int) (form.scrnWidth * widthMultiplier));
			this.Height((int) (form.scrnHeight * heightMultiplier));
		}	
  }

  public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		this.widthMultiplier = widthmultiplier;
		this.heightMultiplier = heightmultiplier;			
	} 

  public double[] getMultipliers() {
		if (autoResize) {
			double[] temp = {widthMultiplier, heightMultiplier}; 
			return temp;
		} else {
			double[] temp = {-1, -1};
			return temp;
		}
	}
  
  // Abstract methods that must be defined by subclasses

  /**
   * Draws the sprite on the given canvas
   *
   * @param canvas the canvas on which to draw
   */
  protected abstract void onDraw(android.graphics.Canvas canvas);
}
