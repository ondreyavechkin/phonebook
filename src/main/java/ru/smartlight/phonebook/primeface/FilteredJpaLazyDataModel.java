package ru.smartlight.phonebook.primeface;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.JpaLazyDataModel;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SerializableSupplier;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JpaLazyDataModel with global filter support
 *
 * @param <T>
 */
public abstract class FilteredJpaLazyDataModel<T> extends JpaLazyDataModel<T> {
    public FilteredJpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager) {
        super(entityClass, entityManager);
    }

    @Override
    protected void applyFilters(CriteriaBuilder cb,
                                CriteriaQuery<?> cq,
                                Root<T> root,
                                Map<String, FilterMeta> filterBy) {

        List<Predicate> predicates = new ArrayList<>();

        applyGlobalFilters(cb, cq, root, predicates);

        if (filterBy != null) {
            for (FilterMeta filter : filterBy.values()) {
                if (filter.getField() == null || filter.getFilterValue() == null) {
                    continue;
                }
                String filterValue = filter.getFilterValue().toString();
                if (filter.getField().equals("globalFilter")) {
                    if (!filterValue.isEmpty()) {
                        List<Predicate> globalFilters = getGlobalFilters(cb, root, filterValue);
                        predicates.add(cb.or(globalFilters.toArray(new Predicate[globalFilters.size()])));
                    }
                } else {
                    Expression fieldExpression = resolveFieldExpression(cb, cq, root, filter.getField());
                    Field filterField = LangUtils.getFieldRecursive(entityClass, filter.getField());
                    Object convertedFilterValue = convertToType(filterValue, filterField.getType());
                    Predicate predicate = createPredicate(filter, filterField, root, cb, fieldExpression, (Comparable) convertedFilterValue);
                    predicates.add(predicate);
                }
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(
                    cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
    }

    public abstract List<Predicate> getGlobalFilters(CriteriaBuilder criteriaBuilder, Root<T> root, String filterValue);
}
