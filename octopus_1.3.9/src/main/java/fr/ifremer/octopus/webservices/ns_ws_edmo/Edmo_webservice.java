/**
 * Edmo_webservice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.octopus.webservices.ns_ws_edmo;

public interface Edmo_webservice extends javax.xml.rpc.Service {
    public java.lang.String getedmo_webserviceSoap12Address();

    public Edmo_webserviceSoap getedmo_webserviceSoap12() throws javax.xml.rpc.ServiceException;

    public Edmo_webserviceSoap getedmo_webserviceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getedmo_webserviceSoapAddress();

    public Edmo_webserviceSoap getedmo_webserviceSoap() throws javax.xml.rpc.ServiceException;

    public Edmo_webserviceSoap getedmo_webserviceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
