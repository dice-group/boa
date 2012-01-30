package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * 
 * @author Daniel Gerber
 * @param <V>
 * @param <V>
 * @param <A>
 */
public abstract class BoaCallable<V> implements Callable<Collection<V>>{

    protected String name;
    
    // for the report / statistics
    protected int progress = 0;

    /**
     * 
     * @param name
     */
    public void setName(String name) {

        this.name = name;
    }
    
    /**
     * 
     * @return
     */
    public String getName(){
        
        return this.name;
    }
    
    /**
     * 
     * @return
     */
    public abstract double getProgress();
    
    /**
     * 
     * @return
     */
    public int getNumberDone() {

        return this.progress;
    }
    
    /**
     * 
     * @return
     */
    public abstract int getNumberTotal();
}
