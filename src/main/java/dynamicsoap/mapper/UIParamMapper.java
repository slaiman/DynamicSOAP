package dynamicsoap.mapper;

import java.util.HashMap;

public class UIParamMapper {


    public Object map(HashMap<String, Object> paramValues, String paramMap) {

        if(paramMap == null || paramMap.isEmpty())
        {
            throw new RuntimeException("mapping parameter name must not be null or empty");
        }

        Object value = paramValues.get(paramMap);

        if(value == null)
        {
            throw new RuntimeException("parameter value is not found");
        }
        return value;
    }
}
