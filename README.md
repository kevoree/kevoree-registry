kevoree-registry
================

Registry server for Kevoree (hosting/delivering Kevoree models) available at [registry.kevoree.org](http://registry.kevoree.org/v5/)  

### API
#### Retrieve model
`GET /your/package`

The type of the delivered model is defined by your request `Accept` header:

| Accept                  | Delivered type          |
| ----------------------- | ----------------------- |
| text/plain              | Traces sequence         |
| application/json        | JSON model              |
| application/vnd.xmi+xml | XMI model               |
| text/html               | Registry HTML page view |

**Example:**
```
GET / HTTP1.1
Accept: application/json


HTTP1.1 200 OK
Connection: close
Access-Control-Allow-Origin: *
Content-Type: application/json
Content-Length: 149
Date: Wed, 10 Sep 2014 08:41:19 GMT


{"class":"root:org.kevoree.ContainerRoot@0","generated_KMF_ID":"0","nodes":[],"repositories":[],"hubs":[],"mBindings":[],"groups":[],"packages":[]}
```

**Status Code:**
 - 200: model found
 - 404: model not found
 - 500: internal server error

#### Push model
`POST /deploy`

The type of the model to host is defined by your request `Content-Type` header:

| Content-Type            | Model type              |
| ----------------------- | ----------------------- |
| text/plain              | Traces sequence         |
| application/json        | JSON model              |
| application/vnd.xmi+xml | XMI model               |

**Example:**
```
POST /deploy HTTP1.1
Content-Type: application/json
Content-Length: 148

{"class":"root:org.kevoree.ContainerRoot@0","generated_KMF_ID":"0","nodes":[],"repositories":[],"hubs":[],"mBindings":[],"groups":[],"packages":[]}
HTTP1.1 201 Created
Connection: close
Content-Length: 0
Date: Wed, 10 Sep 2014 08:48:55 GMT
```

**Status Code:**
 - 200: Deploy succeed
 - 406: Content-Type is not handled (handled MIMEtypes are application/{json,vnd.xmi+xml} and text/plain)
 - 500: internal server error
