package com.networknt.light.rule.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.menu.AbstractMenuRule;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by husteve on 10/31/2014.
 */
public class GetRoleRule extends AbstractRoleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("admin")) {
            String host = (String) user.get("host");
            String hostRoles = getRoles(host);
            if(hostRoles != null) {
                List<Map<String, Object>> roleList
                        = mapper.readValue(hostRoles, new TypeReference<List<HashMap<String, Object>>>() {});
                // get all the hosts
                Set hosts = ServiceLocator.getInstance().getHostMap().keySet();

                Map<String, Object> result = new HashMap<String, Object>();
                result.put("roles", roleList);
                result.put("hosts", hosts);
                inputMap.put("result", mapper.writeValueAsString(result));
                return true;
            } else {
                inputMap.put("result", "No role can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 401);
            return false;
        }
    }
}