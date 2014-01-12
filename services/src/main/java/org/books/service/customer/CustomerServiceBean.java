package org.books.service.customer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import static javax.persistence.PersistenceContextType.EXTENDED;
import javax.persistence.TypedQuery;
import org.books.persistence.customer.Customer;

@Stateless(name = "CustomerService")
public class CustomerServiceBean implements CustomerService {

    @PersistenceContext(type = EXTENDED)
    private EntityManager em;

    @Override
    public void addCustomer(Customer customer) throws CustomerAlreadyExistsException {
        Customer existingCustomer = em.find(Customer.class, customer.getId());
        if (existingCustomer != null) {
            throw new CustomerAlreadyExistsException();
        } else {
            em.persist(em.merge(customer));
        }
    }

    @Override
    public Customer findCustomer(String email) throws CustomerNotFoundException {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        em.persist(em.merge(customer));
    }

}
