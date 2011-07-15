package com.xiledsystems.AlternateJavaBridgelib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import com.google.devtools.simple.runtime.components.android.AndroidNonvisibleComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.Component;


public class TinyDB2 extends AndroidNonvisibleComponent implements Component, Deleteable {
    
    public TinyDB2(ComponentContainer container) {
        super(container.$form());
            
    }
    
    public void StoreValue(final String tag, final Object valueToStore) {
    
        try {
            
            Context context = super.form.$context();
            FileOutputStream fos = context.openFileOutput(tag, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(valueToStore);
            os.flush();
            os.close();
                            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("TinyListDB", "File not found! Which is strange because we're trying to save.");
            
            
        } catch (IOException e) {
            e.printStackTrace();
            
        } 
        
    }
    
    public Object GetValue(final String tag) {
        
        try {
            Context context = super.form.$context();
            FileInputStream filestream = context.openFileInput(tag);    
            ObjectInputStream ois = new ObjectInputStream(filestream);      
            return ois.readObject();
        } catch (FileNotFoundException e) {
            Log.e("TinyListDB", "File not found!");
            e.printStackTrace();
            return "null";
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        return "null";        
    }    
    
    @SuppressWarnings("unchecked")
    public ArrayList<String> GetList(final String tag) {
        ArrayList<String> templist = new ArrayList<String>();
        try {
            Context context = super.form.$context();
            FileInputStream filestream = context.openFileInput(tag);    
            ObjectInputStream ois = new ObjectInputStream(filestream);      
            templist = (ArrayList<String>) ois.readObject();
            
        } catch (FileNotFoundException e) {
            Log.e("TinyDB3", "File not found!");
            e.printStackTrace();
            
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return templist;
    }
             
    
    @Override
    public void onDelete() {
    
    }

}

