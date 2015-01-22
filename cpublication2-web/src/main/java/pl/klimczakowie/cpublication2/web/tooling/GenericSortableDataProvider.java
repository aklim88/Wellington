package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.persistence.SortOrder;
import pl.klimczakowie.cpublication2.persistence.SortOrder.SortOrderType;
import pl.klimczakowie.cpublication2.web.view.base.BaseCriteriaTablePage;

/**
 * Generic sortable data provider for HTML table element.
 * 
 * @author Agnieszka Klimczak
 * @param <T> Data Provider type- entity.
 */
public class GenericSortableDataProvider<T extends Serializable, M> extends SortableDataProvider<T, String> {
    private static final transient Log LOG = LogFactory.getLog(GenericSortableDataProvider.class);
    private static final long serialVersionUID = -7825094849232677107L;
    private static final long MAX = 1000;

    private Cpublication2Service persistenceReference;
    private BaseCriteriaTablePage<T, M> basePage;

    public GenericSortableDataProvider(Cpublication2Service persistenceReference, BaseCriteriaTablePage<T, M> basePage) {

        this.persistenceReference = persistenceReference;
        this.basePage = basePage;

        setSort(basePage.getDefaultSort());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Iterator<T> iterator(long first, long count) {
  
        SortParam<String> sp = getSort();
        SortOrder orderBy = new SortOrder(sp.getProperty(), sp.isAscending() ? SortOrderType.ASC : SortOrderType.DESC);

        List<Criterion<T>> andCriterias = new ArrayList<Criterion<T>>();
        for (CriterionDTO criterion : basePage.getCriteria().get()) {
            if (criterion.getColumn() != null) {
                andCriterias.add(criterion.toCriterion());
            }
        }

        if (basePage.getDefaultCriteria() != null) {
            for (CriterionDTO criterion : basePage.getDefaultCriteria()) {
                if (criterion.getColumn() != null) {
                    andCriterias.add(criterion.toCriterion());
                }
            }
        }

        return persistenceReference.pobierzEncje(basePage.getModelClass(), orderBy, andCriterias, first, MAX).iterator();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public long size() {
        List<Criterion<T>> andCriterias = new ArrayList<Criterion<T>>();
        for (CriterionDTO criterion : basePage.getCriteria().get()) {
            if (criterion.getColumn() != null) {
                andCriterias.add(criterion.toCriterion());
            }
        }

        if (basePage.getDefaultCriteria() != null) {
            for (CriterionDTO criterion : basePage.getDefaultCriteria()) {
                if (criterion.getColumn() != null) {
                    andCriterias.add(criterion.toCriterion());
                }
            }
        }

        return persistenceReference.zliczEncje(basePage.getModelClass(), andCriterias);
    }

    public IModel<T> model(T im) {
        return new Model<T>(im);
    }

    public void detach() {
        // no-op
    }

}
