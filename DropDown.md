# DropDown #

This works similar to a listpicker. Only, you use a string array, rather than a string arraylist to define the elements.

```
private DropDown dropmenu;

dropmenu = new DropDown(this);


// Here we are defining a string array with three choices. We will then
// pass this string array into our dropdown

String elements[] = { "First choice", "Second Choice", "Third Choice" };

dropmenu.Elements(elements);

// This will set the default choice to the third option. - Note that
// Java code is 0 based. (the first element in an array is accessed using 0)

dropmenu.setSelection(elements[2]);

```

## Events ##

> This component throws the "AfterSelection" event. It also passes the string which was selected. The following converts the object to a string, and passes it into a made up method.

```
if (component.equals(dropmenu) && eventName.equals("AfterSelection")) {
   dropmenuWasSelected(args[0].toString());
   return true;
}
```