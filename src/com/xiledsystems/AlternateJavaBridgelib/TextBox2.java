package com.xiledsystems.AlternateJavaBridgelib;

import android.text.InputType;
import android.widget.EditText;

import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.TextBoxBase;

public final class TextBox2 extends TextBoxBase implements OnInitializeListener {
	  
	private final Form form;
	
	  private double widthMultiplier;
	  private double heightMultiplier;
	  private boolean autoResize= false;
	  // If true, then accept numeric keyboard input only
	  private boolean acceptsNumbersOnly;

	  /**
	   * Creates a new TextBox component.
	   *
	   * @param container  container, component will be placed in
	   */
	  public TextBox2(ComponentContainer container) {
	    super(container, new EditText(container.$context()));
	    NumbersOnly(false);
	    form = container.$form();
	    
	    if (form instanceof Form2) {
	    	((Form2) form).registerForOnInitialize(this);
	    }
	  }


	  /**
	   * NumbersOnly property getter method.
	   *
	   * @return {@code true} indicates that the textbox accepts numbers only, {@code false} indicates
	   *         that it accepts any text
	   */
	  
	  public boolean NumbersOnly() {
	    return acceptsNumbersOnly;
	  }

	  /**
	   * NumersOnly property setter method.
	   *
	   * @param acceptsNumbersOnly {@code true} restricts input to numeric,
	   * {@code false} allows any text
	   */
	  
	  public void NumbersOnly(boolean acceptsNumbersOnly) {
	    if (acceptsNumbersOnly) {
	      view.setInputType(
	          InputType.TYPE_CLASS_NUMBER |
	          InputType.TYPE_NUMBER_FLAG_SIGNED |
	          InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    } else {
	      view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	    }
	    this.acceptsNumbersOnly = acceptsNumbersOnly;
	  }


	  @Override
		public void onInitialize() {
			
			if (autoResize) {
				this.Width((int) (((Form2) form).scrnWidth * widthMultiplier));
				this.Height((int) (((Form2) form).scrnHeight * heightMultiplier));
			}
			
		}
		
		public void setMultipliers(double widthmultiplier, double heightmultiplier) {
			
			autoResize=true;
			this.widthMultiplier = widthmultiplier;
			this.heightMultiplier = heightmultiplier;
			
		}

	}
