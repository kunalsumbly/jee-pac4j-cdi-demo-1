package org.pac4j.demo.jee;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

public class CustomAuthorizer extends ProfileAuthorizer<CommonProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) throws HttpAction {
    	System.out.println("CustomAuthorizer :: isAuthorized");
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final CommonProfile profile) {
    	System.out.println("CustomeAuthorizer :: isProfileAuthorized");
        if (profile == null) {
            return false;
        }
        return StringUtils.startsWith(profile.getUsername(), "jle");
    }
}
