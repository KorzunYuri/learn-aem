package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.domains.error.JsonErrorInfo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  provides methods to unify JSON exchange between services,
 *  e.g. common exchange format, like unified error handling
 */
public interface JsonUnifier {

    /**
     *  add mandatory fields to a JSON response, if needed
     */
    JSONObject finalize(JSONObject jsonObject) throws JSONException;

    /**
     *  add unified error information to a JSON response, if needed
     */
    JSONObject finalize(JSONObject jsonObject, Exception e) throws JSONException;

    /**
     *  provide info in case there was no exception but the result if not what was expected
     */
    JSONObject finalize(JSONObject jsonObject, JsonErrorInfo errorInfo) throws JSONException;
}
