package org.dbunit.dataset.builder;

public class CaseInsensitiveStringPolicy implements StringPolicy {

	@Override
	public boolean areEqual(String first, String second) {
		return first.equalsIgnoreCase(second);
	}

	@Override
	public String toKey(String value) {
		return value.toUpperCase();
	}

}
