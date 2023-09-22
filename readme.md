[Dynamic Web TWAIN](https://www.dynamsoft.com/web-twain/overview) is an SDK which enables document scanning from browsers. Under its hood, a backend service named Dynamsoft Service is running to communicate with scanners via protocols like TWAIN, WIA, eSCL, SANE and ICA. The service runs on Windows, macOS and Linux.

Starting from Dynamic Web TWAIN v18.4, Dynamsoft Service will be accessible via REST APIs so that we can use different programming languages to create document scanning applications.

In this article, we are going to talk about how to access document scanners using the REST APIs in Java. A desktop app is built using JavaFX.

Some advantages of using the REST APIs of Dynamsoft Service for document scanning in Java:

* It is cross-platform supporting multiple scanning protocols.
* If we directly call TWAIN API, we have to use JRE 32-bit as most drivers are 32-bit.


## Prerequisites

* You need to install Dynamsoft Service on your device. You can download the preview version for Windows [here](https://www.dynamsoft.com/codepool/downloads/DynamsoftServiceSetup.msi).
* A license for Dynamic Web TWAIN is needed. You can apply for a license [here](https://www.dynamsoft.com/customer/license/trialLicense?product=dwt).


