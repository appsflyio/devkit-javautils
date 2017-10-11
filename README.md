# About appsfly.io Dev Kit Java Utils
Java Utils contains resources to help communicate with appsfly.io secure server through encryption. 

##  Get Started
To start posting messages securely we need the following:

| Key | Description |
| --- | --- |
| Module Handle | Unique handle of the micro module. This will be provided by the service provider. |
| Secret Key | Secret Key is required for encryption. Secret Key should be generated on the Appsfly publisher dashboard. |
| App Key | Application key to identify the publisher instance. |

## Generation of checksum

```
String payload = body + "|" +  microModuleId + "|" + config.appKey + "|" + userID;
String checksum = CtyptoUtil.getInstance().getChecksum(payload.getBytes(), config.secretKey);
```

## Verification of checkSum

```
AppInstance.AFConfig config = new AppInstance.AFConfig("https://microapps.appsfly.io", "1234567890123456", "92ae2562-aebc-468f-bc9e-aa3cdd9d39b1");
```     

## API Endpoint (/executor/exec)

appsfly.io have only single API endpoint to access Microservices directly. We have headers those will authenticate and manage sessions. And body contains intent with parameters.

#### Auth Headers

| Headers | Description |
| --- | --- |
| X-UUID | Unique ID of the execution. Generally it will be unique for unique user. |
| X-App-Key | App Key generated in appsfly.io publisher dashboard. |
| X-Module-Handle | Module Handle provided by service provider |
| X-Checksum | Checksum generated by appsfly.io utils |
| Content-Type | Content Type of body. Must be application/json |

#### Body Params

| Body Parameters | Description |
| --- | --- |
| Intent:String | Intent String to access the service |
| data:JSON | Provide to execute the intent |

#### Response

Microservice will respond based on the intent provided along with the checksum (X-Checksum) in the headers. The checksum is the combination of the api response and the secret key.

