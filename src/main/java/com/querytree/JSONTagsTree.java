package com.querytree;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * QueryTree implementation for JSON content.
 */
class JSONTagsTree implements QueryTree{

    private JSONTagHolder root;
    private JSONTagsTree(Object rootReference) {
        this.root = new JSONTagHolder("", rootReference);
        setUpTagHolder(this.root);
    }

    private static JSONTagsTree constructTagsTree(Object rootElement) {
        return new JSONTagsTree(rootElement);
    }

    static JSONTagsTree constructTagsTree(String jsonContent) {
        Map rootElement = new Gson().fromJson(jsonContent, Map.class);
        return constructTagsTree(rootElement);
    }

    /**
     * Returns null, if query can't be processed successfully.
     * @param query
     * @return
     */
    @Override
    public Object eval(String query) {
        String[] queryTokens = query.split("->");
        JSONTagHolder current = root;
        for(int i = 0; i < queryTokens.length; i++) {

            String queryToken = queryTokens[i];
            if(queryToken.contains("[")) {
                String[] split = queryToken.split("\\[");
                queryToken = split[0];

                String indexSuffix = split[1];
                indexSuffix = indexSuffix.split("\\]")[0];
                int index = Integer.parseInt(indexSuffix);

                JSONTagHolder tagHolder = current.getChildren().get(queryToken);
                current = tagHolder.getChildren().get(indexSuffix);
            } else if(current.getChildren().containsKey(queryToken)) {
                current = current.getChildren().get(queryToken);
            } else {
                return null;
            }
        }
        return current.getObjectReference();
    }

    private void setUpTagHolder(JSONTagHolder tagHolder) {
        Object rootObject = tagHolder.getObjectReference();

        if(rootObject instanceof List) {
            List<Object> rootList = (List<Object>) rootObject;
            int size = rootList.size();
            for(int index = 0; index < size; index++) {
                Object childObject = rootList.get(index);
                String key = String.valueOf(index);
                JSONTagHolder childHolder = new JSONTagHolder(key, childObject);
                tagHolder.addChild(key, childHolder);
                setUpTagHolder(childHolder);
            }
        } else if(rootObject instanceof Map) {
            Map<String, Object> rootMap = (Map<String, Object>) rootObject;

            Set<String> keySet = rootMap.keySet();
            for(String key : keySet) {
                Object childObject = rootMap.get(key);
                JSONTagHolder childHolder = new JSONTagHolder(key, childObject);
                tagHolder.addChild(key, childHolder);
                setUpTagHolder(childHolder);
            }
        } else {
            // No need to do anything. :)
        }
    }

    public String toString() {
        return root.toString();
    }

    private static class JSONTagHolder {
        private String tagName;
        private Map<String, JSONTagHolder> children = new HashMap<>();
        private Object objectReference = null;

        JSONTagHolder(String tagName, Object domPointer) {
            this.tagName = tagName;
            this.objectReference = domPointer;
        }

        void addChild(String tagName, JSONTagHolder tagHolder) {
            this.children.put(tagName, tagHolder);
        }

        public String toString() {
            /*String childTagHoldersString = "";
            for(String key : children.keySet()) {
                List<XMLTagHolder> tagHolder = children.get(key);
                childTagHoldersString = childTagHoldersString.concat(tagHolder.toString());
            }
            return this.tagName + "\n" + childTagHoldersString;*/
            return "";
        }

        Object getObjectReference() {
            return objectReference;
        }

        public void setObjectReference(Object objectReference) {
            this.objectReference = objectReference;
        }

        Map<String, JSONTagHolder> getChildren() {
            return children;
        }

        public void setChildren(Map<String, JSONTagHolder> children) {
            this.children = children;
        }
    }
}
