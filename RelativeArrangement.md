# RelativeArrangement #

> This is a very flexible arrangement type. Everything will get placed at the top left corner of the arrangement, until you place the components where you want.

> Every visible component has the MoveTo method (which only works when the component is placed into a RelativeArrangement). Use the MoveTo method after the screen has been initialized.

private RelativeArrangement ra;
private Button button;

ra = new RelativeArrangement(this);
button = new Button(ra);

// There is a helper method to use a double that you provide to
// automatically place the component where you want. In this case, the x
// value will be .2 X the screen's available width, and .5 X the screen's
// available height. This method will do nothing if the component isn't
// within a RelativeArrangement

button.setLocationMultipliers(.2, .5);


// Manually moving a component to a specified spot on screen - This is 
// after the screen has been initialized

button.MoveTo(50, 150);```