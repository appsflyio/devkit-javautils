# About appsfly.io Dev Kit Java Utils
This library contains resources to help communicate with appsfly.io execution server.
For all communications with execution server, your application should be registered and a secret key needs to be generated. 

Please contact integrations@appsfly.io for your credientials.

#  Get Started
#### Application Params <a name="SECRET_KEY"></a><a name="APP_KEY"></a><a name="EXECUTOR_URL"></a>
| Key | Description |
| --- | --- |
| SECRET_KEY   | Secret Key is required for encryption. Secret Key should be generated on the Appsfly publisher dashboard |
| APP_KEY  | Application key to identify the publisher instance|
| EXECUTOR_URL | Url to reach appsfly.io Microservices |

**NOTE:** Above params are needed for checksum generation. Please refer to the methods mention below.

#### Micro Module Params <a name="MODULE_HANDLE"></a> <a name="UUID"></a>

| Key | Description |
| --- | --- |
| MODULE_HANDLE  | Each micromodule of a service provider is identified by MODULE_HANDLE |
| UUID  | UniqueID to identify user session|

#### Intent Params <a name="INTENT"></a> <a name="PAYLOAD"></a>
| Key | Description |
| --- | --- |
| INTENT | Intent is like an endpoint you are accessing to send message |
| PAYLOAD | Data payload |

# Integration options  

### Option 1: SDK
The SDK can be included to handle authorization. There is no need for you to handle checksum generation and verification.

##### Configuration
```
AppInstance.AFConfig config = new AppInstance.AFConfig("EXECUTOR_URL", "SECRET_KEY", "APP_KEY");
```  
##### Execution
```
AppInstance travelProvider = new AppInstance(config, "MODULE_HANDLE");
travelProvider.exec("INTENT", JSONObject("PAYLOAD"), "UUID", new Callback() {
    @Override
    public void onResponse(JSONObject response) {
        // We have already verified the checksum from you
    }

    @Override
    public void onError(JSONObject error) {
      // Handle error
    }
});
```

### Option 2: API Endpoint
appsfly.io exposes a single API endpoint to access Microservices directly.

##### Endpoint
https://microapps.appsfly.io/executor/exec

##### Method
POST

##### Headers
| Header | Description |
| --- | --- |
| X-UUID | [UUID](#UUID) |
| X-App-Key | [APP_KEY](#APP_KEY)|
| X-Module-Handle | [MODULE_HANDLE](#MODULE_HANDLE)|
| X-Checksum | CHECKSUM. Please go through [this gist]() to generate checksum. |
| Content-Type | Must be "application/json" |

##### Body
[INTENT](#INTENT), [PAYLOAD](#PAYLOAD)
``` 
{
  "intent":"INTENT",
  "data":"PAYLOAD"
 } 
 ```

##### Response
Response format will be dependent on microservice. Please go through [this documentation]() for different microservices.
