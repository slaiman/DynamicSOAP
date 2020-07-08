package dynamicsoap.repository;

import dynamicsoap.GenericModel.WebService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebServiceRepository extends JpaRepository<WebService, Integer> {
}
