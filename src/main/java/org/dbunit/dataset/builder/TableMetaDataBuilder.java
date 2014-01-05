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

import java.util.LinkedHashMap;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;

public class TableMetaDataBuilder {

    private final String tableName;
    private final IStringPolicy policy;
    private final LinkedHashMap<String, Column> keysToColumns = new LinkedHashMap<String, Column>();

    public TableMetaDataBuilder(String tableName, IStringPolicy policy) {
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
