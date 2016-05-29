package org.dbunit.dataset.builder;

public interface RowBuilderNameCreator {

    /**
     * Creates the name of the RowBuilder.
     * @author niels
     * @since 2.4.0
     * @param tableName name of the table.
     * @return name of the RowBuilder.
     */
    String createRowBuilderName(String tableName);

    /**
     * Creates the name of the factory method for the RowBuilder.
     * @author niels
     * @since 2.4.0
     * @param tableName name of the table.
     * @return name of the factory method for the RowBuilder.
     */
    String createFactoryMethodName(String tableName);

    /**
     * Creates the name of the method to define a value.
     * @author niels
     * @since 2.4.0
     * @param columnName name of the column.
     * @return the name of the method to define a value.
     */
    String createSetterName(String columnName);

    /**
     * Creates the name of the constant-name for the column.
     * @author niels
     * @since 2.4.0
     * @param columnName name of the column.
     * @return the name of the constant-name for the column.
     */
    String createColumnConstantName(String columnName);

}