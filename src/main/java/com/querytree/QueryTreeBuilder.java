package com.querytree;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Simple factory to figure out what QueryTree implementation to be returned based on the content.
 */
public class QueryTreeBuilder {

    public static QueryTree constructQueryTree(String jsonOrXMLContent) {
        if(jsonOrXMLContent.startsWith("<")) {
            try {
                return XMLTagsTree.constructTagsTree(jsonOrXMLContent);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return JSONTagsTree.constructTagsTree(jsonOrXMLContent);
        }
    }
}
