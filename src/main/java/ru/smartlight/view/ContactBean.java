package ru.smartlight.view;

import org.primefaces.PrimeFaces;
import ru.smartlight.ejb.ContactsManagerBean;
import ru.smartlight.phonebook.models.Contact;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ContactBean implements Serializable {
    private Contact selectedContact;
    @EJB
    private ContactsManagerBean contactsManagerBean;

    public List<Contact> getContacts() {
        return contactsManagerBean.getAllContacts();
    }

    public void openNew() {
        selectedContact = new Contact();
    }

    public void saveContact() {
        if (contactsManagerBean.isContactExist(selectedContact.getId())) {
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
}
