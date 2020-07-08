package dynamicsoap.repository;

import dynamicsoap.GenericModel.Operations;

import java.util.List;

public interface CustomOperationRepository {

    List<Operations> GetbyName(String operationName);

}
