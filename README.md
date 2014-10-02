SAP Function Module Parameters to Java Classes
==============================================

Purpose
-------
The purpose of this program is create a easy to use way to handle RFC call from SAP an also call SAP function modules. The program get SAP function modules parameters and transform to JAVA classes and also generate classes to handle RFC calls from SAP and to call SAP function module from JAVA.

Base Requirement
----------------
 * [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 * Download jco lib from [SAP](http://service.sap.com/connectors) and put into lib/jco folder
 * Configure SAP_CONNECTION.jcoDestination into startup folder, [example](https://github.com/lugaid/sap-fm-to-java-class/blob/master/SAP_CONNECTION.txt).