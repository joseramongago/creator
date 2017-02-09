package com.mycompany.test_case_creator.session_factory;

/**
 * Concrete session factory.
 *
 * @author panel
 */
public class SessionFactory {

    /**
     *
     */
    public SessionFactory() {
    }

    /**
     * Returns an object that represents a concrete session.
     *
     * @param sessionType
     * @param sessionHost
     * @param sessionPort
     * @param sessionAuthenticationPoint
     * @param domain
     * @param project
     * @return
     */
    public Session createSession(String sessionType, String sessionHost,
            String sessionPort, String sessionAuthenticationPoint,
            String domain, String project) {
        if (sessionType.equalsIgnoreCase("AlmSession")) {
            return new AlmSession(sessionHost, sessionPort,
                    sessionAuthenticationPoint, domain, project);
        } else {
            return null;
        }
    }
}
