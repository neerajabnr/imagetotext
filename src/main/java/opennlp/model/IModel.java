package opennlp.model;


import java.util.*;
import java.io.Reader;

/**
 * This class supplies a bare minimum model implementation,
 * basically allowing views to be added, removed, and
 * notified of messages, errors, and other model information.
 * 
 * @author Owen Astrachan
 *
 */
public interface IModel
{
    /**
      * Initialize the model using a scanner as the source
      * of the model's initial data.
      * @param s is the Scanner used to initialize
      */
    public void initialize(Scanner s);

    /**
     * Add a view to the model, views are updated 
     * based on application-specific basis.
     * @param view is added to this model's views
     * @see IView
     */
    public void addView(IView view);
    
   
    /**
     * Process an Object in some model-specific way. Typically
     * the user might interact with the model, making a change
     * via the object. Views would be notified if the model
     * changes, for example.
     * @param o is the Object processed by this model
     */
    public void process(Object o);
    
    
    /**
     * Notify views with a string to be displayed appropriately.
     * The string isn't a message and isn't an error, hence methods for
     * displaying those aren't appropriate.
     * @param s to be shown in views
     */
    public void notifyViews(String s);
    
    /**
     * Send a message to each of this model's views.
     * @param s is the message sent to all views
     */
    public void messageViews(String s);
    
    /**
     * Send an error-message to this model's views.
     * @param s is the error message displayed in each view
     */
    public void showViewsError(String s);
}