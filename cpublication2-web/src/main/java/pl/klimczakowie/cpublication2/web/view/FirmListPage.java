package pl.klimczakowie.cpublication2.web.view;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Firm_;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.tooling.CriterionDTO;
import pl.klimczakowie.cpublication2.web.tooling.GenericSortableDataProvider;
import pl.klimczakowie.cpublication2.web.tooling.TableToModalProvider.ReRenderComponentProvider;
import pl.klimczakowie.cpublication2.web.view.base.BaseCriteriaTablePage;

public class FirmListPage extends BaseCriteriaTablePage<Firm, Firm_> {
    private static final long serialVersionUID = -2996360394306276097L;

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;

    private final FeedbackPanel feedback = new FeedbackPanel("feedback");
    static final List<String> COLUMNS = Arrays.asList("city", "name", "country", "postCode", "street");

    private static final Logger LOG = LoggerFactory.getLogger(FirmListPage.class);

    public FirmListPage() {
        super(null);
        add(new InputForm("sortForm"));
    }

    public FirmListPage(final PageParameters parameters) {
        super(parameters);
        add(new InputForm("sortForm"));
    }

    public FirmListPage(List<CriterionDTO<Firm, Firm_>> criterias) {
        super(null);
        getCriteria().get().clear();
        getCriteria().get().addAll(criterias);
        add(new InputForm("sortForm"));
    }

    @Override
    protected Cpublication2Service getPersistenceReference() {
        return persistenceReference;
    }

    @Override
    public Class<Firm_> getMetamodelClass() {
        return Firm_.class;
    }

    @Override
    public Class<Firm> getModelClass() {
        return Firm.class;
    }

    @Override
    protected int getRowsPerPage() {
        return 8;
    }

    @Override
    public SortParam<String> getDefaultSort() {
        return new SortParam<String>("name", true);
    }

    @Override
    protected Duration getRefreshDuration() {
        return Duration.seconds(60);
    }

    @Override
    public List<CriterionDTO<Firm, Firm_>> getDefaultCriteria() {
        return null;
    }

    @Override
    protected String getPageTitle() {
        return "Firms";
    }

    @Override
    protected List<IColumn<Firm, String>> getColumns() {
        List<IColumn<Firm, String>> columns = new ArrayList<IColumn<Firm, String>>();
        columns.add(new IDColumn(this, new Model<String>(" "), "name", "name"));
        return columns;
    }

    private final class IDColumn extends PropertyColumn<Firm, String> {
        private static final long serialVersionUID = 1369194420434628663L;
        private FirmListPage page;

        private IDColumn(FirmListPage firmListPage, Model<String> model, String field, String sortField) {
            super(model, field, sortField);
            this.page = firmListPage;
        }

        public void populateItem(Item<ICellPopulator<Firm>> cellItem, String componentId, final IModel<Firm> rowModel) {
            cellItem.add(new FirmListRowPanel<Firm>(componentId, persistenceReference, rowModel, page,"Firm details"));
        }
    }

    private class InputForm extends Form<CriterionDTO<Firm, Firm_>> {
        private static final long serialVersionUID = -6789693786570942596L;

        public InputForm(String id) {
            super(id, new CompoundPropertyModel<CriterionDTO<Firm, Firm_>>(new CriterionDTO<Firm, Firm_>(getModelClass(),
                    getMetamodelClass())));

            final DropDownChoice<String> orderChoice = new DropDownChoice<String>("column", COLUMNS, new IChoiceRenderer<String>() {
                private static final long serialVersionUID = 588971693306748479L;

                @Override
                public Object getDisplayValue(String object) {

                    if (object == "city") {
                        return "City";
                    } else if (object == "name") {
                        return "Company name";
                    } else if (object == "country") {
                        return "Country";
                    } else if (object == "postCode") {
                        return "Post Code";
                    } else if (object == "street") {
                        return "Street";
                    } else
                        throw new IllegalStateException(object + " is not mapped!");
                }

                @Override
                public String getIdValue(String object, int index) {
                    return object;
                }
            });

            add(orderChoice);
            orderChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                private static final long serialVersionUID = 4616716076442775227L;

                protected void onUpdate(AjaxRequestTarget target) {
                    CriterionDTO<Firm, Firm_> criterion = (CriterionDTO) getDefaultModelObject();
                    GenericSortableDataProvider<Firm, Firm_> dataProvider = (GenericSortableDataProvider<Firm, Firm_>) getCriteriaTable()
                            .getDataProvider();
                    dataProvider.setSort(new SortParam<String>(criterion.getColumn(), true));
                    ReRenderComponentProvider reRender = getRenderComponentProvider();
                    reRender.reRenderComponents(target);
                }
            });
            add(feedback.setOutputMarkupId(true));
        }

    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }
}
