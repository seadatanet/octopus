/**
 * Edmo_webserviceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.octopus.webservices.ns_ws_edmo;

public interface Edmo_webserviceSoap extends java.rmi.Remote {
    public java.lang.String ws_edmo_get_list() throws java.rmi.RemoteException;
    public java.lang.String ws_edmo_get_detail(java.lang.String n_code) throws java.rmi.RemoteException;
    public java.lang.String ws_edmo_name_to_code(java.lang.String c_name) throws java.rmi.RemoteException;
    public java.lang.String ws_edmo_active_countries() throws java.rmi.RemoteException;
    public java.lang.String ws_edmo_active_characters() throws java.rmi.RemoteException;
    public java.lang.String ws_edmo_get_query(java.lang.String short_list, java.lang.String free_search, java.lang.String first_character, java.lang.String country_number, java.lang.String upper_left_lat, java.lang.String upper_left_long, java.lang.String lower_right_lat, java.lang.String lower_right_long, java.lang.String last_update) throws java.rmi.RemoteException;
}
