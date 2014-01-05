package org.dbunit.dataset.builder;

import org.dbunit.dataset.DataSetException;

public class DataRowBuilder extends BasicDataRowBuilder {

    private final DataSetBuilder dataSet;
    protected DataRowBuilder(DataSetBuilder dataSet, String tableName) {
        super(tableName);
        this.dataSet = dataSet;
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

}
