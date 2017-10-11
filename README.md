# About appsfly.io Dev Kit Java Utils
Java Utils contains resources to help communicate with appsfly.io secure server through encryption. 

appsfly.io does not whitelist IPs, instead a checkSum should be generated with the Secret Key. To get a Secret Key contact integrations@appsfly.io

##  Get Started
To start posting messages securely we need the following:

| Key | Description |
| --- | --- |
| Module Handle | Unique handle of the micro module. This will be provided by the service provider. |
| Secret Key | Secret Key is required for encryption. Secret Key should be generated on the Appsfly publisher dashboard. |
| App Key | Application key to identify the publisher instance. |

You will have to generate a checkSum from the parameters, headers with the secret key provided by appsfly.io.

## Configuration
Configure the app using the following parameters:

* REPO_URL - currently "https://microapps.appsfly.io"
* SECRET_KEY - generated secret key, contact integrations@appsfly.io for a secret key
* APP_KEY - assigned Application key. Eg: "92ae2562-aebc-468f-009e-aa300d9d39b1"

```
AppInstance.AFConfig config = new AppInstance.AFConfig("REPO_URL", "SECRET_KEY", "APP_KEY");
```    
## Package
The SDK can be included to handle the encryption and decryption along with checkSum generation and verification.
You can use it to avoid boiler plate code. If not, you can use the api endpoint.

```
AppInstance travelProvider = new AppInstance(config, "com.domain.ms-travel");
travelProvider.exec("doBooking",  new JSONObject() {{
    //Set Params Here
    // We will take care of checksum
}}, new Callback() {
    @Override
    public void onResponse(JSONObject response) {
        System.out.println(response);
        // Payment Done Response
        // We have already verified the checksum from you
    }

    @Override
    public void onError(JSONObject error) {
        System.out.println(error);
    }
});
```

## API Endpoint ( https://microapps.appsfly.io/executor/exec )

appsfly.io exposes a single API endpoint to access Microservices directly. Headers are used for authentication and sessions will be managed accordingly.

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

### Generation of checksum

```
// Generate payload string
String payload = body + "|" +  microModuleId + "|" + config.appKey + "|" + userID;
// Generate checksum
String checksum = CtyptoUtil.getInstance().getChecksum(payload.getBytes(), config.secretKey);

// HTTP Request
Request request = new Request.Builder()
                .url(this.config.repoUrl+"/executor/exec")
                .addHeader("X-Module-Handle", microModuleId)
                .addHeader("X-App-Key", config.appKey)
                .addHeader("X-Checksum", checksum)
                .addHeader("X-UUID", userID)
                .post(RequestBody.create(JSON, body.toString()))
                .build();
```

### Verification of checkSum

```
// Fetch Checksum from headers
String checksum = response.headers().get("X-Checksum");
if(checksum!=null){
    // Verify checksum with body and secret key
    boolean verified = CtyptoUtil.getInstance().verifychecksum(response.body().bytes(), checksum, config.secretKey);
    if (verified){
        callback.onResponse(new JSONObject(response.body().bytes()));
    }
    else{
        callback.onError(new JSONObject(){{
            put("message", "Checksum Validation Failed");
        }});
    }
}
``` 
