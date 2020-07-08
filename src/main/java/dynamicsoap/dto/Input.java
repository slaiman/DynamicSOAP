package dynamicsoap.dto;

import java.util.List;

public class Input {

    private String stubName;
    private String operation;
    private List<Parameter> parameters;

    public String getStubName() {
        return stubName;
    }

    public void setStubName(String stubName) {
        this.stubName = stubName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
