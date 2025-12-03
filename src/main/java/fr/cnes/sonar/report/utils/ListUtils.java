package fr.cnes.sonar.report.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to transform Lists
 */
public class ListUtils {

    private ListUtils() {}


    /**
     * Splits input List<String> into two half List<String>
     * 
     * @param input
     * 
     * @return a List containing two List<String>
     */
    public static List<List<String>> splitListOfStrings(final List<String> input) {
    	if (input.isEmpty()) {
    		return new ArrayList<List<String>>(0);
    	} else if (input.size() == 1) {
    		final List<List<String>> result = new ArrayList<List<String>>(1);
    		result.add(input);
    		return result;
    	} else {
    		final List<List<String>> result = new ArrayList<List<String>>(2);
    		
    		// split list
    		final int mid = input.size() / 2;
    		result.add(input.subList(0, mid));
    		result.add(input.subList(mid, input.size()));
    		
    		return result;
    	}
    }

    /**
     * Splits input List<String> into multiple sub-lists, and group them in a list,
     * Cut-points are selected according to quantity of Char in each resulting sub-list,
     * in order to not exceed maxSize, and taking into account a potential future separator size
     * 
     * @param input
     * @param maxSize  Number of char, used as cumulative sizes limit, to select when to cut the input into subset lists
     * @param sepSize  Potential separator size, for future concatenation
     * 
     * @return
     * 
     * @throws Exception 
     */
    public static List<List<String>> splitListOfStrings(final List<String> input, final int maxSize, final int sepSize) throws Exception {
    	final List<List<String>> result = new ArrayList<List<String>>();
    	
    	int size = 0;
    	List<String> accu = new ArrayList<String>();
    	for (final String str : input) {
    		if (str.length() > maxSize) {
    			String errMsg = "Error : ComponentKey is too long ("+ str.length() +") : \""+ str +"\"";
    			throw new Exception(errMsg);
    		} else {
	    		final int futureSize = size + sepSize + str.length();
	    		if (futureSize > maxSize) {
	    			// flush & reset accu
	    			result.add(accu);
	    			accu = new ArrayList<String>();
	    			// insert
	    			accu.add(str);
	    			size  = str.length();
	    		} else {
	    			// insert
	    			accu.add(str);
	    			size = futureSize;
	    		}
    		}
    	}
    	// flush & reset accu
		if (!accu.isEmpty())
			result.add(accu);
		
		return result;
    }
    
    /**
     * Splits each input List<String> into multiple sub-lists, and group them in a common list,
     * Cut-points are selected according to quantity of Char in each resulting sub-list,
     * in order to not exceed maxSize, and taking into account a potential future separator size
     * 
     * @param input
     * @param maxSize  Number of char, used as cumulative sizes limit, to select when to cut the input into subset lists
     * @param sepSize  Potential separator size, for future concatenation
     * 
     * @return
     * 
     * @throws Exception 
     */
    public static List<List<String>> splitListsOfStrings(final List<List<String>> input, final int maxSize, final int sepSize) throws Exception {
    	final List<List<String>> result = new ArrayList<List<String>>();
    	
    	for (final List<String> listOfStrings : input) {
    		final List<List<String>> listsOfStrings = splitListOfStrings(listOfStrings, maxSize, sepSize);
    		result.addAll(listsOfStrings);
    	}
    	
    	return result;
    }

}
