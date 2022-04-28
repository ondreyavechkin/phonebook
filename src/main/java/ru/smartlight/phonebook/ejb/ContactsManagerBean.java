package ru.smartlight.phonebook.ejb;

import ru.smartlight.phonebook.models.Contact;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@NamedQuery(
        name = "findContactsByAnyColumn",
        query = "SELECT c FROM Contact c WHERE c.firstName LIKE :searchValue OR c.lastName LIKE :searchValue OR c.middleName LIKE :searchValue" +
                "OR c.mobilePhone LIKE :searchValue OR c.workPhone LIKE :searchValue OR c.homePhone LIKE :searchValue"
)

@Stateless
@LocalBean
public class ContactsManagerBean {
    @PersistenceContext(unitName = "phonebookPU")
    private EntityManager entityManager;

    public Contact createContact(Contact contact) {
        entityManager.persist(contact);
        return contact;
    }

    public boolean isContactExist(long id) {
        return entityManager.find(Contact.class, id) != null;
    }

    public Contact updateContract(Contact contact) {
        return entityManager.merge(contact);
    }

    public void deleteContact(long id) {
        Contact contact = entityManager.find(Contact.class, id);
        if (contact != null) {
            entityManager.remove(contact);
        }
    }

    public boolean isDuplicateContact(String firstName, String lastName, String middleName, long id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
        Root<Contact> root = criteriaQuery.from(Contact.class);
        criteriaQuery = criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("firstName"), firstName));
        predicates.add(criteriaBuilder.equal(root.get("lastName"), lastName));
        predicates.add(criteriaBuilder.equal(root.get("middleName"), middleName));

        if (!predicates.isEmpty()) {
            criteriaQuery.where(
                    criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        List<Contact> result = entityManager.createQuery(criteriaQuery).getResultList();
        return result.size() > 0 && result.get(0).getId() != id;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
