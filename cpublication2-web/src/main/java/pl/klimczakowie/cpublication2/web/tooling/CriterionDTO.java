package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.klimczakowie.cpublication2.model.NamedCriteria;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.persistence.CriterionType;

public class CriterionDTO<T, M> implements Serializable {
    private static final String DATE_CRITERION_FORMAT = "dd-MM-yyyy";
	private static final transient Log LOG = LogFactory.getLog(CriterionDTO.class);
    private static final long serialVersionUID = 4099838625085590709L;

    private String displayName = null;
    private String column = null;
    private String type = null;
    private Object value = null;
    private Class<T> entityClass;
    private Class<M> metamodel;

    public CriterionDTO(Class<T> entityClass, Class<M> metamodel) {
        this.entityClass = entityClass;
        this.metamodel = metamodel;
    }

    public CriterionDTO(Criterion<T> object, Class<T> entityClass, Class<M> metamodel) {
        this(entityClass, metamodel);
        if (object.getColumn() != null) {
            column = object.getColumn().getName();
            try {
				NamedCriteria namedCriteria = entityClass.getDeclaredField(column).getAnnotation(NamedCriteria.class);
				displayName = namedCriteria.value();
			} catch (Exception e) {
				LOG.info("Field: " + column + " in type: " + entityClass.getCanonicalName() + " has no " + NamedCriteria.class.getCanonicalName() + " annotation");
			}
        }

        if (object.getType() != null) {
            type = object.getType().value();
        }

        if (object.getValue() != null) {
            value = object.getValue();
        }

    }

    @SuppressWarnings("unchecked")
    public Criterion<T> toCriterion() {
        Criterion<T> criterion = new Criterion<T>();
        try {
            if (metamodel.getAnnotation(StaticMetamodel.class) == null) {
                throw new IllegalArgumentException("Podana klasa (metamodel): " + metamodel.getCanonicalName() + " nie jest metamodelem encji!");
            }

            if (entityClass.getAnnotation(Entity.class) == null) {
                throw new IllegalArgumentException("Podana klasa (entityClass): " + entityClass.getCanonicalName() + " nie jest encją!");
            }

            criterion.setColumn((SingularAttribute<T, ?>)metamodel.getField(column).get(null));
            criterion.setType(CriterionType.getCriterionType(type));
            criterion.setValue(value);

            LOG.debug("Zbudowano kryterium: " + criterion);

            return criterion;
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        //FIXME debug
        LOG.info("Ustawiono wartość typu: " + ((value != null) ? value.getClass().getCanonicalName() : "null") + " na : " + this);
        this.value = value;
    }
    
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "CriterionDTO [displayName=" + displayName + ", column="
				+ column + ", type=" + type + ", value=" + value
				+ ", entityClass=" + entityClass + ", metamodel=" + metamodel
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((entityClass == null) ? 0 : entityClass.hashCode());
		result = prime * result
				+ ((metamodel == null) ? 0 : metamodel.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CriterionDTO other = (CriterionDTO) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (entityClass == null) {
			if (other.entityClass != null)
				return false;
		} else if (!entityClass.equals(other.entityClass))
			return false;
		if (metamodel == null) {
			if (other.metamodel != null)
				return false;
		} else if (!metamodel.equals(other.metamodel))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getStringValue() {
		if (Date.class.equals(value.getClass())) {
			return new SimpleDateFormat(DATE_CRITERION_FORMAT).format(value);
		}
		return value.toString();
	}

	public void setStringValue(String stringValue) throws ParseException {
		if (Date.class.equals(value.getClass())) {
			value = new SimpleDateFormat(DATE_CRITERION_FORMAT).parse(stringValue);
		} else if (Integer.class.equals(value.getClass())) { 
			value = Integer.parseInt(stringValue);
		} else if (Long.class.equals(value.getClass())) { 
			value = Long.parseLong(stringValue);
		} else {
			value = stringValue;
		}
	}

	
}
