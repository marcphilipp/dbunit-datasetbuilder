/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.dataset.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

public class BasicDataRowBuilder {


    private final String tableName;
    private final String[] identifierColumns;
    protected final Map<String, Object> columnNameToValue = new LinkedHashMap<String, Object>();
    private String[] allColumnNames = new String[0];
    private final Map<String, Object> defaultValues = new HashMap<String, Object>();

    public BasicDataRowBuilder(String tableName, String... identifierColumns) {
        this.tableName = tableName;
        this.identifierColumns = identifierColumns;
    }

    /**
     * Added the row to the given {@link IDataSetManipulator}.
     * @param dataSet the builder of the dataset.
     * @return the given dataset.
     * @throws DataSetException
     */
    public IDataSetManipulator addTo(IDataSetManipulator dataSet) throws DataSetException {
        dataSet.add(this);
        return dataSet;
    }

    /**
     * Added the column to the Data.
     * @param columnName the name of the column.
     * @param value the value the column should have.
     * @return the current object.
     */
    public BasicDataRowBuilder with(String columnName, Object value) {
        columnNameToValue.put(columnName, value);
        return this;
    }

    /**
     * Define a default value for a column, this is necessary for not null
     * columns.
     * @author niels
     * @since 2.4.10
     * @param columnName
     * @param value
     */
    public void addDefaultValue(String columnName, Object value) {
        defaultValues.put(columnName, value);
    }

    /**
     * Define all values of the table with null or the defaultvalue.
     * All columns are defined with {@link #setAllColumnNames(String...)}.
     * For not null columns you must define default values via
     * {@link #addDefaultValue(String, Object)}.
     * @author niels
     * @since 2.4.10
     * @return the current object.
     */
    public BasicDataRowBuilder fillUndefinedColumns() {
        for (String column : allColumnNames) {
            if (!columnNameToValue.containsKey(column)) {
                columnNameToValue.put(column, defaultValues.get(column));
            }
        }
        return this;
    }

    /**
     * @param allColumnNames the allColumnNames to set
     */
    public void setAllColumnNames(String... allColumnNames) {
        this.allColumnNames = allColumnNames;
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
        for (String columnName : columnNameToValue.keySet()) {
            columns[index++] = createColumn(columnName);
        }
        return createMetaData(columns);
    }

    protected int numberOfColumns() {
        return columnNameToValue.size();
    }


    protected ITableMetaData createMetaData(Column[] columns) {
        return new DefaultTableMetaData(tableName, columns);
    }

    protected Column createColumn(String columnName) {
        return new Column(columnName, DataType.UNKNOWN);
    }

    protected String getTableName() {
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


    protected boolean existsValue(String columnName) {
        return columnNameToValue.containsKey(columnName);
    }


    /**
     * Delivers the identifier column names.
     * @return the identifierColumns
     */
    public String[] getIdentifierColumns() {
        return identifierColumns;
    }



}
