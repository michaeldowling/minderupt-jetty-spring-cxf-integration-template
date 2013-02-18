I use a REST Filter to handle authentication of a simple API Key and API Key Token scheme that is quite common with service implementations.  The basic idea here is:

* The API Key is like a caller's "username"
* The API Key Token is the sha-256 hashed output of the combination of the API Key, the caller's "secret key", and the entire content of the incoming payload.
* If the given API Key Token match what the filter expects it to be, it allows the request to proceed.  Otherwise, return a 401 Unauthorized.

This initial implementation allows for a simple key-value map to be used as a user store.  This works well for me when I'm just getting something started, but obviously this architecture allows for a plugin approach.  When you're ready to tackle authentication, you can plug in something that works for your environment.  (I welcome any contributions!  i.e. a standard CAS template)



