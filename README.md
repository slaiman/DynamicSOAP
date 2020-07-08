# DynamicSOAP

This tool contains three consecutive functionality:

1-it is used to generate dynamically java classes from wsdl file using Restful call.

2-it parses the wsdl structure from operations and parameters to the database, so that it can be managed and called dynamically from UI.

3-it contains dynamic client that calls the operation you want with its required parameters.

All these steps can be called by restful webservice call. These steps must be called respectively step 1 -> step 2 -> step 3.

The wsdl file provided in the first stage must be in the resource folder.

When generating the java classes (step 1) one stub class is created, the implementation of the java classes are done using the Factory design pattern. 

1-Request for generating java file, be sure that the wsdl file is in the resources folder:

![generate request](https://user-images.githubusercontent.com/15660872/86942270-9c786580-c14d-11ea-86cc-f329a5112fd5.jpg)

2-Request for parsing the java stub and saving data into database:

![parse request](https://user-images.githubusercontent.com/15660872/86942384-bf0a7e80-c14d-11ea-81a9-7c1a8d4e7efb.jpg)

3-Request for calling the webservice, please be sure you insert all the parameters:

![client request](https://user-images.githubusercontent.com/15660872/86941888-2a078580-c14d-11ea-9fd6-059eba076793.jpg)
