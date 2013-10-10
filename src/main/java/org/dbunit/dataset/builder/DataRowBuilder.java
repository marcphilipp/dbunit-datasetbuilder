package org.dbunit.dataset.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

public class DataRowBuilder {

	private final DataSetBuilder dataSet;
	private final String tableName;
	private final Map<String, Object> columnNameToValue = new LinkedHashMap<String, Object>();

	protected DataRowBuilder(DataSetBuilder dataSet, String tableName) {
		this.dataSet = dataSet;
		this.tableName = tableName;
	}

	public <T> DataRowBuilder with(ColumnSpec<T> column, T value) {
		return with(column.name(), value);
	}

	public DataRowBuilder with(String columnName, Object value) {
		put(columnName, value);
		return this;
	}

	public DataSetBuilder add() throws DataSetException {
		dataSet.add(this);
		return dataSet;
	}

	public Object[] values(Column[] columns) {
		Object[] values = new Object[columns.length];
		int index = 0;
		for (Column column : columns) {
			values[index++] = getValue(column);
		}
		return values;
	}

	public ITableMetaData toMetaData() {
		Column[] columns = new Column[numberOfColumns()];
		int index = 0;
		for (String columnName : allColumnNames()) {
			columns[index++] = createColumn(columnName);
		}
		return createMetaData(columns);
	}

	protected int numberOfColumns() {
		return columnNameToValue.size();
	}

	protected Set<String> allColumnNames() {
		return columnNameToValue.keySet();
	}

	protected ITableMetaData createMetaData(Column[] columns) {
		return new DefaultTableMetaData(tableName, columns);
	}

	protected Column createColumn(String columnName) {
		return new Column(columnName, DataType.UNKNOWN);
	}

	protected String tableName() {
		return tableName;
	}

	protected void put(String columnName, Object value) {
		columnNameToValue.put(columnName, value);
	}

	protected Object getValue(Column column) {
		return getValue(column.getColumnName());
	}

	protected Object getValue(String columnName) {
		return columnNameToValue.get(columnName);
	}

}
