package com.xiledsystems.AlternateJavaBridgelib.components.collections;


import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.variants.Variant;


public class Collection
{
  private ArrayList<Variant> list = new ArrayList();

  
  public void Clear()
  {
    this.list.clear();
  }

  
  public void Add(Variant item)
  {
    this.list.add(item);
  }

  
  public Variant Item(int index)
  {
    return (Variant)this.list.get(index);
  }

  
  public int Count()
  {
    return this.list.size();
  }

  
  public boolean Contains(Variant item)
  {
    return this.list.contains(item);
  }

  
  public void Remove(Variant item)
  {
    this.list.remove(item);
  }
}
