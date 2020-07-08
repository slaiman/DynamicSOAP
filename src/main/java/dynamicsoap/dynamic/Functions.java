package dynamicsoap.dynamic;

import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class Functions {

    private Object[] ConvertInputs(Method InputMethod,List inputs) {

        Object[] convertedInputs = new Object[inputs.size()];
        for(int i = 0 ; i < inputs.size() ; i++){
            try
            {
                if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("long"))convertedInputs[i] = Long.parseLong((String) inputs.get(i));
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("int"))convertedInputs[i] = Integer.parseInt((String) inputs.get(i));
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("short"))convertedInputs[i] = Short.parseShort((String) inputs.get(i));
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("double"))convertedInputs[i] = Double.parseDouble((String) inputs.get(i));
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("float"))convertedInputs[i] = Float.parseFloat((String) inputs.get(i));
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("char"))convertedInputs[i] = ((String) inputs.get(i)).charAt(0);
                else if(InputMethod.getParameterTypes()[i].getName().equalsIgnoreCase("byte"))convertedInputs[i] = Byte.parseByte((String) inputs.get(i));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return convertedInputs;
    }

    private Class<?> GetClass(String type) throws Exception{

        if(type.equalsIgnoreCase("long")) return long.class;
        else if(type.equalsIgnoreCase("int")) return int.class;
        else if(type.equalsIgnoreCase("short")) return short.class;
        else if(type.equalsIgnoreCase("double")) return double.class;
        else if(type.equalsIgnoreCase("float")) return float.class;
        else if(type.equalsIgnoreCase("char")) return char.class;
        else if(type.equalsIgnoreCase("byte")) return byte.class;
        else {
            return Class.forName(type);
        }
    }
}
