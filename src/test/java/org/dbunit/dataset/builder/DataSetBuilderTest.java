package org.dbunit.dataset.builder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Test;

public class DataSetBuilderTest {

    @Test
    public void ignoringCaseByDefault() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent("PeRsoN");

        assertTrue(builder.containsTable("Person"));
    }

    @Test
    public void notIgnoringCaseWhenSpecified() throws Exception {
        DataSetBuilder builder = new DataSetBuilder(false);
        builder.ensureTableIsPresent("PeRsoN");

        assertFalse(builder.containsTable("Person"));
        assertTrue(builder.containsTable("PeRsoN"));
    }

    @Test
    public void ensuringPresenceOfExistingTableMakesNoHarm() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("person").add();
        builder.ensureTableIsPresent("PeRsoN");

        assertTrue(builder.containsTable("Person"));
    }

    @Test
    public void tablesAreKeptInOrder() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("PERSON").add();
        builder.newRow("ADDRESS").add();
        builder.newRow("_TABLE_").add();
        IDataSet dataSet = builder.build();

        assertArrayEquals(new String[]{"PERSON", "ADDRESS", "_TABLE_"}, dataSet.getTableNames());
    }

    @Test
    public void addsDataForSingleRow() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("PERSON").with("NAME", "Bob").with("AGE", 18).add();

        IDataSet dataSet = builder.build();
        ITable table = dataSet.getTable("PERSON");
        assertEquals(1, table.getRowCount());
        assertEquals("Bob", table.getValue(0, "NAME"));
        assertEquals(18, table.getValue(0, "AGE"));
    }

    @Test
    public void addsDataWithColumnSpec() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec<String> name = ColumnSpec.newColumn("NAME");
        ColumnSpec<Integer> age = ColumnSpec.newColumn("AGE");
        builder.newRow("PERSON").with(name, "Bob").with(age, 18).add();
        
        IDataSet dataSet = builder.build();
        ITable table = dataSet.getTable("PERSON");
        assertEquals(1, table.getRowCount());
        assertEquals("Bob", table.getValue(0, "NAME"));
        assertEquals(18, table.getValue(0, "AGE"));
    }

    @Test
    public void addsDataForMultipleRowsOfDifferentTables() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("PERSON").with("NAME", "Bob").with("AGE", 18).add();
        builder.newRow("ADDRESS").with("STREET", "Main Street").with("NUMBER", 42).add();
        builder.newRow("PERSON").with("NAME", "Alice").with("AGE", 23).add();

        IDataSet dataSet = builder.build();
        ITable table = dataSet.getTable("PERSON");
        assertEquals(2, table.getRowCount());
        assertEquals("Bob", table.getValue(0, "NAME"));
        assertEquals(18, table.getValue(0, "AGE"));
        assertEquals("Alice", table.getValue(1, "NAME"));
        assertEquals(23, table.getValue(1, "AGE"));

        table = dataSet.getTable("ADDRESS");
        assertEquals(1, table.getRowCount());
        assertEquals("Main Street", table.getValue(0, "STREET"));
        assertEquals(42, table.getValue(0, "NUMBER"));
    }

    @Test
    public void addsNewColumnsOnTheFly() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("PERSON").with("NAME", "Bob").add();
        builder.newRow("PERSON").with("AGE", 18).add();

        IDataSet dataSet = builder.build();
        ITable table = dataSet.getTable("PERSON");
        assertEquals(2, table.getRowCount());
        assertEquals("Bob", table.getValue(0, "NAME"));
        assertNull(table.getValue(0, "AGE"));
        assertNull(table.getValue(1, "NAME"));
        assertEquals(18, table.getValue(1, "AGE"));
    }

    @Test
    public void addDataSet() throws Exception {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow("PERSON").with("NAME", "Bob").with("AGE", 18).add();

        IDataSet dataSet = builder.build();
        ITable table = dataSet.getTable("PERSON");

        assertEquals(1, table.getRowCount());

        builder = new DataSetBuilder();
        builder.newRow("PERSON").with("NAME", "John").with("AGE", 19).add();
        builder.addDataSet(dataSet);

        IDataSet dataSet2 = builder.build();
           ITable table2 = dataSet2.getTable("PERSON");

           assertEquals(2, table2.getRowCount());
           assertEquals("John", table2.getValue(0, "NAME"));
           assertEquals(19, table2.getValue(0, "AGE"));

           assertEquals("Bob", table2.getValue(1, "NAME"));
           assertEquals(18, table2.getValue(1, "AGE"));
    }
}
