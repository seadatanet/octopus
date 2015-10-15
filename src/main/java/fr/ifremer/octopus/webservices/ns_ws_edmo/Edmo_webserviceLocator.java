/**
 * Edmo_webserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.octopus.webservices.ns_ws_edmo;

public class Edmo_webserviceLocator extends org.apache.axis.client.Service implements Edmo_webservice {

    public Edmo_webserviceLocator() {
    	System.out.println("");
    }


    public Edmo_webserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Edmo_webserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for edmo_webserviceSoap12
    private java.lang.String edmo_webserviceSoap12_address = "http://seadatanet.maris2.nl/ws/ws_edmo.asmx";

    public java.lang.String getedmo_webserviceSoap12Address() {
        return edmo_webserviceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String edmo_webserviceSoap12WSDDServiceName = "edmo_webserviceSoap12";

    public java.lang.String getedmo_webserviceSoap12WSDDServiceName() {
        return edmo_webserviceSoap12WSDDServiceName;
    }

    public void setedmo_webserviceSoap12WSDDServiceName(java.lang.String name) {
        edmo_webserviceSoap12WSDDServiceName = name;
    }

    public Edmo_webserviceSoap getedmo_webserviceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(edmo_webserviceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getedmo_webserviceSoap12(endpoint);
    }

    public Edmo_webserviceSoap getedmo_webserviceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Edmo_webserviceSoap12Stub _stub = new Edmo_webserviceSoap12Stub(portAddress, this);
            _stub.setPortName(getedmo_webserviceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setedmo_webserviceSoap12EndpointAddress(java.lang.String address) {
        edmo_webserviceSoap12_address = address;
    }


    // Use to get a proxy class for edmo_webserviceSoap
    private java.lang.String edmo_webserviceSoap_address = "http://seadatanet.maris2.nl/ws/ws_edmo.asmx";

    public java.lang.String getedmo_webserviceSoapAddress() {
        return edmo_webserviceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String edmo_webserviceSoapWSDDServiceName = "edmo_webserviceSoap";

    public java.lang.String getedmo_webserviceSoapWSDDServiceName() {
        return edmo_webserviceSoapWSDDServiceName;
    }

    public void setedmo_webserviceSoapWSDDServiceName(java.lang.String name) {
        edmo_webserviceSoapWSDDServiceName = name;
    }

    public Edmo_webserviceSoap getedmo_webserviceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(edmo_webserviceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getedmo_webserviceSoap(endpoint);
    }

    public Edmo_webserviceSoap getedmo_webserviceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Edmo_webserviceSoapStub _stub = new Edmo_webserviceSoapStub(portAddress, this);
            _stub.setPortName(getedmo_webserviceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setedmo_webserviceSoapEndpointAddress(java.lang.String address) {
        edmo_webserviceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Edmo_webserviceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                Edmo_webserviceSoap12Stub _stub = new Edmo_webserviceSoap12Stub(new java.net.URL(edmo_webserviceSoap12_address), this);
                _stub.setPortName(getedmo_webserviceSoap12WSDDServiceName());
                return _stub;
            }
            if (Edmo_webserviceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                Edmo_webserviceSoapStub _stub = new Edmo_webserviceSoapStub(new java.net.URL(edmo_webserviceSoap_address), this);
                _stub.setPortName(getedmo_webserviceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("edmo_webserviceSoap12".equals(inputPortName)) {
            return getedmo_webserviceSoap12();
        }
        else if ("edmo_webserviceSoap".equals(inputPortName)) {
            return getedmo_webserviceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("ns_ws_edmo", "edmo_webservice");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("ns_ws_edmo", "edmo_webserviceSoap12"));
            ports.add(new javax.xml.namespace.QName("ns_ws_edmo", "edmo_webserviceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("edmo_webserviceSoap12".equals(portName)) {
            setedmo_webserviceSoap12EndpointAddress(address);
        }
        else 
if ("edmo_webserviceSoap".equals(portName)) {
            setedmo_webserviceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
