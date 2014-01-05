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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Class for creating Objects, so that we get a more compact code.
 * @author niels (linux-java AT users.sourceforge.net)
 * @author Last changed by: niels
 * @version 03.01.2014
 * @since 2.4.10
 *
 */
public class ObjectFactory {

    private ObjectFactory() {
        //Hidden Constructor.
    }

    /**
     * Converts a string in JDBC date escape format to
     * a <code>Date</code> value.
     *
     * @param s a <code>String</code> object representing a date in
     *        in the format "yyyy-mm-dd"
     * @return a <code>java.sql.Date</code> object representing the
     *         given date
     * @throws IllegalArgumentException if the date given is not in the
     *         JDBC date escape format (yyyy-mm-dd)
     */
    public static Date d(String date) {
            return Date.valueOf(date);
    }

    /**
     * Converts a <code>String</code> object in JDBC timestamp escape format to a
     * <code>Timestamp</code> value.
     *
     * @param s timestamp in format <code>yyyy-mm-dd hh:mm:ss[.f...]</code>.  The
     * fractional seconds may be omitted.
     * @return corresponding <code>Timestamp</code> value
     * @exception java.lang.IllegalArgumentException if the given argument
     * does not have the format <code>yyyy-mm-dd hh:mm:ss[.f...]</code>
     */
    public static Timestamp ts(String time) {
            return Timestamp.valueOf(time);
    }

    /**
     * Converts a string in JDBC time escape format to a <code>Time</code> value.
     *
     * @param s time in format "hh:mm:ss"
     * @return a corresponding <code>Time</code> object
     */
    public static Time t(String time) {
            return Time.valueOf(time);
    }

    public static Double d(double d) {
        return Double.valueOf(d);
    }

    public static Double d(long d) {
        return Double.valueOf(d);
    }

    public static Float f(float f) {
        return Float.valueOf(f);
    }

    public static Float f(long f) {
        return Float.valueOf(f);
    }

    public static Long l(long l) {
        return Long.valueOf(l);
    }

    public static Integer i(int i) {
        return Integer.valueOf(i);
    }

    public static BigInteger bi(String bi) {
        return new BigInteger(bi);
    }

    public static BigDecimal bd(String bd) {
        return new BigDecimal(bd);
    }

    public static BigInteger bi(long bi) {
        return BigInteger.valueOf(bi);
    }

    public static BigDecimal bd(double bd) {
        return BigDecimal.valueOf(bd);
    }

}
