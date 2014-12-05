package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by husteve on 10/17/2014.
 */
public class AddRoleEvRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addRole(data);
        return true;
    }
}