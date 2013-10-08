package org.dbunit.dataset.builder;

public class CaseSensitiveStringPolicy implements StringPolicy {

	@Override
	public boolean areEqual(String first, String second) {
		return first.equals(second);
	}

	@Override
	public String toKey(String value) {
		return value;
	}

}
