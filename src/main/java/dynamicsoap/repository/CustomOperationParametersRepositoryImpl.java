package dynamicsoap.repository;

import dynamicsoap.GenericModel.OperationParameters;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CustomOperationParametersRepositoryImpl implements  CustomOperationParametersRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<OperationParameters> GetbyNameAndCode(String paramName, String paramCode) {
        Query query = entityManager.createNativeQuery("select * from OPERATION_PARAMETERS where name = '"+paramName+"' and param_code='"+paramCode+"'", OperationParameters.class);

        List<OperationParameters> operationParams = query.getResultList();
        return operationParams;
    }
}
