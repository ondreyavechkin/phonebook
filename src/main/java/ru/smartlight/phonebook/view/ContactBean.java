package ru.smartlight.phonebook.view;

import org.primefaces.PrimeFaces;
import ru.smartlight.phonebook.ejb.ContactsManagerBean;
import ru.smartlight.phonebook.models.Contact;
import ru.smartlight.phonebook.primeface.FilteredJpaLazyDataModel;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ContactBean implements Serializable {
    private final FilteredJpaLazyDataModel<Contact> model;
    private Contact selectedContact;
    @EJB
    private ContactsManagerBean contactsManagerBean;

    public ContactBean() {
        model = new FilteredJpaLazyDataModel<Contact>(Contact.class, () -> contactsManagerBean.getEntityManager()) {
            @Override
            public List<Predicate> getGlobalFilters(CriteriaBuilder criteriaBuilder, Root<Contact> root, String filterValue) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + filterValue + "%"));
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + filterValue + "%"));
                predicates.add(criteriaBuilder.like(root.get("middleName"), "%" + filterValue + "%"));
                predicates.add(criteriaBuilder.like(root.get("mobilePhone"), "%" + filterValue + "%"));
                predicates.add(criteriaBuilder.like(root.get("homePhone"), "%" + filterValue + "%"));
                predicates.add(criteriaBuilder.like(root.get("workPhone"), "%" + filterValue + "%"));
                return predicates;
            }
        };
    }

    public void openNew() {
        selectedContact = new Contact();
    }

    public void saveContact() {
        if (contactsManagerBean.isDuplicateContact(selectedContact.getFirstName(), selectedContact.getLastName(), selectedContact.getMiddleName(), selectedContact.getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Contact " +
                    selectedContact.getFirstName() + " " + selectedContact.getLastName() + " " +  selectedContact.getMiddleName() + " already exists!"));
        } else if (!contactsManagerBean.isContactExist(selectedContact.getId())) {
            contactsManagerBean.createContact(selectedContact);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Contact Added"));
        } else {
            contactsManagerBean.updateContract(selectedContact);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Contact Updated"));
        }

        PrimeFaces.current().executeScript("PF('manageContactDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-contacts");
    }

    public void deleteContact() {
        contactsManagerBean.deleteContact(selectedContact.getId());
        this.selectedContact = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-contacts");
    }


    public Contact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public FilteredJpaLazyDataModel getModel() {
        return model;
    }
}
