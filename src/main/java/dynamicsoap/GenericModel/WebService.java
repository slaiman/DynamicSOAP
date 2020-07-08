package dynamicsoap.GenericModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "WebService")
@NoArgsConstructor
public class WebService {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "packageName")
    private String packageName;

    @Column(name = "stubName")
    private String stubName;

    @OneToMany(mappedBy = "operation_webService", fetch = FetchType.LAZY)
    private List<Operations> webservice_operations = new ArrayList<Operations>();
}
