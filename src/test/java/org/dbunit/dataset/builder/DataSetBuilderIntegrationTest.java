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

import static org.dbunit.dataset.builder.DataSetBuilder.newBasicRow;
import static org.dbunit.dataset.builder.DataSetBuilderIntegrationTest.PERSONRowBuilder.newPERSON;
import static org.dbunit.dataset.builder.ObjectFactory.*;

import java.sql.Date;
import java.util.TimeZone;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the class {@link DataSetBuilder}.
 * @author niels (linux-java AT users.sourceforge.net)
 * @author Last changed by: niels
 * @version 02.01.2014
 * @since 2.4.10
 *
 */
public class DataSetBuilderIntegrationTest {


    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.dbunit.dataset.builder.DataSetBuilder#build()}.
     */
    @Test
    public void testBuild() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-1"));
        DataSetBuilder builder = new DataSetBuilder();
        newPERSON().NAME("Bob").BIRTHPLACE("NEW YORK").addTo(builder);
        newPERSON().NAME("Alice").BIRTHPLACE("London").addTo(builder);
        newBasicRow("ADDRESS").with("STREET", "Main Street").with("NUMBER", 42).addTo(builder);

        final IDataSet actual = builder.build();

        ReplacementDataSet expected = new ReplacementDataSet(new FlatXmlDataSetBuilder().build(
                this.getClass().getResourceAsStream("/reference.xml")));
        expected.addReplacementObject("[NULL]", null);

        Assertion.assertEquals(expected, actual);
    }

    static class PERSONRowBuilder extends BasicDataRowBuilder {

        public static final String TABLE_NAME = "PERSON";

        public static final String C_DATE_OF_BIRTH = "DATE_OF_BIRTH";
        public static final String C_BIRTHPLACE = "BIRTPLACE";
        public static final String C_SEX = "SEX";
        public static final String C_ID = "ID";
        public static final String C_NAME = "NAME";
        public static final String C_VERSION = "VERSION";
        public static final String C_FIRSTNAME = "FIRSTNAME";

        public static final String[] PRIMARY_KEY = {C_ID};

        public static final String[] ALL_COLUMNS = {C_DATE_OF_BIRTH, C_BIRTHPLACE,
            C_SEX, C_ID, C_VERSION, C_NAME, C_FIRSTNAME};

        public PERSONRowBuilder(String... identifierColumns) {
            super(TABLE_NAME, identifierColumns);
            setAllColumnNames(ALL_COLUMNS);
            addDefaultValue(C_DATE_OF_BIRTH, d("1970-01-01"));
            addDefaultValue(C_NAME, "");
            addDefaultValue(C_VERSION, new Long("0"));
            addDefaultValue(C_FIRSTNAME, "");
            addDefaultValue(C_ID, new Long("0"));
            addDefaultValue(C_BIRTHPLACE, "");
        }

        public final PERSONRowBuilder DATE_OF_BIRTH (Date value) {
            with(C_DATE_OF_BIRTH, value);
            return this;
        }

        public final PERSONRowBuilder BIRTHPLACE (String value) {
            with(C_BIRTHPLACE, value);
            return this;
        }



        public final PERSONRowBuilder SEX (Integer value) {
            with(C_SEX, value);
            return this;
        }


        public final PERSONRowBuilder ID (Long value) {
            with(C_ID, value);
            return this;
        }



        public final PERSONRowBuilder NAME (String value) {
            with(C_NAME, value);
            return this;
        }


        public final PERSONRowBuilder VERSION (Long value) {
            with(C_VERSION, value);
            return this;
        }


        public final PERSONRowBuilder FIRSTNAME (String value) {
            with(C_FIRSTNAME, value);
            return this;
        }


        public static PERSONRowBuilder newPERSON() {
            return new PERSONRowBuilder(PRIMARY_KEY);
        }

        public static PERSONRowBuilder newPERSON(String... identifierColumns) {
            return new PERSONRowBuilder(identifierColumns);
        }

    }


}
