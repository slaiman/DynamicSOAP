package dynamicsoap.service;

import dynamicsoap.GenericModel.WebService;

import java.util.HashMap;

public interface ParserService {

    public WebService ParseWSDLRecursive(String packageName, String className);
    public Object CallDynamicClient(String StubName, String OperationName, HashMap<String,Object> paramValues);
    public void GenerateJavafromWSDL(String wsdlName);

}
