package dynamicsoap.repository;

import dynamicsoap.GenericModel.Operations;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CustomOperationRepositoryImpl implements  CustomOperationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Operations> GetbyName(String operationName) {

        TypedQuery<Operations> query = entityManager.createQuery("from Operations where name = '"+operationName+"'", Operations.class);

        List<Operations> operations = query.getResultList();
        return operations;
    }
}
