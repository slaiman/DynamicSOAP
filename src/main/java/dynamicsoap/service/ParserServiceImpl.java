package dynamicsoap.service;

import dynamicsoap.GenericModel.OperationParameters;
import dynamicsoap.GenericModel.Operations;
import dynamicsoap.GenericModel.WebService;
import dynamicsoap.repository.OperationParametersRepository;
import dynamicsoap.repository.OperationsRepository;
import dynamicsoap.repository.WebServiceRepository;
import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.codegen.CodeGenerationEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ParserServiceImpl implements  ParserService {

    @Autowired
    private WebServiceRepository webServiceRepo;

    @Autowired
    private OperationsRepository operationsRepo;

    @Autowired
    private OperationParametersRepository operationParametersRepo;

    @Override
    public WebService ParseWSDLRecursive(String packageName, String className) {
        return AddWebService(packageName,className);
    }

    @Override
    public Object CallDynamicClient(String StubName, String OperationName, HashMap<String,Object> paramValues) {
        Object result = null;
        try
        {
            Class stubClass = Class.forName(StubName);
            Object stubInstance = stubClass.newInstance();

            List<Operations> operations = operationsRepo.GetbyName(OperationName);
            if(operations.size() >= 1)
            {
                List<OperationParameters> params = operations.get(0).getOperation_parameters();
                Class[] paramTypes = getParamClasses(params);
                Method meth = stubClass.getDeclaredMethod(OperationName, paramTypes);
                List<Object> Result = new ArrayList<>();
                for(int i=0;i<params.size();i++)
                {
                    Result.add(callParams(params.get(i),paramValues));
                }
                result = meth.invoke(stubInstance, Result.toArray());
                result= result.toString();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    private Object callParams(OperationParameters param, HashMap<String,Object> paramValues) {

        Object Return = null;
        Object paramInstance = null;
        try
        {
            String paramName = param.getName();
            String paramCode = String.valueOf(param.getId());
            List<OperationParameters> params2 = operationParametersRepo.GetbyNameAndCode(paramName,paramCode);

            for(int j=0;j<params2.size();j++)
            {
                if(paramInstance == null) {
                    Class paramClass = Class.forName(paramName + "$Factory");

                    Method facMeth = paramClass.getDeclaredMethod("newInstance", null);
                    paramInstance = facMeth.invoke(null, null);
                }
                Operations op = params2.get(j).getParameter_operations();
                String opName = op.getName();
                Class[] paramTypes2 = getParamClasses(op.getOperation_parameters().stream().filter(x->x.getType().equals(OperationParameters.Type.PARAMETER)).collect(Collectors.toList()));

                Class caller = Class.forName(paramName);
                Method meth = caller.getDeclaredMethod(opName, paramTypes2);

                List<Object> Result = new ArrayList<Object>();
                List<OperationParameters> parameters = op.getOperation_parameters().stream().filter(x->x.getType().equals(OperationParameters.Type.PARAMETER)).collect(Collectors.toList());
                for(int i=0;i<parameters.size();i++)
                {
                    //get the name of the parameter
                    String name = parameters.get(i).getName();
                    //check if parameter is not primitive and enum
                    if(!isPrimitive(name) && !name.equals("java.lang.String") && !name.endsWith("Enum"))
                        Result.add(callParams(parameters.get(i),paramValues));
                    else
                    {
                        //get the name of parameter in class
                        String fieldName = opName.startsWith("set") ? opName.substring(3).toUpperCase():"";
                        //get the parameter value by it's name
                        Object Value = paramValues.get(fieldName);
                        //if parameter is not enum get the primitive value
                        if(!name.endsWith("Enum"))  Value = ParseValue(Value,name);
                        //if parameter is enum
                        else
                        {
                            Field field = Class.forName(name.substring(0, name.length() - 5)).getDeclaredField(((String) Value).toUpperCase());
                            Value = field.get(null);
                        }
                        Result.add(Value);
                    }
                }
                //call the method
                Object methodReturn = meth.invoke(paramInstance, Result.toArray());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return paramInstance;
    }

    private Object ParseValue(Object value, String name) {

        try
        {
            if(value.getClass().getName().equalsIgnoreCase(name)) return value;
            else
            {
                if(name.equalsIgnoreCase("boolean"))  return Boolean.parseBoolean(String.valueOf(value));
                else if(name.equalsIgnoreCase("long"))   return Long.parseLong(String.valueOf(value));
                else if(name.equalsIgnoreCase("int"))   return Integer.parseInt(String.valueOf(value));
                else if(name.equalsIgnoreCase("short")) return Short.parseShort(String.valueOf(value));
                else if(name.equalsIgnoreCase("double"))    return Double.parseDouble(String.valueOf(value));
                else if(name.equalsIgnoreCase("float")) return Float.parseFloat(String.valueOf(value));
                else if(name.equalsIgnoreCase("char"))  return String.valueOf(value).charAt(0);
                else if(name.equalsIgnoreCase("byte"))  return Byte.parseByte(String.valueOf(value));

                else if(name.equalsIgnoreCase("byte[]")) return String.valueOf(value).getBytes();
                else if(name.equalsIgnoreCase("int[]")) {
                    int[] arr = new int[String.valueOf(value).length()];
                    for(int i=0;i<arr.length;i++) arr[i] = Character.getNumericValue(String.valueOf(value).charAt(i));
                    return arr;
                }
                else if(name.equalsIgnoreCase("double[]")) return value;
                else if(name.equalsIgnoreCase("float[]")) return value;
                else if(name.equalsIgnoreCase("long[]")) return value;
                else if(name.equalsIgnoreCase("short[]")) return value;
                else if(name.equalsIgnoreCase("boolean[]")) return value;
                else if(name.equalsIgnoreCase("char[]")) return value;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return value;
    }

    private boolean isPrimitive(String className)
    {
        try
        {
            if(className.equalsIgnoreCase("boolean"))  return true;
            else if(className.equalsIgnoreCase("long"))   return true;
            else if(className.equalsIgnoreCase("int"))   return true;
            else if(className.equalsIgnoreCase("short")) return true;
            else if(className.equalsIgnoreCase("double"))    return true;
            else if(className.equalsIgnoreCase("float")) return true;
            else if(className.equalsIgnoreCase("char"))  return true;
            else if(className.equalsIgnoreCase("byte"))  return true;

            else if(className.equalsIgnoreCase("byte[]")) return true;
            else if(className.equalsIgnoreCase("int[]")) return true;
            else if(className.equalsIgnoreCase("double[]")) return true;
            else if(className.equalsIgnoreCase("float[]")) return true;
            else if(className.equalsIgnoreCase("long[]")) return true;
            else if(className.equalsIgnoreCase("short[]")) return true;
            else if(className.equalsIgnoreCase("boolean[]")) return true;
            else if(className.equalsIgnoreCase("char[]")) return true;

            else return false;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    private Class[] getParamClasses(List<OperationParameters> params) {

        int i = 0;
        Class[] classes = new Class[params.size()];
        try
        {
            for (OperationParameters op: params) {
                if(op.getName().equalsIgnoreCase("boolean"))  classes[i] = boolean.class;
                else if(op.getName().equalsIgnoreCase("long"))   classes[i] = long.class;
                else if(op.getName().equalsIgnoreCase("int"))   classes[i] = int.class;
                else if(op.getName().equalsIgnoreCase("short")) classes[i] =short.class;
                else if(op.getName().equalsIgnoreCase("double"))    classes[i] = double.class;
                else if(op.getName().equalsIgnoreCase("float")) classes[i] = float.class;
                else if(op.getName().equalsIgnoreCase("char"))  classes[i] = char.class;
                else if(op.getName().equalsIgnoreCase("byte"))  classes[i] = byte.class;

                else if(op.getName().equalsIgnoreCase("byte[]")) classes[i] = byte[].class;
                else if(op.getName().equalsIgnoreCase("int[]")) classes[i] = int[].class;
                else if(op.getName().equalsIgnoreCase("double[]")) classes[i] = double[].class;
                else if(op.getName().equalsIgnoreCase("float[]")) classes[i] = float[].class;
                else if(op.getName().equalsIgnoreCase("long[]")) classes[i] = long[].class;
                else if(op.getName().equalsIgnoreCase("short[]")) classes[i] = short[].class;
                else if(op.getName().equalsIgnoreCase("boolean[]")) classes[i] = boolean[].class;
                else if(op.getName().equalsIgnoreCase("char[]")) classes[i] = char[].class;
                else classes[i] = Class.forName(op.getName());
                i++;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return classes;
    }

    public WebService AddWebService(String packageName, String className)
    {
        WebService service = new WebService();
        try {
            service.setStubName(className);
            service.setPackageName(packageName);
            service = webServiceRepo.save(service);
            AddOperations(packageName + "." + className,service,null);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return service;
    }

    public void AddOperations(String className, WebService service,OperationParameters param)
    {
        try
        {
            Class StubClass = Class.forName(className);
            //Object StubInstance = StubClass.newInstance();
            Predicate<Method> method_filter = null;

            Method[] allMethods = StubClass.getMethods();

            method_filter =  method -> !method.getDeclaringClass().getName().equals("java.lang.Object") && !method.getName().equals("wait") && !method.getName().startsWith("start") && !method.getName().equals("notify") && !method.getName().equals("notifyAll") && !method.getName().equals("getClass") && !method.getName().equals("toString") && !method.getName().equals("hashCode") && !method.getName().equals("equals");
            List<Method> filtered_methods = new LinkedList<>(Arrays.asList(allMethods)).stream().filter(method_filter).collect(Collectors.toList());

            if(service != null) {
                filtered_methods = filtered_methods.stream().filter(method -> method.getDeclaringClass().getName().endsWith(service.getStubName())).collect(Collectors.toList());
                filtered_methods = filtered_methods.stream().filter(method -> !method.getReturnType().getName().startsWith("org.apache.xmlbeans")).collect(Collectors.toList());
            }
            else
            {
                filtered_methods = filtered_methods.stream().filter(method -> !method.getName().startsWith("xset")).collect(Collectors.toList());
                filtered_methods = filtered_methods.stream().filter(method -> method.getReturnType().equals(Void.TYPE)).collect(Collectors.toList());
                filtered_methods = filtered_methods.stream().filter(method -> !method.getDeclaringClass().getName().startsWith("org.apache.xmlbeans")).collect(Collectors.toList());
                filtered_methods = filtered_methods.stream().filter(method -> method.getParameterTypes().length >= 1).collect(Collectors.toList());
                filtered_methods = filtered_methods.stream().filter(method -> !method.getParameterTypes()[0].getName().startsWith("org.apache.xmlbeans")).collect(Collectors.toList());
                if(filtered_methods.size() > 1) {
                    for (int i = 0; i < filtered_methods.size(); i++) {
                        if (StringUtils.countOccurrencesOf(filtered_methods.get(i).getDeclaringClass().getName(), ".") >= 2) {
                            filtered_methods.removeIf(method -> !method.getName().startsWith("set"));
                        }
                    }
                }

                else for(int i=0;i<filtered_methods.size();i++)
                {
                    Method meth1 = filtered_methods.get(i);
                    for(int j=0;j<filtered_methods.size();j++)
                    {
                        Method meth2 = filtered_methods.get(j);
                        if(i != j)
                        {
                            if(meth1.getName().equals(meth2.getName()))
                            {
                                if(meth1.getParameterCount() > meth2.getParameterCount())
                                {
                                    filtered_methods.remove(meth1);
                                    break;
                                }
                                else if(meth2.getParameterCount() > meth1.getParameterCount())
                                {
                                    filtered_methods.remove(meth2);
                                    break;
                                }
                                else if(meth1.getParameterCount() == meth2.getParameterCount())
                                {
                                    if(meth1.getParameterTypes()[0].getName().contains("xml"))
                                    {
                                        filtered_methods.remove(meth1);
                                        break;
                                    }
                                    else if(meth2.getParameterTypes()[0].getName().contains("xml"))
                                    {
                                        filtered_methods.remove(meth2);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Method ClassMethod : filtered_methods) {

                String mname = ClassMethod.getName();
                String output = ClassMethod.getReturnType().getName();

                Operations operation = new Operations();
                operation.setName(mname);
                operation.setOperation_webService(service);
                operation.setOutputType(output);
                operation = operationsRepo.save(operation);
                if(param != null)
                {
                    OperationParameters op = new OperationParameters();
                    op.setType(OperationParameters.Type.CALLER);
                    op.setName(param.getName());
                    op.setParamCode(String.valueOf(param.getId()));
                    op.setParameter_operations(operation);
                    operationParametersRepo.save(op);
                }
                AddOperationParameters(ClassMethod,operation);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void AddOperationParameters(Method ClassMethod,Operations operation)
    {
        try
        {
            for (java.lang.reflect.Parameter param : ClassMethod.getParameters()) {

                String paramName = param.getParameterizedType().getTypeName();
                OperationParameters parameter = new OperationParameters();
                parameter.setName(paramName);
                parameter.setParameter_operations(operation);
                parameter.setType(OperationParameters.Type.PARAMETER);
                parameter = operationParametersRepo.save(parameter);

                if(!param.getType().isPrimitive()
                        && !param.getType().getName().equals("java.lang.String")
                        && !param.getType().getName().equals("java.util.TimeZone")
                        && !param.getType().isArray())  AddOperations(paramName, null,parameter);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void GenerateJavafromWSDL(String wsdlName) {
        try {

            Map<String, CommandLineOption> Configuration = new <String,String>HashMap();
            Configuration.put("p",new CommandLineOption("p", getStringArray("dynamicsoap.stubs")));
            Configuration.put("uri",new CommandLineOption("uri",getStringArray("src/main/resources/"+wsdlName+".wsdl")));
            Configuration.put("d",new CommandLineOption("d",getStringArray("xmlbeans")));
            Configuration.put("o",new CommandLineOption("o",getStringArray("src/main/")));
            Configuration.put("S",new CommandLineOption("S",getStringArray("java/")));
            Configuration.put("R",new CommandLineOption("R",getStringArray("resources/")));
            Configuration.put("s", new CommandLineOption("s", new String[0]));
            Map<String, CommandLineOption> commandLineOptions = Configuration;
            CommandLineOptionParser parser = new CommandLineOptionParser(commandLineOptions);
            (new CodeGenerationEngine(parser)).generate();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String[] getStringArray(String value) {
        String[] values = new String[]{value};
        return values;
    }


}
