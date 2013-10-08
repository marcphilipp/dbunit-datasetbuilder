package org.dbunit.dataset.builder;

import java.util.HashMap;
import java.util.Map;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.stream.BufferedConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;

public class DataSetBuilder {

	private final CachedDataSet dataSet = new CachedDataSet();
	private final IDataSetConsumer consumer = new BufferedConsumer(dataSet);
	private final Map<String, TableMetaDataBuilder> tableNameToMetaData = new HashMap<String, TableMetaDataBuilder>();
	private final StringPolicy stringPolicy;

	private String currentTableName;

	private static StringPolicy stringPolicy(boolean ignoreCase) {
		return ignoreCase ? new CaseInsensitiveStringPolicy() : new CaseSensitiveStringPolicy();
	}

	public DataSetBuilder() throws DataSetException {
		this(true);
	}

	public DataSetBuilder(boolean ignoreCase) throws DataSetException {
		this(stringPolicy(ignoreCase));
	}

	public DataSetBuilder(StringPolicy stringPolicy) throws DataSetException {
		this.stringPolicy = stringPolicy;
		consumer.startDataSet();
	}

	public void ensureTableIsPresent(String tableName) throws DataSetException {
		if (containsTable(tableName)) {
			return;
		}
		endTableIfNecessary();
		startTable(metaDataBuilderFor(tableName).build());
	}

	public DataRowBuilder newRow(String tableName) {
		return new DataRowBuilder(this, tableName);
	}

	public IDataSet build() throws DataSetException {
		endTableIfNecessary();
		consumer.endDataSet();
		return dataSet;
	}

	protected void add(DataRowBuilder row) throws DataSetException {
		ITableMetaData metaData = updateTableMetaData(row);
		Object[] values = extractValues(row, metaData);
		notifyConsumer(values);
	}

	private Object[] extractValues(DataRowBuilder row, ITableMetaData metaData) throws DataSetException {
		return row.values(metaData.getColumns());
	}

	private void notifyConsumer(Object[] values) throws DataSetException {
		consumer.row(values);
	}

	private ITableMetaData updateTableMetaData(DataRowBuilder row) throws DataSetException {
		TableMetaDataBuilder builder = metaDataBuilderFor(row.tableName());
		int previousNumberOfColumns = builder.numberOfColumns();
		
		ITableMetaData metaData = builder.with(row.toMetaData()).build();
		int newNumberOfColumns = metaData.getColumns().length;
		
		boolean addedNewColumn = newNumberOfColumns > previousNumberOfColumns;
		handleTable(metaData, addedNewColumn);
		
		return metaData;
	}

	private void handleTable(ITableMetaData metaData, boolean addedNewColumn) throws DataSetException {
		if (isNewTable(metaData.getTableName())) {
			endTableIfNecessary();
			startTable(metaData);
		} else if (addedNewColumn) {
			startTable(metaData);
		}
	}

	private void startTable(ITableMetaData metaData) throws DataSetException {
		currentTableName = metaData.getTableName();
		consumer.startTable(metaData);
	}

	private void endTable() throws DataSetException {
		consumer.endTable();
		currentTableName = null;
	}

	private void endTableIfNecessary() throws DataSetException {
		if (hasCurrentTable()) {
			endTable();
		}
	}

	private boolean hasCurrentTable() {
		return currentTableName != null;
	}

	private boolean isNewTable(String tableName) {
		return currentTableName == null	|| !stringPolicy.areEqual(currentTableName, tableName);
	}

	private TableMetaDataBuilder metaDataBuilderFor(String tableName) {
		String key = stringPolicy.toKey(tableName);
		if (containsKey(key)) {
			return tableNameToMetaData.get(key);
		}
		TableMetaDataBuilder builder = createNewTableMetaDataBuilder(tableName);
		tableNameToMetaData.put(key, builder);
		return builder;
	}

	protected TableMetaDataBuilder createNewTableMetaDataBuilder(String tableName) {
		return new TableMetaDataBuilder(tableName, stringPolicy);
	}

	public boolean containsTable(String tableName) {
		return containsKey(stringPolicy.toKey(tableName));
	}

	private boolean containsKey(String key) {
		return tableNameToMetaData.containsKey(key);
	}
}
