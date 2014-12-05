package com.networknt.light.rule.page;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 */
public class UpdPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("pageAdmin")) {
                error = "Role owner or admin or pageAdmin is required to update page";
                inputMap.put("responseCode", 401);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only update page from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument page = null;
                    if(rid != null) {
                        page = DbService.getODocumentByRid(rid);
                        if(page != null) {
                            int inputVersion = (int)data.get("@version");
                            int storedVersion = page.field("@version");
                            if(inputVersion != storedVersion) {
                                inputMap.put("responseCode", 400);
                                error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                            } else {
                                Map eventMap = getEventMap(inputMap);
                                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                                inputMap.put("eventMap", eventMap);
                                eventData.putAll((Map<String, Object>)inputMap.get("data"));
                                eventData.put("updateDate", new java.util.Date());
                                eventData.put("updateUserId", user.get("userId"));
                            }
                        } else {
                            error = "Page with @rid " + rid + " cannot be found.";
                            inputMap.put("responseCode", 404);
                        }
                    } else {
                        error = "@rid is required";
                        inputMap.put("responseCode", 400);
                    }
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}