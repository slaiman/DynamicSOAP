package dynamicsoap.repository;

import dynamicsoap.GenericModel.OperationParameters;
import dynamicsoap.GenericModel.Operations;

import java.util.List;

public interface CustomOperationParametersRepository {

    List<OperationParameters> GetbyNameAndCode(String paramName,String paramCode);

}
