package dynamicsoap.GenericModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "OperationParameters")
@NoArgsConstructor
public class OperationParameters {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "paramCode")
    private String paramCode;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="operationsId")
    @JsonIgnore
    private Operations parameter_operations;

    public enum Type {
        PARAMETER, CALLER;
    }

}
