package com.mycompany.test_case_creator.session_factory;

/**
 * Session interface.
 *
 * @author panel
 */
public interface Session {

    /**
     *
     * @param username
     * @param password
     * @throws Exception
     */
    public void login(String username, String password)
            throws Exception;

    /**
     *
     * @throws Exception
     */
    public void logout()
            throws Exception;

    /**
     *
     * @return @throws Exception
     */
    public boolean isAuthenticated()
            throws Exception;
    
    public void update(String pathCase, String testName, String status, String pathEvidence)
            throws Exception;
}
