package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.domains.error.JsonErrorInfo;
import com.adobe.aem.guides.wknd.core.services.JsonUnifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(
        service = JsonUnifier.class
)
public class JsonUnifierImpl implements JsonUnifier {
    
    @Override
    public JSONObject finalize(JSONObject jsonObject) throws JSONException{
        return finalize(jsonObject, new JsonErrorInfo());
    }

    @Override
    public JSONObject finalize(JSONObject jsonObject, Exception e) throws JSONException {
        return finalize(jsonObject,
                    new JsonErrorInfo(
                            e.getMessage(),
                            e.getCause().getMessage()
                    ));
    }

    @Override
    public JSONObject finalize(JSONObject jsonObject, JsonErrorInfo errorInfo) throws JSONException {
        JSONObject errJson = new JSONObject();
        errJson.put("message",  errorInfo.getMessage());
        errJson.put("cause",    errorInfo.getCauseMessage());
        jsonObject.put("error", errJson);
        return jsonObject;
    }

}
