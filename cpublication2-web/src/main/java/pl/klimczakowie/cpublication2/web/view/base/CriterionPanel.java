package pl.klimczakowie.cpublication2.web.view.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.metamodel.StaticMetamodel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.NamedCriteria;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.web.tooling.CriterionDTO;
import pl.klimczakowie.cpublication2.web.tooling.CriterionDisplay;
import pl.klimczakowie.cpublication2.web.tooling.TableToModalProvider;

public class CriterionPanel<T extends Serializable, M> extends BaseCleanPage {
    private static final Logger LOG = LoggerFactory.getLogger(CriterionPanel.class);
    private static final long serialVersionUID = 7179424011910986931L;
    private static final Map<Class<?>, List<String>> CRITERIA_MAP;

    private TableToModalProvider<Criterion<T>> tableToModalProvider;
    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    private BaseCriteriaTablePage<T, M> page;

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;

    static {
        CRITERIA_MAP = new HashMap<Class<?>, List<String>>();
        CRITERIA_MAP.put(String.class, Arrays.asList("=", "!=", "!=NULL", "=NULL", "LIKE"));

        CRITERIA_MAP.put(Enum.class, Arrays.asList("=", "!=", "!=NULL", "=NULL"));

        CRITERIA_MAP.put(int.class, Arrays.asList("=", "!=", "!=NULL", "=NULL", ">=", "<="));
        CRITERIA_MAP.put(Integer.class, Arrays.asList("=", "!=", "!=NULL", "=NULL", ">=", "<="));
        CRITERIA_MAP.put(long.class, Arrays.asList("=", "!=", "!=NULL", "=NULL", ">=", "<="));
        CRITERIA_MAP.put(Long.class, Arrays.asList("=", "!=", "!=NULL", "=NULL", ">=", "<="));

        CRITERIA_MAP.put(Date.class, Arrays.asList(">=", "<="));
    }

    public CriterionPanel(TableToModalProvider<Criterion<T>> tableToModalProvider, BaseCriteriaTablePage<T, M> page) {
        super(null);
        this.tableToModalProvider = tableToModalProvider;
        this.page = page;

        add(feedback.setOutputMarkupId(true));
        add(new InputForm("inputForm"));
    }

    private class InputForm extends Form<CriterionDTO<T, M>> {
        private static final long serialVersionUID = 1997572509197780476L;

        public InputForm(String id) {
            super(id, new CompoundPropertyModel<CriterionDTO<T, M>>(new CriterionDTO<T, M>(tableToModalProvider.getModel().getObject(),
                    page.getModelClass(), page.getMetamodelClass())));

            final Map<String, CriterionDisplay> availableColumns = getCriteriaColumns(page.getMetamodelClass());

            IModel<List<? extends String>> typeChoices = new AbstractReadOnlyModel<List<? extends String>>() {
                private static final long serialVersionUID = 3968639765727837514L;

                @Override
                public List<String> getObject() {
                    if (getModelObject().getColumn() == null) {
                        return Collections.emptyList();
                    }
                    CriterionDisplay criterionDisplay = availableColumns.get(getModelObject().getColumn());
                    List<String> models = criterionDisplay.getAvailableCriterions();

                    if (models == null) {
                        models = Collections.emptyList();
                    }
                    return models;
                }
            };

            IModel<List<Object>> valueChoices = new AbstractReadOnlyModel<List<Object>>() {
                private static final long serialVersionUID = 3968639765727837514L;

                @Override
                public List<Object> getObject() {
                    if (getModelObject().getColumn() == null) {
                        return Collections.emptyList();
                    }
                    List<Object> values = new ArrayList<Object>();

                    Class<?> modelClass = page.getMetamodelClass().getAnnotation(StaticMetamodel.class).value();
                    Class<?> enumType = null;

                    try {
                        enumType = modelClass.getDeclaredField(getModelObject().getColumn()).getType();
                        if (!enumType.isEnum()) {
                            return values;
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("There is no field " + getModelObject().getColumn() + " in "
                                + modelClass.getCanonicalName() + " class", e);
                    }

                    for (Object value : Arrays.asList(enumType.getEnumConstants())) {
                        values.add(value);
                    }

                    return values;
                }
            };

            final DropDownChoice<String> columnsChoice = new DropDownChoice<String>("column", new ArrayList<String>(
                    availableColumns.keySet()), new IChoiceRenderer<String>() {
                private static final long serialVersionUID = 588971693306748479L;

                @Override
                public Object getDisplayValue(String object) {
                    return availableColumns.get(object).getFieldDisplay();
                }

                @Override
                public String getIdValue(String object, int index) {
                    return object;
                }
            });

            final DropDownChoice<String> criteriaTypesChoice = new DropDownChoice<String>("type", new PropertyModel<String>(
                    getModelObject(), "type"), typeChoices);
            getModelObject().setType("LIKE");
            criteriaTypesChoice.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

            final TextField<String> stringValue = new TextField<String>("stringValue", new PropertyModel<String>(getModelObject(), "value"));
            stringValue.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false);

            final DropDownChoice<Object> choiceValue = new DropDownChoice<Object>("choiceValue", new PropertyModel<Object>(
                    getModelObject(), "value"), valueChoices);
            choiceValue.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false);

            final DateTextField dateValue = new DateTextField("dateValue", new PropertyModel<Date>(getModelObject(), "value"),
                    new StyleDateConverter("L-", true));

            DatePicker datePicker = new DatePicker() {

                private static final long serialVersionUID = 7911904143494731547L;

                @Override
                protected String getAdditionalJavaScript() {
                    return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
                }
            };
            datePicker.setShowOnFieldClick(true);
            datePicker.setAutoHide(true);
            dateValue.add(datePicker);
            dateValue.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false);

            columnsChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
                private static final long serialVersionUID = -2057565514717103695L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    Class<?> modelClass = page.getMetamodelClass().getAnnotation(StaticMetamodel.class).value();
                    getModelObject().setDisplayName(availableColumns.get(getModelObject().getColumn()).getFieldDisplay());
                    Class<?> fieldType = null;
                    try {
                        fieldType = getFieldType(page.getMetamodelClass().getDeclaredField(getModelObject().getColumn()), modelClass);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("W klasie " + modelClass.getCanonicalName() + " nie istnieje pole "
                                + getModelObject().getColumn(), e);
                    }

                    if (Enum.class.equals(fieldType)) {
                        stringValue.setVisible(false);
                        stringValue.setVisibilityAllowed(false);
                        choiceValue.setVisible(true);
                        choiceValue.setVisibilityAllowed(true);
                        dateValue.setVisible(false);
                        dateValue.setVisibilityAllowed(false);
                    } else if (Date.class.equals(fieldType)) {
                        stringValue.setVisible(false);
                        stringValue.setVisibilityAllowed(false);
                        choiceValue.setVisible(false);
                        choiceValue.setVisibilityAllowed(false);
                        dateValue.setVisible(true);
                        dateValue.setVisibilityAllowed(true);
                    } else {
                        stringValue.setVisible(true);
                        stringValue.setVisibilityAllowed(true);
                        choiceValue.setVisible(false);
                        choiceValue.setVisibilityAllowed(false);
                        dateValue.setVisible(false);
                        dateValue.setVisibilityAllowed(false);
                    }

                    target.add(criteriaTypesChoice);
                    target.add(stringValue);
                    target.add(choiceValue);
                    target.add(dateValue);
                }
            });

            add(columnsChoice);
            add(criteriaTypesChoice);
            add(stringValue);
            add(choiceValue);
            add(dateValue);

            add(new saveAjaxButton("saveButton", this).setDefaultFormProcessing(true));
            add(new abortAjaxButton("abortButton", this).setDefaultFormProcessing(false));

        }

        private Class<?> getFieldType(Field metamodelField, Class<?> modelClass) {
            Class<?> fieldType;
            try {
                Field field = modelClass.getDeclaredField(metamodelField.getName());
                if (field.getAnnotation(NamedCriteria.class) == null) {
                    return null;
                }

                if (field.getType().isEnum()) {
                    fieldType = Enum.class;
                } else {
                    fieldType = field.getType();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("W klasie " + modelClass.getCanonicalName() + " nie istnieje pole "
                        + metamodelField.getName(), e);
            }
            return fieldType;
        }

        private List<String> getCriteriaTypes(Class<?> metamodelClass, Field metamodelField) {
            Class<?> modelClass = metamodelClass.getAnnotation(StaticMetamodel.class).value();
            Class<?> fieldType = getFieldType(metamodelField, modelClass);

            // FIXME UGLY
            if (fieldType == null) {
                return null;
            }

            return CRITERIA_MAP.get(fieldType);
        }

        private String getCriteriaDisplayName(Class<?> metamodelClass, Field metamodelField) {
            Class<?> modelClass = metamodelClass.getAnnotation(StaticMetamodel.class).value();
            Field field;
            try {
                field = modelClass.getDeclaredField(metamodelField.getName());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            return field.getAnnotation(NamedCriteria.class).value();
        }

        private Map<String, CriterionDisplay> getCriteriaColumns(Class<?> metamodelClass) {
            Map<String, CriterionDisplay> displayCriterions = new HashMap<String, CriterionDisplay>();

            if (metamodelClass.getAnnotation(StaticMetamodel.class) == null) {
                throw new IllegalArgumentException("Klasa " + metamodelClass.getCanonicalName() + " nie jest metamodelem encji");
            }

            for (Field metamodelField : metamodelClass.getFields()) {
                List<String> criteriaTypes = getCriteriaTypes(metamodelClass, metamodelField);
                if (criteriaTypes != null && criteriaTypes.size() > 0) {
                    CriterionDisplay criterionDisplay = new CriterionDisplay();
                    criterionDisplay.setAvailableCriterions(criteriaTypes);
                    criterionDisplay.setFieldName(metamodelField.getName());
                    criterionDisplay.setFieldDisplay(getCriteriaDisplayName(metamodelClass, metamodelField));
                    displayCriterions.put(metamodelField.getName(), criterionDisplay);
                }
            }

            return displayCriterions;
        }

        /**
         * End of criterion definition edition.
         * 
         * @param target
         */
        public void closeModified(AjaxRequestTarget target) {
            tableToModalProvider.getModal().close(target);
            tableToModalProvider.reRenderDependentComponents(target);
        }

        private final class abortAjaxButton extends AjaxButton {
            private static final long serialVersionUID = -4147490899188148760L;

            private abortAjaxButton(String id, Form<?> form) {
                super(id, form);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                tableToModalProvider.getModal().close(target);
                closeModified(target);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }

        private final class saveAjaxButton extends AjaxButton {
            private static final long serialVersionUID = 2919263666105760806L;

            private saveAjaxButton(String id, Form<?> form) {
                super(id, form);
            }

            @SuppressWarnings("rawtypes")
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                page.getCriteria().set((CriterionDTO) form.getDefaultModelObject());
                closeModified(target);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }
    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }

}
