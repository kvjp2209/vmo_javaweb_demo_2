package com.vmo.core.repositories.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.type.TimestampType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

public class LocalDateTimeUserType implements EnhancedUserType, ParameterizedType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.TIMESTAMP};
    }

    @Override
    public Class returnedClass() {
        return LocalDateTime.class;
    }

    @Override
    public boolean equals(Object o1, Object o2) throws HibernateException {
        return Objects.equals(o1, o2);
    }

    @Override
    public int hashCode(Object obj) throws HibernateException {
        return obj.hashCode();
    }

    @Override
    public Object nullSafeGet(
            ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object owner
    ) throws HibernateException, SQLException {
        return TimestampType.INSTANCE.nullSafeGet(resultSet, names, session, owner);
    }

    @Override
    public void nullSafeSet(
            PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session
    ) throws HibernateException, SQLException {
        if (value != null) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            Timestamp timestamp = new Timestamp(localDateTime.toDate().getTime());
            TimestampType.INSTANCE.nullSafeSet(preparedStatement, timestamp, index, session);
        } else {
            TimestampType.INSTANCE.nullSafeSet(preparedStatement, null, index, session);
        }
    }

    @Override
    public Object deepCopy(Object obj) throws HibernateException {
        return obj;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object obj) throws HibernateException {
        Serializable result;
        if (obj == null) {
            result = null;
        } else {
            Object deepCopy = this.deepCopy(obj);
            if (!(deepCopy instanceof Serializable)) {
                throw new SerializationException(String.format("deepCopy of %s is not serializable", obj), null);
            }

            result = (Serializable) deepCopy;
        }

        return result;
    }

    @Override
    public Object assemble(Serializable cachedValue, Object owner) throws HibernateException {
        return deepCopy(cachedValue);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public String objectToSQLString(Object obj) {
        return TimestampType.INSTANCE.toString();
    }

    @Override
    public String toXMLString(Object value) {
        return value.toString();
    }

    @Override
    public Object fromXMLString(String value) {
        //TODO make shared logic with object mapper
        return LocalDateTime.parse(value);
    }

    @Override
    public void setParameterValues(Properties parameters) {

    }
}
