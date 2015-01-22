package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.klimczakowie.cpublication2.web.view.base.BaseCleanPage;

public class CriteriaSessionContainer implements Serializable {
    private static final long serialVersionUID = -1048741552647840603L;
    private static final transient Log LOG = LogFactory.getLog(CriteriaSessionContainer.class);

    private Map<Class<? extends BaseCleanPage>, CriteriaContainer<?>> criteria = new HashMap<Class<? extends BaseCleanPage>, CriteriaSessionContainer.CriteriaContainer<?>>();

    @SuppressWarnings("unchecked")
    public <T> CriteriaContainer<T> getCriteriaForPage(Class<? extends BaseCleanPage> page, Class<T> criteriaMetaModel) {
        if (!criteria.containsKey(page)) {
            synchronized (this) {
                if (!criteria.containsKey(page)) {
                    LOG.debug("Creating new CriteriaContainer for page: " + page.getCanonicalName());
                    criteria.put(page, new CriteriaContainer<T>(criteriaMetaModel));
                }
            }
        }
        return (CriteriaContainer<T>)criteria.get(page);
    }

    public static class CriteriaContainer<T> implements Serializable {
        private static final long serialVersionUID = 2306903222200593770L;
        private List<CriterionDTO> criteria = new ArrayList<CriterionDTO>();
        private Class<T> criteriaMetaModel;

        public CriteriaContainer(Class<T> criteriaMetaModel) {
            this.criteriaMetaModel = criteriaMetaModel;
        }

        public void set(CriterionDTO criterion) {
            if (criteria.contains(criterion)) {
                LOG.info("Replaced criterion: " + criterion);
                criteria.get(criteria.indexOf(criterion)).setValue(criterion.getValue());
            } else {
                LOG.info("Set criterion: " + criterion);
                criteria.add(criterion);
            }
            LOG.info("Has: " + criteria.size() + " criteria");
        }

        public void remove(CriterionDTO criterion) {
            if (criteria.contains(criterion)) {
                LOG.info("Removed criterion: " + criterion);
                criteria.remove(criterion);
            }
        }

        public List<CriterionDTO> get() {
            return criteria;
        }

        public Class<T> getCriteriaMetaModel() {
            return criteriaMetaModel;
        }
    }
}
