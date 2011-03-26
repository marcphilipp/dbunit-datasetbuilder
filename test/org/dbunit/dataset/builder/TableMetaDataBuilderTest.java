package org.dbunit.dataset.builder;

import static org.junit.Assert.*;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Test;

public class TableMetaDataBuilderTest {

	private TableMetaDataBuilder builder = new TableMetaDataBuilder("MY_TABLE_NAME", new CaseInsensitiveStringPolicy());
	
	@Test
	public void isEmptyInTheBeginning() throws Exception {
		assertEquals(0, builder.numberOfColumns());
		Column[] columns = builder.build().getColumns();
		assertEquals(0, columns.length);
	}

	@Test
	public void addsSingleColumn() throws Exception {
		Column column = new Column("someColumn", DataType.CHAR);
		
		builder.with(column);
		
		assertEquals(1, builder.numberOfColumns());
		Column[] columns = builder.build().getColumns();
 		assertEquals(1, columns.length);
		assertEquals(column, columns[0]);
	}

	@Test
	public void preservesExistingColumn() throws Exception {
		Column originalColumn = new Column("someColumn", DataType.CHAR);
		Column sameColumnDifferentType = new Column("someColumn", DataType.INTEGER);

		builder.with(originalColumn).with(sameColumnDifferentType);
		
		assertEquals(1, builder.numberOfColumns());
		Column[] columns = builder.build().getColumns();
		assertEquals(1, columns.length);
		assertEquals(originalColumn, columns[0]);
	}

	@Test
	public void addsAllColumnsOfTableMetaData() throws Exception {
		Column existingColumn = new Column("existingColumn", DataType.CHAR);
		Column newColumn = new Column("newColumn", DataType.INTEGER);
		Column anotherNewColumn = new Column("anotherNewColumn", DataType.INTEGER);
		ITableMetaData metaData = new DefaultTableMetaData("MY_TABLE_NAME", new Column[]{ newColumn, anotherNewColumn });
		
		builder.with(existingColumn).with(metaData);
		
		assertEquals(3, builder.numberOfColumns());
		Column[] columns = builder.build().getColumns();
		assertEquals(3, columns.length);
		assertEquals(existingColumn, columns[0]);
		assertEquals(newColumn, columns[1]);
		assertEquals(anotherNewColumn, columns[2]);
	}
}
