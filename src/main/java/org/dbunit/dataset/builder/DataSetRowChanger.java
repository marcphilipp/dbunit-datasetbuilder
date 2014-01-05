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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.stream.BufferedConsumer;
import org.dbunit.dataset.stream.DataSetProducerAdapter;

/**
 * {@link IDataSetManipulator} which change the rows of an existing {@link IDataSet}
 * and creates a new {@link IDataSet}.
 * @author niels
 *
 */
public class DataSetRowChanger extends BufferedConsumer implements IDataSetManipulator {

    private final CachedDataSet dataSet;
    private final IDataSet oldDataSet;
    private final Map<String, List<BasicDataRowBuilder>> tableNameToRow = new HashMap<String, List<BasicDataRowBuilder>>();

    private final IStringPolicy stringPolicy;

    private String currentTableName;
    private Column[] currentTableColumns;
    private Map<String, Integer> columnameToColNr = new HashMap<String, Integer>();

    private static IStringPolicy stringPolicy(boolean ignoreCase) {
        return ignoreCase ? new CaseInsensitiveStringPolicy() : new CaseSensitiveStringPolicy();
    }

    public DataSetRowChanger(IDataSet oldDataSet) throws DataSetException {
        this(true, oldDataSet);
    }

    public DataSetRowChanger(boolean ignoreCase, IDataSet oldDataSet) throws DataSetException {
        this(stringPolicy(ignoreCase), oldDataSet);
    }

    public DataSetRowChanger(IStringPolicy stringPolicy, IDataSet oldDataSet) throws DataSetException {
        this(stringPolicy, oldDataSet, new CachedDataSet());
    }

    private DataSetRowChanger(IStringPolicy stringPolicy, IDataSet oldDataSet, CachedDataSet newDataSet) throws DataSetException {
        super(newDataSet);
        this.dataSet = newDataSet;
        this.stringPolicy = stringPolicy;
        this.oldDataSet = oldDataSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(BasicDataRowBuilder row) throws DataSetException {
        final String key = stringPolicy.toKey(row.getTableName());
        if (!tableNameToRow.containsKey(key)) {
            tableNameToRow.put(key, new ArrayList<BasicDataRowBuilder>());
        }
        tableNameToRow.get(key).add(row);
    }

    public IDataSet build() throws DataSetException {
        final DataSetProducerAdapter provider = new DataSetProducerAdapter(oldDataSet);
        provider.setConsumer(this);
        provider.produce();
        return dataSet;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void row(Object[] values) throws DataSetException {
        final List<BasicDataRowBuilder> changeRows = tableNameToRow.get(currentTableName);
        if (changeRows != null && !changeRows.isEmpty()) {
            for (BasicDataRowBuilder dataRowBuilder : changeRows) {
                boolean match = true;
                final String[] identifierCols = dataRowBuilder.getIdentifierColumns();
                for (int i = 0; i < identifierCols.length; i++) {
                    final Integer idColNr = columnameToColNr.get(stringPolicy.toKey(
                            identifierCols[i]));
                    if (idColNr == null) {
                        throw new IllegalStateException(identifierCols[i] + " is unknown.");
                    }
                    final boolean thisColMatch = values[idColNr.intValue()].equals(
                            dataRowBuilder.getValue(identifierCols[i]));
                    match = match && thisColMatch;
                }
                if (match) {
                    for (int i = 0; i < currentTableColumns.length; i++) {
                        if (dataRowBuilder.existsValue(currentTableColumns[i].getColumnName())) {
                            values[i] = dataRowBuilder.getValue(currentTableColumns[i]);
                        }
                    }
                }
            }
        }
        super.row(values);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void startTable(ITableMetaData metaData) throws DataSetException {
        currentTableName = metaData.getTableName();
        currentTableColumns = metaData.getColumns();
        for (int i = 0; i < currentTableColumns.length; i++) {
            columnameToColNr.put(stringPolicy.toKey(currentTableColumns[i].getColumnName()),
                    Integer.valueOf(i));
        }
        super.startTable(metaData);
    }

    public static BasicDataRowBuilder changeRow(String tableName, String... identifierColumns) {
        final BasicDataRowBuilder row = new BasicDataRowBuilder(tableName, identifierColumns);
        return row;
    }

}
