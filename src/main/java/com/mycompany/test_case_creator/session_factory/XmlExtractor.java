package com.mycompany.test_case_creator.session_factory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.hp.qc.web.restapi.docexamples.docexamples.infrastructure.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ALM response extractor.
 *
 * @author panel
 */
public class XmlExtractor {

    /**
     *
     */
    public XmlExtractor() {
    }

    /**
     * Given an XML node, returns an XML string with the contents.
     *
     * @param node
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public String getNodeString(Node node)
            throws TransformerConfigurationException, TransformerException {
        StringWriter writer;
        Transformer transformer;
        String output;
        String ret;

        writer = new StringWriter();
        transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        output = writer.toString();
        ret = output.substring(output.indexOf("?>") + 2);

        return ret;
    }

    /**
     * Given an XML string and xpath, returns a list of XML nodes.
     *
     * @param xml
     * @param xPathNodes
     * @return
     * @throws UnsupportedEncodingException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public NodeList getNodeList(String xml, String xPathNodes)
            throws UnsupportedEncodingException, SAXException, IOException,
            ParserConfigurationException, XPathExpressionException {
        Document doc;
        XPathExpression expr;
        Object result;
        NodeList ret;

        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        expr = XPathFactory.newInstance().newXPath().compile(xPathNodes);
        result = expr.evaluate(doc, XPathConstants.NODESET);
        ret = (NodeList) result;

        return ret;
    }
    
    public String generateSingleFieldUpdateXml(String field, String value) {
        return "<Entity Type=\"test-instance\"><Fields>"
                + Constants.generateFieldXml(field, value)
                + "</Fields></Entity>";
    }
}
