package com.mycompany.test_case_creator;

import com.mycompany.test_case_creator.session_factory.Session;
import com.mycompany.test_case_creator.session_factory.SessionFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author joseramon.gago
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String propertiesPath;
            Properties properties;

            String sessionType;
            String sessionHost;
            String sessionPort;
            String sessionAuthenticationPoint;
            String sessionUsername;
            String sessionPassword;
            String sessionDomain;
            String sessionProject;
            String xslxPath;
            
            SessionFactory sessionFactory;
            Session session;

            // Must be "config.properties" when generating the .jar.
            propertiesPath = "C:\\Users\\joseramon.gago\\Documents\\NetBeansProjects\\test_case_creator\\src\\main\\resources\\config.properties";
            properties = new Properties();
            properties.load(new FileInputStream(propertiesPath));

            // List of properties from the file "config.properties".
            sessionType = properties.getProperty("session_type");
            sessionHost = properties.getProperty("session_host");
            sessionPort = properties.getProperty("session_port");
            sessionAuthenticationPoint = properties.getProperty("session_authentication_point");
            sessionUsername = properties.getProperty("username");
            sessionPassword = properties.getProperty("password");
            sessionDomain = properties.getProperty("domain");
            sessionProject = properties.getProperty("project");
            xslxPath = properties.getProperty("xslxPath");
            // "Session" represents the connection to the external service.
            sessionFactory = new SessionFactory();
            session = sessionFactory.createSession(sessionType, sessionHost,
                    sessionPort, sessionAuthenticationPoint, sessionDomain, sessionProject);

            session.login(sessionUsername, sessionPassword);
            Creator creator = new Creator(session);
            
            creator.create(xslxPath);
            
            session.logout();   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
