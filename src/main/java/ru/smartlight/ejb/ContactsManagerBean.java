package ru.smartlight.ejb;

import ru.smartlight.phonebook.models.Contact;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@LocalBean
public class ContactsManagerBean {
    @PersistenceContext(unitName = "phonebookPU")
    private EntityManager entityManager;

    public Contact createContact(Contact contact) {

        entityManager.persist(contact);
        return contact;
    }

    public List<Contact> getAllContacts() {
        TypedQuery<Contact> query = entityManager.createQuery("select c from Contact c", Contact.class);
        return query.getResultList();
    }

    public boolean isContactExist(long id) {
        return entityManager.find(Contact.class, id) == null;
    }

    public Contact updateContract(Contact contact) {
        return entityManager.merge(contact);
    }

    public List<Contact> findContacts() {
        return null;
    }

    public void deleteContact(long id) {
        Contact contact = entityManager.find(Contact.class, id);
        if (contact != null) {
            entityManager.remove(contact);
        }
    }
}
