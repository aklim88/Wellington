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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public abstract class BaseChosableCriteriaTablePage<T extends Serializable, M> extends BaseCriteriaTablePage<T, M> {
    private static final long serialVersionUID = -2996360394306276097L;

    private List<T> chosedElements = new ArrayList<T>();
    private WebMarkupContainer chosedElementsWMC;
    private Label chosedElementsTitle;

    public BaseChosableCriteriaTablePage(final PageParameters parameters) {
        super(parameters);

        chosedElementsWMC = new WebMarkupContainer("chosedElementsWMC");
        chosedElementsWMC.add(new PropertyListView<T>("chosedElements", chosedElements) {
            private static final long serialVersionUID = -3643550819063901559L;

            @Override
            protected void populateItem(ListItem<T> listItem) {
                getChoseableItem(listItem);
            }

        });
        chosedElementsWMC.setOutputMarkupId(true);

        chosedElementsTitle = new Label("chosedElementsTitle", "Wybrane elementy:");
        chosedElementsTitle.setOutputMarkupId(true);
        chosedElementsTitle.setOutputMarkupPlaceholderTag(true);
        chosedElementsTitle.setVisible(false);

        add(chosedElementsWMC);
        add(chosedElementsTitle);
    }

    public List<T> getChosedElements() {
        return chosedElements;
    }

    public void updatePage(AjaxRequestTarget target) {
        target.add(getCriteriaTable());
        target.add(chosedElementsWMC);
    }

    @Override
    protected List<IColumn<T, String>> getColumns() {
        List<IColumn<T, String>> columns = new ArrayList<IColumn<T, String>>();

        columns.add(new AbstractColumn<T, String>(new Model<String>("X")) {
            private static final long serialVersionUID = 3681674256183152440L;

            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(new CheckBoxPanel(componentId, rowModel));
            }

        });

        addMoreColumns(columns);

        return columns;
    }

    protected abstract void getChoseableItem(ListItem<T> listItem);

    protected void addMoreColumns(List<IColumn<T, String>> columns) {
        columns.add(new PropertyColumn<T, String>(new Model<String>("ID"), "id", "id"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("UTI"), "uti", "uti") {
            private static final long serialVersionUID = -3263298074133814435L;

            @Override
            public String getCssClass() {
                return "highlined";
            }
        });
        columns.add(new PropertyColumn<T, String>(new Model<String>("LEI"), "lei", "lei"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("Zawarta"), "dataZawarcia", "dataZawarcia") {
            private static final long serialVersionUID = -1514962753504611337L;

            public void populateItem(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> rowModel) {
                Date data = (Date)getDataModel(rowModel).getObject();
                String value = (data != null) ? new SimpleDateFormat("yyyy-MM-dd").format(data) : "";
                item.add(new Label(componentId, value));
            }
        });
        columns.add(new PropertyColumn<T, String>(new Model<String>("Modyfikacja"), "dataModyfikacji", "dataModyfikacji") {
            private static final long serialVersionUID = -1514962753504611333L;

            public void populateItem(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> rowModel) {
                Date data = (Date)getDataModel(rowModel).getObject();
                String value = (data != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data) : "";
                item.add(new Label(componentId, value));
            }
        });
        columns.add(new PropertyColumn<T, String>(new Model<String>("Gen."), "generator", "generator"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("Kod kontr."), "kodKontrahenta", "kodKontrahenta"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("Nazwa kontr."), "nazwaKontrahenta", "nazwaKontrahenta"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("ID prod."), "idProduktu", "idProduktu"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("Strony"), "strony", "strony"));
        columns.add(new PropertyColumn<T, String>(new Model<String>("Akcja"), "akcjaNaTransakcji", "akcjaNaTransakcji"));
    }

    protected abstract void choosedChanged(AjaxRequestTarget target);

    public class CheckBoxPanel extends Panel implements IMarkupResourceStreamProvider {
        private static final long serialVersionUID = 3296006488095809917L;

        public CheckBoxPanel(String id, final IModel<T> rowModel) {
            super(id);
            add(new AjaxCheckBox("checkbox", new Model<Boolean>(chosedElements.contains(rowModel.getObject()))) {
                private static final long serialVersionUID = 587128956898204225L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    if (getModel().getObject()) {
                        chosedElements.add(rowModel.getObject());
                    } else {
                        chosedElements.remove(rowModel.getObject());
                    }
                    target.add(chosedElementsWMC);
                    choosedChanged(target);

                    chosedElementsTitle.setVisible(chosedElements.size() != 0);
                    chosedElementsTitle.setVisibilityAllowed(chosedElements.size() != 0);
                    target.add(chosedElementsTitle);
                }
            });
        }

        public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
            return new StringResourceStream("<wicket:panel><input wicket:id=\"checkbox\" type=\"checkbox\"/></wicket:panel>");
        }
    }
}
