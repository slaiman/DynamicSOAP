package dynamicsoap.repository;

import dynamicsoap.GenericModel.OperationParameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationParametersRepository extends JpaRepository<OperationParameters,Integer>,CustomOperationParametersRepository {
}
