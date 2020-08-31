package com.alexbakker.carlease.repository;


import com.alexbakker.carlease.model.Customer;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<Customer> getCustomerRevisions(Customer customer) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(Customer.class, true, true).
                        add(AuditEntity.property("id").eq(customer.getId())).getResultList();
    }

}
