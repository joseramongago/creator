package com.mycompany.test_case_creator.session_factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.hp.qc.web.restapi.docexamples.docexamples.infrastructure.Response;
import org.hp.qc.web.restapi.docexamples.docexamples.infrastructure.RestConnector;
import org.w3c.dom.Node;

/**
 * Connection to the external service.
 *
 * @author panel
 */
public class AlmSession implements Session {

    private final RestConnector con;
    private final String authenticationPoint;
    private final Map<String, String> requestHeaders;
    private final XmlExtractor xmlExtractor;
    
    /**
     *
     * @param sessionHost
     * @param sessionPort
     * @param sessionAuthenticationPoint
     * @param sessionDomain
     * @param sessionProject
     */
    public AlmSession(String sessionHost, String sessionPort,
            String sessionAuthenticationPoint, String sessionDomain, String sessionProject) {
        if (!sessionPort.isEmpty()) {
            sessionPort = ":" + sessionPort;
        }

        con = RestConnector.getInstance().init(
                new HashMap<>(),
                "http://"
                + sessionHost
                + sessionPort
                + "/qcbin",
                sessionDomain,
                sessionProject);
        this.authenticationPoint = "http://" + sessionHost + sessionPort + "/qcbin/" + sessionAuthenticationPoint;
        this.requestHeaders = new HashMap<>();
        this.xmlExtractor = new XmlExtractor();
    }

    /**
     * @param username
     * @param password
     * @throws Exception
     */
    @Override
    public void login(String username, String password)
            throws Exception {
        HttpResponse serverResponse;
       
        Encryptor e = new Encryptor();
        String passEncrypted = e.encrypt(password); 

        serverResponse = con.httpPost1(this.authenticationPoint, username, 
                passEncrypted , new HashMap<String, String>());
        // El log in en el REST de la empresa nos lleva a página en blanco que
        // devuelve error 500 pero es correcto.
        if (serverResponse.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_INTERNAL_ERROR) {
            System.out.println("* Error: unexpected error when loggin in.");
        }
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void logout()
            throws Exception {
        Response serverResponse;

        serverResponse = con.httpGet(con.buildUrl("authentication-point/logout"),
                null, null);

        if (serverResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("* Error: unexpected error when loggin out.");
        }
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    public boolean isAuthenticated()
            throws Exception {
        String isAuthenticateUrl;
        Response serverResponse;
        boolean ret;

        isAuthenticateUrl = con.buildUrl("rest/is-authenticated");
        serverResponse = con.httpGet(isAuthenticateUrl, null, null);

        if (serverResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("* Error: not authenticated.");
            ret = false;
        } else {
            ret = true;
        }

        return ret;
    }

    @Override
    public void update(String pathCase, String testName, String status, String pathEvidence) throws Exception {
        String folderID = getFolderTestID(pathCase);
        String testID = getTestID(folderID, testName);
        String testCaseID = getTestCaseID(testID);
        
        updateStatus(testCaseID, status);
        attachFile(testCaseID, pathEvidence);
    }
   
    private void attachFile(String testCaseID, String pathEvidence) throws IOException, Exception {
        String requirementsUrl = con.buildEntityCollectionUrl("test-instance") + "/" + testCaseID;
        
        if (pathEvidence.contains("\""))
            pathEvidence = pathEvidence.replaceAll("\"", "");
        
        String[] pathsToFile = pathEvidence.split(";");
        
        for(String path : pathsToFile) {
            byte[] fileContent = readFile(path);

            Response response = con.httpPost(requirementsUrl + "/attachments", fileContent, requestHeaders);
            if (response.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
                throw new Exception(response.toString());
            }
        }
    }
    
    private byte[] readFile(String pathToFile) throws IOException {
        String fileName = null;

        if (pathToFile.contains("\\"))// || pathToFile.contains("/"))
            fileName = pathToFile.substring(pathToFile.lastIndexOf("\\") + 1, pathToFile.length());
        else
            fileName = pathToFile;
        requestHeaders.put("Slug", fileName);
        requestHeaders.put("Content-Type", "application/octet-stream");

        File fileToAttach = new File(pathToFile);
        FileInputStream fileToAttachFis = new FileInputStream(fileToAttach);
        byte[] fileContent = new byte[(int)fileToAttach.length()];
        fileToAttachFis.read(fileContent);
        fileToAttachFis.close();
        
        return fileContent;
    }
    
    private void updateStatus(String testCaseID, String status) throws Exception {
        String requirementsUrl = con.buildEntityCollectionUrl("test-instance");
        String updatedEntityXml = xmlExtractor.generateSingleFieldUpdateXml("status", status);

        requirementsUrl += "/" + testCaseID;
        
        requestHeaders.put("Content-Type", "application/xml");
        requestHeaders.put("Accept", "application/xml");        
        
        Response putResponse = con.httpPut(requirementsUrl, updatedEntityXml.getBytes(), requestHeaders);
        
        if (putResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(putResponse.toString());
        }
    }
    
    private String getTestCaseID(String testID) throws Exception {
        String requirementsUrl = con.buildEntityCollectionUrl("test-instance");
        StringBuilder b = new StringBuilder();
        Response serverResponse;
        String xmlReturn;
        Node testCaseNode;  
        
        b.append("fields=id&query={test-id[");
        b.append(testID);
        b.append("]}");
        
        serverResponse = con.httpGet(requirementsUrl, b.toString(), requestHeaders);
        xmlReturn = new String(serverResponse.toString().getBytes(), "UTF-8");
        testCaseNode = xmlExtractor.getNodeList(xmlReturn, "//Field[@Name='id']//Value").item(0);
        return testCaseNode.getTextContent();
    }
    
    private String getTestID(String folderID, String testName) throws Exception {
        String requirementsUrl = con.buildEntityCollectionUrl("test");
        StringBuilder b = new StringBuilder();
        Response serverResponse;
        String xmlReturn;
        Node testNode;  
        
        b.append("fields=id&query={parent-id[");
        b.append(folderID);
        b.append("];name[");
        b.append(URLEncoder.encode("'","UTF-8"));
        b.append(URLEncoder.encode(testName,"UTF-8"));
        b.append(URLEncoder.encode("'","UTF-8"));
        b.append("];cycle-id[12404]}");//cycle-id[12404] quitarlo que es por la duplicidad de IDs 
        
        serverResponse = con.httpGet(requirementsUrl, b.toString(), requestHeaders);
        xmlReturn = new String(serverResponse.toString().getBytes(), "UTF-8");
        testNode = xmlExtractor.getNodeList(xmlReturn, "//Field[@Name='id']//Value").item(0);
        return testNode.getTextContent();
    }
    
    private String getFolderTestID(String pathCase) throws Exception {
        String requirementsUrl = con.buildEntityCollectionUrl("test-folder");
        StringBuilder b;
        Response serverResponse;
        String xmlReturn;
        String testFolderID = "";
        Node directoryNode;
        
        String[] directories = pathCase.split("\\\\");

        for (int i = 0; i < directories.length; ++i) {
            b = new StringBuilder();
            b.append("fields=id&query={name[").append(directories[i]).append("]");
            if (i != 0)
                b.append(";parent-id[").append(testFolderID).append("]");
            b.append("}");
            serverResponse = con.httpGet(requirementsUrl, b.toString(), requestHeaders);
            xmlReturn = new String(serverResponse.toString().getBytes(), "UTF-8");
            directoryNode = xmlExtractor.getNodeList(xmlReturn, "//Field[@Name='id']//Value").item(0);
            testFolderID = directoryNode.getTextContent();
        }
        
        return testFolderID;
    }
}
