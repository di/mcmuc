package edu.drexel.cs544.mcmuc.ui;

/**
 * Interface for all methods that a user interface must implement, namely input, output, and 
 * special alert messages
 */
public interface UI {

    void input(String inputString);

    void output(String outputString);

    void alert(String string);

}
