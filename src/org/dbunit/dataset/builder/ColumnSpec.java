package org.dbunit.dataset.builder;

public class ColumnSpec<T> {

	private final String name;

	public static <T> ColumnSpec<T> newColumn(String name) {
		return new ColumnSpec<T>(name);
	}

	protected ColumnSpec(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

}
