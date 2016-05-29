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

/**
 * Creates names for the rowbuilder in a javafriendly way.
 * @author niels (linux_java AT users.sourceforge.net)
 * @author Last changed by: niels
 * @version 13.04.2014
 * @since 2.4.10
 *
 */
public class JavaFriendlyNameCreator implements RowBuilderNameCreator {

    /**
     * {@inheritDoc}
     */
    @Override
    public String createRowBuilderName(String tableName) {
        StringBuilder sb = new StringBuilder();
        for(String word : tableName.toLowerCase().split("_")) {
            sb.append(word.substring(0,1).toUpperCase());
            sb.append(word.substring(1));
        }
        sb.append("RowBuilder");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createFactoryMethodName(String tableName) {
        StringBuilder sb = new StringBuilder("new");
        for(String word : tableName.toLowerCase().split("_")) {
            sb.append(word.substring(0,1).toUpperCase());
            sb.append(word.substring(1));
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String createSetterName(String columnName) {
        StringBuilder sb = new StringBuilder();
        for(String word : columnName.toLowerCase().split("_")) {
            sb.append(word.substring(0,1).toUpperCase());
            sb.append(word.substring(1));
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createColumnConstantName(String columnName) {
        StringBuilder sb = new StringBuilder("C_");
        sb.append(columnName.toUpperCase());
        return sb.toString();
    }
}
