package pl.klimczakowie.cpublication2.web.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Firm_;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.persistence.Criterion;
import pl.klimczakowie.cpublication2.persistence.CriterionType;
import pl.klimczakowie.cpublication2.web.tooling.CriterionDTO;

public class SearchPage extends FirmManagement {
    private static final long serialVersionUID = 4300390880041685656L;
    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;

    public SearchPage() {
        add(new InputForm("inputForm"));
    }

    @Override
    protected String getPageTitle() {
        return "Basic search";
    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }

    private class InputForm extends Form<Firm> {

        private static final long serialVersionUID = -6789693786570942596L;

        public InputForm(String id) {
            super(id, new CompoundPropertyModel<Firm>(new Firm()));
            add(new TextField<String>("name").setRequired(true));
            add(new TextField<String>("postCode").setRequired(true));
            add(new CountryDropDownChoice("country").setRequired(true));

            add(new SearchAjaxButton("searchButton", this).setDefaultFormProcessing(true));

            add(feedback.setOutputMarkupId(true));
        }

        private final class CountryDropDownChoice extends DropDownChoice<String> {

            private static final long serialVersionUID = 571822569466137797L;

            public CountryDropDownChoice(String id) {
                super(id, getCountries());
            }
        }

        private final class SearchAjaxButton extends IndicatingAjaxButton {
            private static final long serialVersionUID = 2919263666105760806L;

            private SearchAjaxButton(String id, Form<?> form) {
                super(id, form);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Firm firmFilter = ((Firm) form.getDefaultModelObject());
                List<CriterionDTO<Firm, Firm_>> criterias = new ArrayList<CriterionDTO<Firm, Firm_>>();

                LOG.info("Filter by object: " + firmFilter);

                criterias.add(new CriterionDTO<Firm, Firm_>(new Criterion<Firm>(Firm_.postCode, CriterionType.LIKE, firmFilter
                        .getPostCode()), Firm.class, Firm_.class));
                criterias.add(new CriterionDTO<Firm, Firm_>(new Criterion<Firm>(Firm_.name, CriterionType.LIKE, firmFilter.getName()),
                        Firm.class, Firm_.class));
                criterias.add(new CriterionDTO<Firm, Firm_>(
                        new Criterion<Firm>(Firm_.country, CriterionType.LIKE, firmFilter.getCountry()), Firm.class, Firm_.class));

                setResponsePage(new FirmListPage(criterias));
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }
    }

}
