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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.stream.DataSetProducerAdapter;
import org.dbunit.dataset.stream.IDataSetConsumer;

/**
 * Writes the dataset in the builder definition.
 * @author niels
 *
 */
public class BuilderDataSetWriter implements IDataSetConsumer {

    private final File destinationDir;
    private final String packageName;
    private final String className;
    private final String encoding;
    private final String rowBuilderPackage;
    private final boolean allowAutoBoxing;

    private final String[] importStatements;

    private final RowBuilderNameCreator rowBuilderNameCreator;

    private static final String INDENT = "    ";

    private String currentIndent = "";

    private PrintWriter out;

    private Column[] currentTableColumns;
    private String currentTableName;

    private Map<Class<?>, Class<?>> typeMap = new HashMap<Class<?>, Class<?>>();





    /**
     * Creates a new writer.
     * @param destinationDir directory where the new class should be written.
     * @param packageName the package name of the class.
     * @param className the name of the class.
     * @param encoding the file encoding.
     * @param rowBuilderPackage package where the rowbuilder exists.
     * @param allowAutoBoxing true if auto boxing is allowed.
     * @param rowBuilderNameCreator the naming strategy for teh rowbuilder.
     * @param importStatements some additional imports.
     */
    public BuilderDataSetWriter(File destinationDir,
            String packageName, String className, String encoding, String rowBuilderPackage,
            boolean allowAutoBoxing, RowBuilderNameCreator rowBuilderNameCreator, String... importStatements) {
        this.destinationDir = new File(destinationDir,
                packageName.replace('.', '/'));
        this.packageName = packageName;
        this.className = className;
        this.encoding = encoding == null ? System.getProperty("file.encoding") : encoding;
        this.importStatements = importStatements;
        this.rowBuilderPackage = rowBuilderPackage;
        this.allowAutoBoxing = allowAutoBoxing;
        this.rowBuilderNameCreator = rowBuilderNameCreator;
    }

    public void addTypeMapping(Class<?> source, Class<?> Target) {
        typeMap.put(source, Target);
    }

     /**
     * Writes the given {@link IDataSet} using this writer.
     * @param dataSet The {@link IDataSet} to be written
     * @throws DataSetException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void write(IDataSet dataSet) throws DataSetException, FileNotFoundException, UnsupportedEncodingException {
        try {
            destinationDir.mkdirs();
            out = new PrintWriter(new File(destinationDir, className + ".java"), encoding);
            final DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
            provider.setConsumer(this);
            println("package " + packageName + ";");
            out.println();
            for (String tableName : dataSet.getTableNames()) {
                println("import static " + rowBuilderPackage + "." +
                        rowBuilderNameCreator.createRowBuilderName(tableName) +
                        "." + rowBuilderNameCreator.createFactoryMethodName(tableName) + ";");
            }
            println("import static org.dbunit.dataset.builder.ObjectFactory.*;");
            println("import static java.lang.Boolean.*;");
            out.println();

            for (Class<?> clazz : getAllUsedTypes(dataSet)) {
                println("import " + clazz.getName() +";");
            }
            out.println();
            for (String importStatement : importStatements) {
                println(importStatement);
            }
            out.println();
            out.println("import org.dbunit.dataset.DataSetException;");
            out.println("import org.dbunit.dataset.IDataSet;");
            out.println("import org.dbunit.dataset.builder.DataSetBuilder;");
            out.println();
            provider.produce();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    private Set<Class<?>> getAllUsedTypes(IDataSet dataSet) throws DataSetException {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        for (String tableName : dataSet.getTableNames()) {
            for (Column column : dataSet.getTableMetaData(tableName).getColumns()) {
                final Class<?> type = mappedType(column.getDataType().getTypeClass());
                if (!type.getPackage().getName().equals("java.lang")) {
                    result.add(type);
                }
            }
        }
        return result;
    }



    @Override
    public void startDataSet() throws DataSetException {
        println("public class " + className + " {");
        increaseIndent();
        out.println();
        if (allowAutoBoxing) {
            println("@SuppressWarnings(\"boxing\")");
        }
        println("public static IDataSet build" + className + "DataSet() throws DataSetException {");
        increaseIndent();
        println("final DataSetBuilder b = new DataSetBuilder();");
    }



    @Override
    public void endDataSet() throws DataSetException {
        println("return b.build();");
        decreaseIndent();
        println("}");
        decreaseIndent();
        println("}");
        out.println();
    }


    @Override
    public void startTable(ITableMetaData metaData) throws DataSetException {
        currentTableName = metaData.getTableName();
        currentTableColumns = metaData.getColumns();
    }

    @Override
    public void endTable() throws DataSetException {
        currentTableColumns = null;
        currentTableName = null;
    }

    @Override
    public void row(Object[] values) throws DataSetException {
        out.print(currentIndent + rowBuilderNameCreator.createFactoryMethodName(currentTableName) + "()");
        for (int i = 0; i < values.length; i++) {
              final String columnName = currentTableColumns[i].getColumnName();
              final Object value = values[i];
              if (value != null) {
                  out.print("." +  rowBuilderNameCreator.createSetterName(columnName)
                          + "(" + getValueRepresentation(value) + ")");
              }
        }
        out.println(".addTo(b);");
    }

    private String getValueRepresentation(Object value) {
        final Class<?> type = mappedType(value.getClass());
        if (type == String.class) {
            return "\"" + value.toString().replaceAll("\"", "\\\\\"")
                    .replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")
                    .replaceAll("\\t", "\\\\t") + "\"";
        } else if (type == Boolean.class) {
            return value.toString().toUpperCase();
        } else if (Integer.class.isAssignableFrom(type)) {
            return allowAutoBoxing?value.toString():"i(" + value.toString() + ")";
        } else if (Long.class.isAssignableFrom(type)) {
            return allowAutoBoxing?value.toString()+"L":"l(" + value.toString() + ")";
        } else if (Float.class.isAssignableFrom(type)) {
            return allowAutoBoxing?value.toString()+"F":"f(" + value.toString() + ")";
        } else if (Double.class.isAssignableFrom(type)) {
            return allowAutoBoxing?value.toString()+"D":"d(" + value.toString() + ")";
        } else if (BigInteger.class.isAssignableFrom(type)) {
            return "bi(" + value.toString() + ")";
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return "bd(" + value.toString() + ")";
        } else if (Date.class.isAssignableFrom(type)) {
            return "d(\"" + value.toString() + "\")";
        } else if (Timestamp.class.isAssignableFrom(type)) {
            return "ts(\"" + value.toString() + "\")";
        } else if (Time.class.isAssignableFrom(type)) {
            return "t(\"" + value.toString() + "\")";
        } else if (Number.class.isAssignableFrom(type)) {
            return type.getSimpleName() + ".valueOf(" + value.toString() +")";
        } else {
            return type.getSimpleName() + ".valueOf(\"" + value.toString() +"\")";
        }
    }

    private void increaseIndent() {
        currentIndent += INDENT;
    }

    private void decreaseIndent() {
        currentIndent = currentIndent.substring(0, currentIndent.length() - INDENT.length());
    }

    private void println(String text) {
        out.print(currentIndent);
        out.println(text);
    }

    private Class<?> mappedType(Class<?> type) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        } else {
            return type;
        }
    }


}
