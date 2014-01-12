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
    private EntityManager entityManager;

    @Override
    public void addCustomer(Customer customer) throws CustomerAlreadyExistsException {
        Customer existingCustomer = entityManager.find(Customer.class, customer.getId());
        if (existingCustomer != null) {
            throw new CustomerAlreadyExistsException();
        } else {
            entityManager.persist(entityManager.merge(customer));
        }
    }

    @Override
    public Customer findCustomer(String email) throws CustomerNotFoundException {
        TypedQuery<Customer> query = entityManager.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        entityManager.persist(entityManager.merge(customer));
    }

}
