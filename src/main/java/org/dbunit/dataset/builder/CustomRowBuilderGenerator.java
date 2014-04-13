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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;

/**
 * Generates an custom row builder.
 * Improvements: Better names.
 * @author niels
 *
 */
public class CustomRowBuilderGenerator {

    private final File destinationDir;
    private final String packageName;
    private final String encoding;
    private final boolean includeValidation;
    private final RowBuilderNameCreator rowBuilderNameCreator;

    private String indent = "";

    private Map<Class<?>, Class<?>> typeMap = new HashMap<Class<?>, Class<?>>();

    /**
     * Intantiate a new instance.
     * @param destinationDir the directory where the file should be written.
     * @param packageName the packagename of the classes.
     * @param encoding the fileEncoding
     * @param javaFriendlyNames true if you prefer more java common names then
     * the original table and column-names.
     */
    public CustomRowBuilderGenerator(File destinationDir, String packageName,
            String encoding, RowBuilderNameCreator rowBuilderNameCreator) {
        this.destinationDir = new File(destinationDir,
                packageName.replace('.', '/'));
        this.packageName = packageName;
        this.encoding = encoding == null ? System.getProperty("file.encoding") : encoding;
        includeValidation = isValidtorExtensionInClasspath();
        this.rowBuilderNameCreator = rowBuilderNameCreator;
    }

    /**
     * Check if the validation-extension is part of the classpath.
     * @author niels
     * @since 2.4.0
     * @return
     */
    private boolean isValidtorExtensionInClasspath() {
        boolean validatorFound;
        try {
            this.getClass().getClassLoader().loadClass("org.dbunit.validator.IValidator");
            validatorFound = true;
        } catch (ClassNotFoundException e) {
            validatorFound = false;
        }
        return validatorFound;
    }

    public void addTypeMapping(Class<?> source, Class<?> Target) {
        typeMap.put(source, Target);
    }

    public void generate(IDataSet dataSet) throws DataSetException {
        final String[] tableNames = dataSet.getTableNames();
        // tables
        for (int i = 0; i < tableNames.length; i++) {
            // table element
            final String tableName = tableNames[i];
            final ITableMetaData metadata = dataSet.getTableMetaData(tableName);
            final Column[] pkColumns = metadata.getPrimaryKeys();
            final String[] pkColumnNames = new String[pkColumns.length];
            for (int col = 0; col < pkColumnNames.length; col++) {
                pkColumnNames[col] = pkColumns[col].getColumnName();
            }
            // Add the columns
            final Column[] columns = metadata.getColumns();
            final Set<String> notNullColumns = new HashSet<String>();
            final SortedMap<String, Class<?>>  columnTypes = new TreeMap<String, Class<?>>();
            for (int j = 0; j < columns.length; j++) {
                final Column column = columns[j];
                final String columnName = column.getColumnName();
                final boolean nullable = (column.getNullable() == Column.NULLABLE);
                if (!nullable) {
                    notNullColumns.add(columnName);
                }
                columnTypes.put(columnName, mappedType(column.getDataType().getTypeClass()));

            }
            indent="";
            writeClass(tableName, columnTypes, notNullColumns, pkColumnNames);
        }

    }


    private void writeClass(String tableName,
            SortedMap<String, Class<?>> columnTypes, Set<String> notNullColumns, String[] pkColumnNames) {
        final SortedSet<String> importStatements = new TreeSet<String>();
        for (Class<?> clazz : columnTypes.values()) {
            if (!clazz.getPackage().getName().equals("java.lang")) {
                importStatements.add("import " + clazz.getName() +";");
            }
        }

        final String className = rowBuilderNameCreator.createRowBuilderName(tableName);
        destinationDir.mkdirs();
        final File outputFile = new File(destinationDir, className + ".java");
        System.out.println("Write " + outputFile.getAbsolutePath());
        PrintWriter out = null;
        try {
            out = new PrintWriter(outputFile, encoding);
            out.println("package " + packageName + ";");
            out.println();
            out.println("import org.dbunit.dataset.builder.BasicDataRowBuilder;");
            if (includeValidation) {
                out.println("import org.dbunit.validator.IValidator;");
            }
            for (String importStatement : importStatements) {
                out.println(importStatement);
            }
            out.println();
            out.println("public class " + className + " extends BasicDataRowBuilder {");
            indent = indent + "    ";
            out.println();
            println(out, "public static final String TABLE_NAME"
                        + " = \"" + tableName + "\";");
            out.println();
            for (Map.Entry<String, Class<?>> columns : columnTypes.entrySet()) {
                println(out, "public static final String " +
                        rowBuilderNameCreator.createColumnConstantName(columns.getKey())
                        + " = \"" + columns.getKey() + "\";");
            }
            out.println();
            final StringBuilder pk = new StringBuilder("public static final String[] PRIMARY_KEY = {");
            for (int i = 0; i < pkColumnNames.length; i++) {
                pk.append(rowBuilderNameCreator.createColumnConstantName(pkColumnNames[i]));
                if (i < pkColumnNames.length - 1) {
                    pk.append(", ");
                }
            }
            pk.append("};");
            println(out, pk.toString());
            out.println();
            final StringBuilder all = new StringBuilder("public static final String[] ALL_COLUMNS = {");
            for (String colName : columnTypes.keySet()) {
                all.append(rowBuilderNameCreator.createColumnConstantName(colName)).append(", ");
            }
            all.replace(all.length() - 2, all.length(), "};");
            println(out, all.toString());
            out.println();
            println(out, "public " + className + "(String... identifierColumns) {");
            println(out, "    super(TABLE_NAME, identifierColumns);");
            println(out, "    setAllColumnNames(ALL_COLUMNS);");
            for (String column : notNullColumns) {
                println(out, "    addDefaultValue(" + rowBuilderNameCreator.
                        createColumnConstantName(column)
                        + ", " + getDefaultValue(columnTypes.get(column)) + ");");
            }
            println(out, "}");
            out.println();
            for (Map.Entry<String, Class<?>> column : columnTypes.entrySet()) {
                createSetterForColumnValue(out, className, column);
                out.println();

                if (includeValidation) {
                    createSetterForColumnValidator(className, out, column);
                    out.println();
                }
            }
            out.println();
            println(out, "public static " + className + " " +
                    rowBuilderNameCreator.createFactoryMethodName(tableName) +"() {");
            println(out, "    return new " + className + "(PRIMARY_KEY);");
            println(out, "}");
            out.println();
            println(out, "public static " + className + " " +
                    rowBuilderNameCreator.createFactoryMethodName(tableName) +
                    "(String... identifierColumns) {");
            println(out, "    return new " + className + "(identifierColumns);");
            println(out, "}");
            out.println();
            out.println("}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * @author niels
     * @since 2.4.10
     * @param className
     * @param out
     * @param column
     */
    private void createSetterForColumnValidator(final String className,
            PrintWriter out, Map.Entry<String, Class<?>> column) {
        println(out, "public final " + className + " "
                +  rowBuilderNameCreator.createSetterName(column.getKey())
                + " (IValidator<?> value) {");
        println(out, "    with(" + rowBuilderNameCreator.
                createColumnConstantName(column.getKey()) +", value);");
        println(out, "    return this;");
        println(out, "}");
    }

    /**
     * @author niels
     * @since 2.4.10
     * @param out
     * @param className
     * @param column
     */
    private void createSetterForColumnValue(PrintWriter out,
            final String className, Map.Entry<String, Class<?>> column) {
        println(out, "public final " + className + " "
                + rowBuilderNameCreator.createSetterName(column.getKey())
                + " (" + column.getValue().getSimpleName() + " value) {");
        println(out, "    with(" + rowBuilderNameCreator.
                createColumnConstantName(column.getKey()) +", value);");
        println(out, "    return this;");
        println(out, "}");
    }

    private String getDefaultValue(Class<?> clazz) {
        if (Number.class.isAssignableFrom(clazz)) {
            return "new " + clazz.getSimpleName() + "(\"0\")";
        } else if (Date.class.isAssignableFrom(clazz)) {
            return "new " + clazz.getSimpleName() + "(0)";
        } else if (String.class.equals(clazz)) {
            return "\"\"";
        } else if (Boolean.class.equals(clazz)) {
            return "Boolean.FALSE";
        } else {
            System.err.println("Unknown class " + clazz);
        }
        return null;
    }

    private void println(PrintWriter out, String text) {
        out.println(indent + text);
    }

    private Class<?> mappedType(Class<?> type) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        } else {
            return type;
        }
    }

}
