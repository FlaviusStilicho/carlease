package com.alexbakker.carlease.repository;


import com.alexbakker.carlease.model.Car;
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
public class CarHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<Car> getCarRevisions(Car car) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(Car.class, true, true).
                        add(AuditEntity.property("id").eq(car.getId())).getResultList();
    }

}
