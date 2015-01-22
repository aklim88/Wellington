package pl.klimczakowie.cpublication2.persistence.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Firm_;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.persistence.CriterionType;
import pl.klimczakowie.cpublication2.persistence.SortOrder;
import pl.klimczakowie.cpublication2.persistence.SortOrder.SortOrderType;

public class Cpublication2ServiceImpl implements Cpublication2Service {
    private final static Logger LOG = LoggerFactory.getLogger(Cpublication2ServiceImpl.class);

    private EntityManager entityManager;

    public void init() {
    }

    @Override
    public <T> List<T> pobierzEncje(Class<T> c, SortOrder orderBy, List<Criterion<T>> andCriterias) {
        return pobierzEncje(c, orderBy, andCriterias, null, null);
    }

    /**
     * Return entities on selected criteria and sort order
     * @param orderBy
     * @param andCriterias
     * @return
     */
    @Override
    public <T> List<T> pobierzEncje(Class<T> c, SortOrder orderBy, List<Criterion<T>> andCriterias, Long first, Long max) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(c);
        Root<T> root = criteriaQuery.from(c);
        criteriaQuery.select(root);

        criteriaQuery.where(buildAndPredicates(andCriterias, root, builder));

        if (orderBy != null) {
            criteriaQuery.orderBy(buildOrderBy(orderBy, root, builder));
        }

        TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);
        if (first != null) {
            query.setFirstResult(new Long(first).intValue());
        }
        if (max != null) {
            query.setMaxResults(new Long(max).intValue());
        }
        return query.getResultList();
    }

    @Override
    public <T> long zliczEncje(Class<T> c, List<Criterion<T>> andCriterias) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(c);
        CriteriaQuery<Long> selectQuery = criteriaQuery.select(builder.count(root));
        selectQuery.where(buildAndPredicates(andCriterias, root, builder));

        return getEntityManager().createQuery(selectQuery).getSingleResult();
    }

	@Override
	public Firm findFirmForUser(String username, String password) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Firm> criteriaQuery = builder.createQuery(Firm.class);
        Root<Firm> root = criteriaQuery.from(Firm.class);
        criteriaQuery.where(builder.and(builder.equal(root.get(Firm_.login), username), builder.equal(root.get(Firm_.passwd), password)));

        return getEntityManager().createQuery(criteriaQuery).getSingleResult();
	}

    private <T> Order buildOrderBy(SortOrder sortOrder, Path<T> path, CriteriaBuilder builder) {
        if (sortOrder.getType() == SortOrderType.ASC) {
            return builder.asc(path.get(sortOrder.getColumn()));
        }
        return builder.desc(path.get(sortOrder.getColumn()));
    }

    @SuppressWarnings("unchecked")
    private <T> Predicate buildAndPredicates(List<Criterion<T>> criterias, Root<T> root, CriteriaBuilder builder) {
        if (criterias == null) {
            return null;
        }

        List<Predicate> searchPredicates = new ArrayList<Predicate>();
        for (Criterion<T> criteria : criterias) {
            
            Path<?> col = root.get(criteria.getColumn());

            if (criteria.getType() == CriterionType.LIKE) {
                searchPredicates.add(builder.like((Expression<String>) col, criteria.getValue().toString()));
            } else if (criteria.getType() == CriterionType.EQUAL) {
                searchPredicates.add(builder.equal(col, criteria.getValue()));
            } else if (criteria.getType() == CriterionType.NOT_EQUAL) {
                searchPredicates.add(builder.notEqual(col, criteria.getValue()));
            } else if (criteria.getType() == CriterionType.NULL) {
                searchPredicates.add(builder.isNull(col));
            } else if (criteria.getType() == CriterionType.NOT_NULL) {
                searchPredicates.add(builder.isNotNull(col));
            } else if (criteria.getType() == CriterionType.LOWER_OR_EQUAL_THAN && Long.getLong(criteria.getValue().toString()) != null) {
                searchPredicates.add(builder.le((Expression<? extends Number>) col, (Number) criteria.getValue()));
            } else if (criteria.getType() == CriterionType.GRATER_OR_EQUAL_THAN && Long.getLong(criteria.getValue().toString()) != null) {
                searchPredicates.add(builder.ge((Expression<? extends Number>)col, (Number)criteria.getValue()));
            } else if (criteria.getType() == CriterionType.LOWER_OR_EQUAL_THAN && criteria.getValue().getClass() == Date.class) {
            	searchPredicates.add(builder.lessThanOrEqualTo((Path<Date>)col, (Date)criteria.getValue()));
            } else if (criteria.getType() == CriterionType.GRATER_OR_EQUAL_THAN && criteria.getValue().getClass() == Date.class) {
            	searchPredicates.add(builder.greaterThanOrEqualTo((Path<Date>)col, (Date)criteria.getValue()));
            }
        }

        return builder.and(searchPredicates.toArray(new Predicate[searchPredicates.size()]));
    }

    @Override
    public <T> T pobierzEncje(Class<T> entityClass, Serializable primaryKey) {
        return getEntityManager().find(entityClass, primaryKey);
    }

    @Override
    public <T> T zapiszEncje(T object) {
        return getEntityManager().merge(object);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
