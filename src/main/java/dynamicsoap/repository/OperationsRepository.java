package dynamicsoap.repository;

import dynamicsoap.GenericModel.Operations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsRepository extends JpaRepository<Operations,Integer>,CustomOperationRepository {
}
