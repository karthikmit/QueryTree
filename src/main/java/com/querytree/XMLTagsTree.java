package com.querytree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XMLTagsTree internally uses DOM Parser and creates a Trie of DOM Nodes based on TagNames.
 *      Using DOM Parser to access data from XML is tedious and error prone. XPath solves that problem but comes with performance issues.
 *      XMLTagsTree solves the problem of XPath slowness by building a Trie structure of DOM Nodes and effectively handles many XPath like cases.
 *      Certain XPath queries might need traversing all the child nodes recursively and performance may not be great in those cases.
 */

class XMLTagsTree implements QueryTree{

    private XMLTagHolder root;
    private XMLTagsTree(Object domPointer) {
        Node node = (Node) domPointer;
        this.root = new XMLTagHolder(node.getNodeName(), node);
        setUpTagHolder(this.root);
    }

    private static XMLTagsTree constructTagsTree(Document document) {
        Element rootElement = document.getDocumentElement();
        return new XMLTagsTree(rootElement);
    }

    static XMLTagsTree constructTagsTree(String xmlContent) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(xmlContent)));
        if(doc.getDocumentElement() != null) {
            doc.getDocumentElement().normalize();
        }

        return constructTagsTree(doc);
    }

    @Override
    public Object eval(String query) {
        String[] queryTokens = query.split("->");
        XMLTagHolder current = root;
        for(int i = 1; i < queryTokens.length; i++) {

            String queryToken = queryTokens[i];
            if(queryToken.contains("[")) {
                String[] split = queryToken.split("\\[");
                queryToken = split[0];

                String indexSuffix = split[1];
                indexSuffix = indexSuffix.split("\\]")[0];
                int index = Integer.parseInt(indexSuffix);

                current = current.getChildren().get(queryToken).get(index);
            } else if(current.getChildren().containsKey(queryToken)) {
                current = current.getChildren().get(queryToken).get(0);
            } else {
                return null;
            }
        }
        return current.getDomPointer();
    }

    private void setUpTagHolder(XMLTagHolder tagHolder) {
        Node rootNode = (Node) tagHolder.getDomPointer();
        // Construct Children,
        NodeList childNodes = rootNode.getChildNodes();

        for(int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String tagName = childNode.getNodeName();

            XMLTagHolder childTagHolder = new XMLTagHolder(tagName, childNode);
            tagHolder.addChild(tagName, childTagHolder);
            setUpTagHolder(childTagHolder);
        }
    }

    public String toString() {
        return root.toString();
    }


    private static class XMLTagHolder {
        private String tagName;
        private Map<String, List<XMLTagHolder>> children = new HashMap<>();
        private Object domPointer = null;

        XMLTagHolder(String tagName, Object domPointer) {
            this.tagName = tagName;
            this.domPointer = domPointer;
        }

        void addChild(String tagName, XMLTagHolder tagHolder) {
            if(!this.children.containsKey(tagName)) {
                this.children.put(tagName, new ArrayList<XMLTagHolder>());
            }

            List<XMLTagHolder> tagHolders = this.children.get(tagName);
            tagHolders.add(tagHolder);
        }

        public String toString() {
            String childTagHoldersString = "";
            for(String key : children.keySet()) {
                List<XMLTagHolder> tagHolder = children.get(key);
                childTagHoldersString = childTagHoldersString.concat(tagHolder.toString());
            }
            return this.tagName + "\n" + childTagHoldersString;
        }

        Object getDomPointer() {
            return domPointer;
        }

        public void setDomPointer(Object domPointer) {
            this.domPointer = domPointer;
        }

        Map<String, List<XMLTagHolder>> getChildren() {
            return children;
        }

        public void setChildren(Map<String, List<XMLTagHolder>> children) {
            this.children = children;
        }
    }
}
