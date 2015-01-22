package pl.klimczakowie.cpublication2.persistence;

import java.io.Serializable;

import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Criterion<T> implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(Criterion.class);
    private static final long serialVersionUID = 886260164142991815L;

    private volatile SingularAttribute<T, ?> column;
    private CriterionType type;
    private Object value;

    public Criterion() {
        super();
    }

    public Criterion(SingularAttribute<T, ?> column, CriterionType type, Object value) {
        super();
        this.column = column;
        this.type = type;
        setValue(value);
    }

    public SingularAttribute<T, ?> getColumn() {
        return column;
    }

    public void setColumn(SingularAttribute<T, ?> column) {
        this.column = column;
    }

    public CriterionType getType() {
        return type;
    }

    public void setType(CriterionType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (Long.class.equals(column.getType().getJavaType()) && value instanceof String) {
            value = Long.parseLong(value.toString());
        } else if (long.class.equals(column.getType().getJavaType()) && value instanceof String) {
            value = Long.parseLong(value.toString());
        } else if (Integer.class.equals(column.getType().getJavaType()) && value instanceof String) {
            value = Integer.parseInt(value.toString());
        } else if (int.class.equals(column.getType().getJavaType()) && value instanceof String) {
            value = Integer.parseInt(value.toString());
        }

        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Criterion other = (Criterion)obj;
        if (column == null) {
            if (other.column != null) {
                return false;
            }
        } else if (!column.equals(other.column)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Criterion [column=" + ((column != null) ? column.getName() : "") + "/" + ((column != null) ? column.getType() : "") + ", type=" + type + ", value=" + value + ", ofType=" + ((value != null) ? value.getClass().getCanonicalName() : "null") + "]";
    }
}
