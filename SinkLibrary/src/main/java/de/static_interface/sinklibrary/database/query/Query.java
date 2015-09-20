/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.database.query;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.Row;
import de.static_interface.sinklibrary.database.query.condition.EqualsCondition;
import de.static_interface.sinklibrary.database.query.condition.GreaterThanCondition;
import de.static_interface.sinklibrary.database.query.condition.GreaterThanEqualsCondition;
import de.static_interface.sinklibrary.database.query.condition.LikeCondition;
import de.static_interface.sinklibrary.database.query.condition.WhereCondition;
import de.static_interface.sinklibrary.database.query.impl.FromQuery;
import de.static_interface.sinklibrary.database.query.impl.LimitQuery;
import de.static_interface.sinklibrary.database.query.impl.OrderByQuery;

import javax.annotation.Nonnull;

public abstract class Query<T> {

    private Query child;
    private Query parent;

    public Query(Query parent) {
        this.parent = parent;
    }

    /**
     * Start a new query
     * @param table the table on which the query is executed on
     * @return a new query
     */
    public static <T extends Row> FromQuery<T> from(AbstractTable<T> table) {
        return new FromQuery(table);
    }

    /**
     * Check if the given column value equals the given object
     * @param o the object to check. Strings will be SQL escaped. <b>You don't need to put "'s at the start and end of strings!</b>
     */
    public static EqualsCondition eq(Object o) {
        return new EqualsCondition(o);
    }

    public static <T extends WhereCondition> T not(T condition) {
        condition.setNegated(!condition.isNegated());
        return condition;
    }

    /**
     * Check if the given column value is greater than the given object
     * @param o the object to check. Strings will be SQL escaped. <b>You don't need to put "'s at the start and end of strings!</b>
     */
    public static GreaterThanCondition gt(Object o) {
        return new GreaterThanCondition(o);
    }

    /**
     * Check if the given column value is less than the given object
     * @param o the object to check. Strings will be SQL escaped. <b>You don't need to put "'s at the start and end of strings!</b>
     */
    public static GreaterThanCondition lt(Object o) {
        GreaterThanCondition condition = new GreaterThanCondition(o);
        condition.setInverted(true);
        return condition;
    }

    /**
     * Check if the given column value is great than or equal the given object
     * @param o the object to check. Strings will be SQL escaped. <b>You don't need to put "'s at the start and end of strings!</b>
     */
    public static GreaterThanEqualsCondition gte(Object o) {
        return new GreaterThanEqualsCondition(o);
    }

    /**
     * Check if the given column value is less than or equal the given object
     * @param o the object to check. Strings will be SQL escaped. <b>You don't need to put "'s at the start and end of strings!</b>
     */
    public static GreaterThanEqualsCondition lte(Object o) {
        GreaterThanEqualsCondition condition = new GreaterThanEqualsCondition(o);
        condition.setInverted(true);
        return condition;
    }

    /**
     * Check if the given column value is less than or equal the given object
     * @param pattern the SQL wildcard pattern. Strings will SQL escaped. <b>You don't need to put "'s at the start and end of the pattern!</b>
     */
    public static LikeCondition like(String pattern) {
        return new LikeCondition(pattern);
    }

    public Query<T> getChild() {
        return child;
    }

    protected void setChild(Query query) {
        this.child = query;
    }

    public Query<T> getParent() {
        return parent;
    }

    /**
     * Set the SQL <code>LIMIT</code> for the query
     * @param rowCount the maximal row count
     */
    public LimitQuery<T> limit(int rowCount) {
        return limit(0, rowCount);
    }

    /**
     * Set the SQL <code>LIMIT</code> for the query
     * @param rowCount the maximal row count
     * @param offset the offset for the result
     */
    public LimitQuery<T> limit(int offset, int rowCount) {
        LimitQuery<T> query = new LimitQuery(this, offset, rowCount);
        setChild(query);
        return query;
    }

    /**
     * Orders the result
     * @param column the column which is used for ordering
     * @param order the order type
     */
    public OrderByQuery<T> orderBy(String column, Order order) {
        OrderByQuery<T> query = new OrderByQuery(this, column, order);
        setChild(query);
        return query;
    }

    /**
     * Get the Result as {@link T}[] array
     * @param bindings the SQL bindings
     */
    @Nonnull
    public T[] getResults(Object... bindings) {
        return (T[]) getFromQuery().getTable().get(toSql(), bindings);
    }

    /**
     * Convert the query to an SQL query
     * @return the query as sql query
     */
    public String toSql() {
        return getFromQuery().getTable().getDatabase().buildQuery(getMasterQuery());
    }

    private FromQuery<T> getFromQuery() {
        Query<T> q = this;
        while (q != null) {
            if (q instanceof FromQuery) {
                break;
            }
            q = q.getParent();
        }

        if (q == null || !(q instanceof FromQuery)) {
            throw new IllegalStateException("Query is not a from() query!");
        }

        return (FromQuery<T>) q;
    }

    /**
     * Get the query result as {@link T}
     * @param bindings the SQL bindings
     */
    @Nonnull
    public T get(Object... bindings) {
        return getResults(bindings)[0];
    }

    public Query<T> getMasterQuery() {
        if (getFromQuery() != null) {
            return getFromQuery();
        }

        //todo: also include UpdateQueries

        throw new IllegalStateException("Master query type not found");
    }
}
