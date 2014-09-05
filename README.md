SAP Function Module Parameters to Java Classes
==============================================

Purpose
-------
Transform the SAP parameters to easy to use in java mainly to handle RFC calls from SAP.

Base Requirement
----------------
 * [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 * Download jco lib from [SAP](http://service.sap.com/connectors) and put into lib/jco folder
 * Configure SAP_CONNECTION.jcoDestination into startup folder, file content example:
  jco.client.lang=EN
  jco.destination.peak_limit=10
  jco.client.client=100
  jco.client.passwd=pass
  jco.client.user=user
  jco.client.sysnr=00
  jco.destination.pool_capacity=0
  jco.client.ashost=saphost
  