package hx.jdk;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author Administrator
 * 
 * <p>ConcurrentModificationException may be thrown by methods that have detected
 * concurrent modification of an object when such modification is not permissible.
 * <p>For example, it is not generally permissible for one thread to modify a Collection
 * <p>while another thread is iterating over it.
 * 
 * <p>Note that this exception does not always indicate that an object has been concurrently
 * <p>modified by a different thread. If a single thread issues a sequence of method invocations
 * <p>that violates the contract of an object, the object may throw this exception.
 * 
 * <p>For example, if a thread modifies a collection directly while it is iterating
 * <p>over the collection with a fail-fast iterator, the iterator will throw this exception. 

 */
public class IteratorException {
    
    public static void main(String[] args) {
		
    	int n = 3;
   
    	try {
    		List<Integer> list = new ArrayList<>(6);
        	for (int i=0; i<n; i++)
        		list.add(new Random().nextInt());
        	Iterator<Integer> it = list.iterator();
        	for (int i=0; i<n; i++) 
        		list.add(it.next());
        	System.out.println(list);
    	} catch (ConcurrentModificationException ex) {
    		ex.printStackTrace();
    	}  
    	
    	// will not throw this exception becease adding the same element
    	// in a Set doesnot take any effect
    	try {
    		Set<Integer> set = new HashSet<>(8);
        	for (int i=0; i<n; i++)
        		set.add(new Random().nextInt());
        	Iterator<Integer> it = set.iterator();
        	for (int i=0; i<n; i++) 
        		set.add(it.next());
        	System.out.println(set);
    	} catch (ConcurrentModificationException ex) {
    		ex.printStackTrace();
    	}
    	
	}
}
