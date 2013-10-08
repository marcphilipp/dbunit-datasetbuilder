package org.dbunit.dataset.builder;

import java.util.LinkedHashMap;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;

public class TableMetaDataBuilder {

	private final String tableName;
	private final StringPolicy policy;
	private final LinkedHashMap<String, Column> keysToColumns = new LinkedHashMap<String, Column>();

	public TableMetaDataBuilder(String tableName, StringPolicy policy) {
		this.tableName = tableName;
		this.policy = policy;
	}

	public TableMetaDataBuilder with(ITableMetaData metaData) throws DataSetException {
		return with(metaData.getColumns());
	}

	public TableMetaDataBuilder with(Column... columns) {
		for (Column column : columns) {
			with(column);
		}
		return this;
	}

	public TableMetaDataBuilder with(Column column) {
		if (isUnknown(column)) {
			add(column);
		}
		return this;
	}

	public int numberOfColumns() {
		return keysToColumns.size();
	}

	public ITableMetaData build() {
		return new DefaultTableMetaData(tableName, columns());
	}

	private void add(Column column) {
		keysToColumns.put(toKey(column), column);
	}

	private String toKey(Column column) {
		return policy.toKey(column.getColumnName());
	}

	private boolean isUnknown(Column column) {
		return !isKnown(column);
	}

	private boolean isKnown(Column column) {
		return keysToColumns.containsKey(toKey(column));
	}

	private Column[] columns() {
		return keysToColumns.values().toArray(new Column[keysToColumns.size()]);
	}

}
