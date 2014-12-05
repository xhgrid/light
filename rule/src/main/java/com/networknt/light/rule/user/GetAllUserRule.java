package com.networknt.light.rule.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.injector.main.feed.FeedRule;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve on 26/10/14.
 */
public class GetAllUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("userAdmin") && !roles.contains("admin")) {
                inputMap.put("error", "Role owner or admin or userAdmin is required to get all users");
                inputMap.put("responseCode", 401);
                return false;
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        inputMap.put("error", "User can only get all users from host: " + host);
                        inputMap.put("responseCode", 401);
                        return false;
                    }
                } else {
                    // retrieve everything as this is the owner
                    data.remove("host"); // removed the host added by RestHandler.
                }
            }
        }
        long total = getTotalNumberUserFromDb(data);
        if(total > 0) {
            String json = getUserFromDb(data);
            List<Map<String, Object>> users
                = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>() {});
            // get all the roles
            List<String> roles = getRoles();
            // get all the hosts
            Set hosts = ServiceLocator.getInstance().getHostMap().keySet();

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("roles", roles);
            result.put("hosts", hosts);
            result.put("users", users);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("error", "No user can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}