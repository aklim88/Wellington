package pl.klimczakowie.cpublication2.persistence;

import java.io.Serializable;
import java.util.List;

import pl.klimczakowie.cpublication2.model.Firm;

public interface Cpublication2Service {

    <T> List<T> pobierzEncje(Class<T> c, SortOrder orderBy, List<Criterion<T>> andCriterias);

    <T> List<T> pobierzEncje(Class<T> c, SortOrder orderBy, List<Criterion<T>> andCriterias, Long first, Long max);

    <T> long zliczEncje(Class<T> c, List<Criterion<T>> andCriterias);

    <T> T pobierzEncje(Class<T> entityClass, Serializable primaryKey);

    <T> T zapiszEncje(T object);

	Firm findFirmForUser(String username, String password);

}
