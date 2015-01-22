/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.klimczakowie.cpublication2.web.view.base;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.web.tooling.CriteriaSessionContainer.CriteriaContainer;
import pl.klimczakowie.cpublication2.web.tooling.CriterionDTO;
import pl.klimczakowie.cpublication2.web.tooling.GenericSortableDataProvider;
import pl.klimczakowie.cpublication2.web.tooling.ModalWindowActionPanel;
import pl.klimczakowie.cpublication2.web.tooling.TableToModalProvider;
import pl.klimczakowie.cpublication2.web.tooling.TableToModalProvider.ReRenderComponentProvider;
import pl.klimczakowie.cpublication2.web.view.base.resources.StaticImage;

public abstract class BaseCriteriaTablePage<T extends Serializable, M> extends BasePage {
    private static final long serialVersionUID = -2996360394306276097L;

    private ReRenderComponentProvider reRender;

    private AjaxFallbackDefaultDataTable<T, String> table;

    private WebMarkupContainer criteriaWMC;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    @SuppressWarnings("rawtypes")
    public BaseCriteriaTablePage(final PageParameters parameters) {
        super(parameters);
        reRender = new ReRenderComponentProvider();

        // TABLE
        GenericSortableDataProvider<T, M> dataProvider = new GenericSortableDataProvider<T, M>(getPersistenceReference(), this);
        table = new AjaxFallbackDefaultDataTable<T, String>("table", getColumns(), dataProvider, getRowsPerPage());

        criteriaWMC = new WebMarkupContainer("criteriaWMC");
        criteriaWMC.setOutputMarkupId(true);
        criteriaWMC.add(new DataView<CriterionDTO>("criterionList", new CriteriaDataProvider()) {
            private static final long serialVersionUID = 2544212454413843168L;

            @SuppressWarnings("unchecked")
            @Override
            protected void populateItem(final Item<CriterionDTO> item) {
                final StaticImage pencil = new StaticImage("pencil", "pencil.png");

                item.add(new Label("displayName"));
                item.add(new Label("type"));
                item.add(new AjaxEditableLabel<Object>("stringValue") {
                    private static final long serialVersionUID = -8211904925503430617L;

                    @Override
                    protected void onSubmit(final AjaxRequestTarget target) {
                        super.onSubmit(target);
                        getCriteria().set(item.getModelObject());
                        reRender.reRenderComponents(target);
                    }
                    protected Component newLabel(final MarkupContainer parent, final String componentId,
                            final IModel<Object> model) {
                        pencil.add(new LabelAjaxBehavior(getLabelAjaxEvent()));
                        return super.newLabel(parent, componentId, model);
                    }
                });
                
                item.add(pencil);
                item.add(new CriteriaRemoveLink("remove", item.getModelObject()));
            }
        });

        reRender.addReRenderComponent(table, getRefreshDuration());
        reRender.addReRenderComponent(criteriaWMC);

        add(criteriaWMC);
        add(table);
        add(new ModalWindowActionPanel<Criterion<M>>("addCriterion", "Add new search criterion", new CriteriumToModalProvider(reRender)));
    }

    protected AjaxFallbackDefaultDataTable<T, String> getCriteriaTable() {
        return table;
    }

    protected abstract Duration getRefreshDuration();

    public abstract Class<M> getMetamodelClass();

    public abstract Class<T> getModelClass();

    protected abstract int getRowsPerPage();

    protected abstract Cpublication2Service getPersistenceReference();

    protected abstract List<IColumn<T, String>> getColumns();

    public abstract SortParam<String> getDefaultSort();

    public abstract List<CriterionDTO<T, M>> getDefaultCriteria();

    public ReRenderComponentProvider getRenderComponentProvider() {
        return reRender;
    }

    public CriteriaContainer<M> getCriteria() {
        return getSession().getCriteriaForPage(this.getClass(), getMetamodelClass());
    }

    @SuppressWarnings("rawtypes")
    private final class CriteriaDataProvider implements IDataProvider<CriterionDTO> {
        private static final long serialVersionUID = -3911591355302109185L;

        @Override
        public void detach() {
            // no-op
        }

        @Override
        public Iterator<? extends CriterionDTO> iterator(long first, long count) {
            return getCriteria().get().iterator();
        }

        @Override
        public long size() {
            return getCriteria().get().size();
        }

        @Override
        public IModel<CriterionDTO> model(CriterionDTO object) {
            return new CompoundPropertyModel<CriterionDTO>(object);
        }
    }

    private final class CriteriaRemoveLink extends AjaxFallbackLink<Object> {
        private static final long serialVersionUID = 5696038474195813909L;
        private CriterionDTO<T, M> criterionDTO;

        private CriteriaRemoveLink(String id, CriterionDTO<T, M> criterionDTO) {
            super(id);
            this.criterionDTO = criterionDTO;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            getCriteria().remove(criterionDTO);
            reRender.reRenderComponents(target);
        }
    }

    private final class CriteriumToModalProvider extends TableToModalProvider<Criterion<T>> {
        private static final long serialVersionUID = 1L;

        private CriteriumToModalProvider(IModel<Criterion<T>> model, ReRenderComponentProvider rrp) {
            super(model, rrp);
        }

        private CriteriumToModalProvider(ReRenderComponentProvider rrp) {
            super(rrp);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Page constructPage() {
            return new CriterionPanel<T, M>(this, (BaseCriteriaTablePage<T, M>) getPage());
        }

        @Override
        public String getShownValue() {
            return "Add search criterion";
        }

        @Override
        protected Criterion<T> constructNewObject() {
            return new Criterion<T>();
        }
    }
}
