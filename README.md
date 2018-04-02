# About Java Integration Kit
This library contains resources to help communicate with appsfly.io execution server.
For all communications with execution server, your application should be registered and a secret key needs to be generated. 

Please contact integrations@appsfly.io for your credientials.

#  Get Started
 <a name="SECRET_KEY"></a><a name="APP_KEY"></a><a name="EXECUTOR_URL"></a>
#### Application Params
| Key | Description |
| --- | --- |
| SECRET_KEY   | Secret Key is required for encryption. Secret Key should be generated on the Appsfly publisher dashboard |
| APP_KEY  | Application key to identify the publisher instance|
| EXECUTOR_URL | Url to reach appsfly.io Microservices |

**NOTE:** Above params are needed for checksum generation. Please refer to the methods mention below.

 <a name="MODULE_HANDLE"></a> <a name="UUID"></a>
#### Micro Module Params

| Key | Description |
| --- | --- |
| MODULE_HANDLE  | Each micromodule of a service provider is identified by MODULE_HANDLE |
| UUID  | UniqueID to identify user session|

 <a name="INTENT"></a> <a name="PAYLOAD"></a>
#### Intent Params
| Key | Description |
| --- | --- |
| INTENT | Intent is like an endpoint you are accessing to send messages |
| PAYLOAD | Data payload |

# Integration options  

### Option 1: SDK
The SDK can be included to handle authorization. There is no need for you to handle checksum generation and verification.

#### Setup SDK

Maven
###### Step 1. Add repo to your root pom.xml
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		 <url>https://jitpack.io</url>
	</repository>
</repositories>
```

###### Step 2. Add the dependency
```
<dependency>
	 <groupId>com.github.appsflyio</groupId>
	 <artifactId>java-integration-kit</artifactId>
	 <version>0.0.11</version>
</dependency>
```

Gradle
###### Step 1. Add it in your root build.gradle at the end of repositories
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

###### Step 2. Add the dependency
```
dependencies {
	compile 'com.github.appsflyio:devkit-javautils:0.0.11'
}

```

#### Configuration
```
AppInstance.AFConfig config = new AppInstance.AFConfig("EXECUTOR_URL", "SECRET_KEY", "APP_KEY");
```  
#### Execution Asynchronous
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
#### Execution Synchronous
```
AppInstance travelProvider = new AppInstance(config, "MODULE_HANDLE");
try {
            Object object = travelProvider.execSync("INTENT", JSONObject("PAYLOAD"), "UUID");
        } catch (AppsflyException e) {
            e.printStackTrace();
        }

```
### Option 2: API Endpoint
appsfly.io exposes a single API endpoint to access Microservices directly.

#### Endpoint
[EXECUTOR_URL](#EXECUTOR_URL)/executor/exec

#### Method
POST

#### Headers
| Header | Description |
| --- | --- |
| X-UUID | [UUID](#UUID) |
| X-App-Key | [APP_KEY](#APP_KEY)|
| X-Module-Handle | [MODULE_HANDLE](#MODULE_HANDLE)|
| X-Encrypted | BOOLEAN |
| Content-Type | Must be "text/plain" |

#### Body
```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhZl9jbGFpbSI6IntcImludGVudFwiOlwiSU5URU5UXCIsXCJkYXRhXCI6XCJQQVlMT0FEXCJ9In0.ZPUfElCCO2FiSQwtur6t80kHFTOzsvnJGQ-j_70WZ0k
```
Body must have the encrypted checksum for the following JSON. Please use [java-jwt](https://github.com/auth0/java-jwt) to generate and verify checksum.
[INTENT](#INTENT), [PAYLOAD](#PAYLOAD)
``` 
{
  "intent":"INTENT",
  "data":"PAYLOAD"
} 
 ```
Covert the above JSON to string and append it to key "af_claim" as follows:
``` 
{"af_claim": "{\"intent\":\"INTENT\", \"data\":\"PAYLOAD\"}"}
 ```

----------------------------------------

### Micro Service Response
Response format will be dependent on microservice. Please go through [this documentation](https://github.com/appsflyio/micro-module-documentations) for different microservices.
