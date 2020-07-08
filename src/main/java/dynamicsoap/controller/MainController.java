package dynamicsoap.controller;

import dynamicsoap.GenericModel.WebService;
import dynamicsoap.dto.Input;
import dynamicsoap.dto.Parameter;
import dynamicsoap.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequestMapping(path = "/Service")
@RestController
public class MainController {

    @Autowired
    ParserService parseService;

    @RequestMapping(value = "/Generate", method = RequestMethod.GET,produces = "application/json")
    ResponseEntity<String> Generate(@RequestParam String wsdlName) throws Exception
    {
        parseService.GenerateJavafromWSDL(wsdlName);
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/ParseRec", method = RequestMethod.GET,produces = "application/json")
    ResponseEntity<Object> ParseRecursive(@RequestParam String packageName,@RequestParam String className) throws Exception
    {
        WebService response = parseService.ParseWSDLRecursive(packageName,className);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/CallDynamic", method = RequestMethod.POST, consumes="application/json",produces = "application/json")
    @ResponseBody
    ResponseEntity<Object> CallDynamic(@RequestBody Input input) throws Exception
    {
        HashMap<String,Object> parameterValues = new HashMap<String,Object>();
        for (Parameter param: input.getParameters()) {
            parameterValues.put(param.getName().toUpperCase(),param.getValue());
        }
        Object response = parseService.CallDynamicClient(input.getStubName(),input.getOperation(),parameterValues);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

}
