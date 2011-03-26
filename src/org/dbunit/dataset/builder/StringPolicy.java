package org.dbunit.dataset.builder;

public interface StringPolicy {
	
	boolean areEqual(String first, String second);
	
	String toKey(String value);

}
