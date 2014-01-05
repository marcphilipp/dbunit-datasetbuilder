package org.dbunit.dataset.builder;

import static org.junit.Assert.assertEquals;
import static org.dbunit.dataset.builder.DataSetBuilder.newBasicRow;
import static org.dbunit.dataset.builder.DataSetRowChanger.changeRow;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Test;

/**
 * Test {@link DataSetRowChanger}.
 * @author niels (linux-java AT users.sourceforge.net)
 * @author Last changed by: niels
 * @version 01.01.2014
 * @since 2.4.10
 *
 */
public class DataSetRowChangerTest {

    private static final String T_ADDRESS = "ADDRESS";
    private static final String T_PERSON = "PERSON";

    private static final String C_FIRSTNAME = "FIRSTNAME";
    private static final String C_LAST_NAME = "LastName";
    private static final String C_AGE = "AGE";

    private static final String BOB = "Bob";
    private static final String ALICE = "Alice";
    private static final String MILLER = "Miller";

    private static final String C_ID = "ID";
    private static final String C_STREET = "STREET";
    private static final String C_NUMBER = "NUMBER";
    private static final String MAIN_STREET = "Main Street";



    @Test
    public void testBuild() throws Exception {
        //Arrange
        final DataSetBuilder builder = new DataSetBuilder(false);

        newBasicRow(T_PERSON).with(C_FIRSTNAME, BOB).with(C_LAST_NAME, MILLER).with(C_AGE, 18).addTo(builder);
        newBasicRow(T_ADDRESS).with(C_ID, 1).with(C_STREET, MAIN_STREET).with(C_NUMBER, 42).addTo(builder);
        newBasicRow(T_PERSON).with(C_FIRSTNAME, ALICE).with(C_LAST_NAME, MILLER).with(C_AGE, 23).addTo(builder);
        final CachedDataSet oldDataSet = (CachedDataSet)builder.build();

        final DataSetRowChanger changer = new DataSetRowChanger(oldDataSet);
        changeRow(T_PERSON, C_LAST_NAME, C_FIRSTNAME).with(C_FIRSTNAME, BOB).with(C_LAST_NAME, MILLER).with(C_AGE, 21).addTo(changer);
        changeRow("Address", C_ID).with(C_ID, 1).with(C_NUMBER, 21).addTo(changer);
        changeRow(T_ADDRESS, C_ID).with(C_ID, 2).with(C_NUMBER, 72).addTo(changer);
        //Act
        final IDataSet dataSet = changer.build();
        //Assert
        ITable table = dataSet.getTable(T_PERSON);
        assertEquals(2, table.getRowCount());
        assertEquals(BOB, table.getValue(0, C_FIRSTNAME));
        assertEquals(MILLER, table.getValue(0, "LASTNAME"));
        assertEquals(21, table.getValue(0, C_AGE));
        assertEquals(ALICE, table.getValue(1, C_FIRSTNAME));
        assertEquals(MILLER, table.getValue(0, "LASTNAME"));
        assertEquals(23, table.getValue(1, C_AGE));

        table = dataSet.getTable(T_ADDRESS);
        assertEquals(1, table.getRowCount());
        assertEquals(MAIN_STREET, table.getValue(0, C_STREET));
        assertEquals(21, table.getValue(0, C_NUMBER));
    }

    @Test
    public void testBuildCaseSensitive() throws Exception {
        //Arrange
        final DataSetBuilder builder = new DataSetBuilder(true);

        newBasicRow(T_PERSON).with(C_FIRSTNAME, BOB).with(C_LAST_NAME, MILLER).with(C_AGE, 18).addTo(builder);
        newBasicRow(T_ADDRESS).with(C_ID, 1).with(C_STREET, MAIN_STREET).with(C_NUMBER, 42).addTo(builder);
        newBasicRow(T_PERSON).with(C_FIRSTNAME, ALICE).with(C_LAST_NAME, MILLER).with(C_AGE, 23).addTo(builder);
        final CachedDataSet oldDataSet = (CachedDataSet)builder.build();

        final DataSetRowChanger changer = new DataSetRowChanger(false, oldDataSet);
        changeRow(T_PERSON, C_LAST_NAME, C_FIRSTNAME).with(C_FIRSTNAME, BOB).with(C_LAST_NAME, MILLER).with(C_AGE, 21).addTo(changer);
        changeRow(T_PERSON, C_LAST_NAME, C_FIRSTNAME).with(C_FIRSTNAME, ALICE).with("LASTNAME", MILLER).with(C_AGE, 21).addTo(changer);
        changeRow("Address", C_ID).with(C_ID, 1).with(C_NUMBER, 21).addTo(changer);
        //Act
        final IDataSet dataSet = changer.build();
        //Assert
        ITable table = dataSet.getTable(T_PERSON);
        assertEquals(2, table.getRowCount());
        assertEquals(BOB, table.getValue(0, C_FIRSTNAME));
        assertEquals(MILLER, table.getValue(0, "LASTNAME"));
        assertEquals(21, table.getValue(0, C_AGE));
        assertEquals(ALICE, table.getValue(1, C_FIRSTNAME));
        assertEquals(MILLER, table.getValue(0, "LASTNAME"));
        assertEquals(23, table.getValue(1, C_AGE));

        table = dataSet.getTable(T_ADDRESS);
        assertEquals(1, table.getRowCount());
        assertEquals(MAIN_STREET, table.getValue(0, C_STREET));
        assertEquals(42, table.getValue(0, C_NUMBER));
    }
}
