package com.alexbakker.carlease.repository;


import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ContractHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<Contract> getContractRevisions(Contract contract) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(Contract.class, true, true).
                        add(AuditEntity.property("id").eq(contract.getId())).getResultList();
    }

}
