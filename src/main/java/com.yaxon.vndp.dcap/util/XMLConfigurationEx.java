package com.yaxon.vndp.dcap.util;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 20:09
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

import com.yaxon.vndp.dcap.io.FileResource;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.List;


/**
 * 增强版的XML配置文件类，该类相对于Apache Commons中提供的XMLConfiguration增加了如下功能：
 * 1、文件名支持以"classpath:"开头，从而从类路径下寻找相应的配置文件并加载。
 * 2、配置文件中允许通过 <import relatedResource="..."/> 的方式包含另外一个配置文件，不过只是将被包含文件的根元素内的内容包含进来。
 */
public class XMLConfigurationEx extends XMLConfiguration {
    protected static Logger logger = LoggerFactory.getLogger(XMLConfigurationEx.class);

    public static final String CLASS_PATH_PREFIX = "classpath:";

    public XMLConfigurationEx() {
    }

    public XMLConfigurationEx(String fileName) throws ConfigurationException {
        load(fileName);
    }

    @Override
    public void load(String fileName) throws ConfigurationException {
        try {
            Document doc = loadConfigFile(new FileResource(fileName));
            super.load(new StringReader(doc.asXML()));
        } catch (Exception e) {
            throw new ConfigurationException("加载配置文件失败: " + fileName, e);
        }
    }

    private Document loadConfigFile(FileResource resource) throws Exception {
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document document = reader.read(resource.getInputStream());
        Element root = document.getRootElement();

        treeWalk(root, resource);

        return document;
    }


    /**
     * 遍历子元素，将由元素 <import relatedResource="..."/> 标识的XML文档内容包含进来。
     *
     * @param element
     */
    private void treeWalk(Element element, FileResource relatedResource) throws Exception {
        List children = element.content();

        int i = 0;
        while (i < children.size()) {
            Object n = children.get(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                if ("import".equals(e.getName())) {
                    String file = e.attribute("resource").getValue();
                    FileResource fr = new FileResource(relatedResource, file);
                    Document includedDoc = null;
                    try {
                        includedDoc = loadConfigFile(fr);
                    } finally {
                        if (fr != null) {
                            fr.close();
                        }
                    }

                    children.remove(i);
                    List includedElements = includedDoc.getRootElement().elements();
                    for (Object o : includedElements) {
                        children.add(i++, o);
                    }
                } else {
                    treeWalk(e, relatedResource);
                    i++;
                }
            } else {
                i++;
            }
        }
    }
}
