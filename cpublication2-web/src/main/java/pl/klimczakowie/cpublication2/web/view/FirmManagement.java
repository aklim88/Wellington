package pl.klimczakowie.cpublication2.web.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import pl.klimczakowie.cpublication2.model.Category;
import pl.klimczakowie.cpublication2.model.Country;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.view.base.BasePage;

public abstract class FirmManagement extends BasePage {

    private static final long serialVersionUID = -4015610965624103544L;
    private List<String> countries = new ArrayList<String>();
    private List<String> categories = new ArrayList<String>();
    @Named("cpublication2Persistence")
    @Inject
    protected Cpublication2Service persistenceReference;

    public FirmManagement() {
        super(null);
        List<Country> countriesEntity = persistenceReference.pobierzEncje(Country.class, null, null);
        List<Category> categoriesEntity = persistenceReference.pobierzEncje(Category.class, null, null);
        for (int i = 0; i < countriesEntity.size(); i++) {
            countries.add(countriesEntity.get(i).getCountry());
        }
        for (int i = 0; i < categoriesEntity.size(); i++) {
            categories.add(categoriesEntity.get(i).getCategory());
        }

    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }

}
