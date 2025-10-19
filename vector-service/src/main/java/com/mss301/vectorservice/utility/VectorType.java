package com.mss301.vectorservice.utility;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

public class VectorType implements UserType<Double[]> {

    @Override
    public int getSqlType() {
        // PostgreSQL pgvector uses the "OTHER" SQL type
        return Types.OTHER;
    }

    @Override
    public Class<Double[]> returnedClass() {
        return Double[].class;
    }

    @Override
    public boolean equals(Double[] x, Double[] y) throws HibernateException {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(Double[] x) throws HibernateException {
        return Arrays.hashCode(x);
    }

    @Override
    public Double[] nullSafeGet(ResultSet rs, int position,
                                SharedSessionContractImplementor session,
                                Object owner) throws SQLException {
        String vectorString = rs.getString(position);
        if (vectorString == null) {
            return null;
        }

        vectorString = vectorString.replace("[", "").replace("]", "");
        String[] parts = vectorString.split(",");
        Double[] result = new Double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Double[] value, int index,
                            SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i < value.length - 1) sb.append(",");
        }
        sb.append("]");

        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        pgObject.setValue(sb.toString());
        st.setObject(index, pgObject);
    }

    @Override
    public Double[] deepCopy(Double[] value) throws HibernateException {
        if (value == null) return null;
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Double[] value) throws HibernateException {
        return deepCopy(value);
    }

    @Override
    public Double[] assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy((Double[]) cached);
    }

    @Override
    public Double[] replace(Double[] detached, Double[] managed, Object owner)
            throws HibernateException {
        return deepCopy(detached);
    }
}
