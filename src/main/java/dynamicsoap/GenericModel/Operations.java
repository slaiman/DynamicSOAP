package dynamicsoap.GenericModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Operations")
@NoArgsConstructor
public class Operations {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "outputType")
    private String outputType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="webServiceId")
    @JsonIgnore
    private WebService operation_webService;

    @OneToMany(mappedBy = "parameter_operations",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OperationParameters> operation_parameters = new ArrayList<OperationParameters>();
}
