package com.examples.HelloPurr;

import android.app.Activity;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.Button;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.Label;
import com.google.devtools.simple.runtime.components.android.Notifier;
import com.google.devtools.simple.runtime.components.android.Sound;
import com.google.devtools.simple.runtime.events.EventDispatcher;

import android.os.Bundle;

public class HelloPurrActivity extends Form implements HandlesEventDispatching {
    
	private Button cat;
	private Label catspeak;
	private Sound snd;
	private int backcount;
	
	void $define() {
		
		this.BackgroundColor(COLOR_BLACK);
		cat = new Button(this);
		cat.Image("cat.png");
		catspeak = new Label(this);
		catspeak.Text("Pet the cat....OR DIE!!!");
		catspeak.BackgroundColor(COLOR_WHITE);
		catspeak.TextColor(COLOR_RED);
		catspeak.FontSize(30.0f);
		snd = new Sound(this);
		snd.Source("meow.mp3");
		EventDispatcher.registerEventForDelegation(this, "ButtonClick", "Click");
		
	}
	
	@Override
	public boolean dispatchEvent(Component component, String id, String eventName,
			Object[] args) {
	    if (component.equals(cat) && eventName.equals("Click")) {
	         catWasClicked();
	         return true;
	    }
	    return false;
	}
	
	@Override
	public void onBackPressed() {
	  backcount++;
	  if (backcount==1) {
	    Notifier note = new Notifier(this); //we can declare and initialize this right here because after it displays a message, we dont need it anymore
	    note.ShowAlert("The cat doesn't want you to go!");
	    return;
	   }
	   if (backcount==2) {
	   finishApplication();
	   }
	}

	private void catWasClicked() {
		
		snd.Play();
		
	}
	
}