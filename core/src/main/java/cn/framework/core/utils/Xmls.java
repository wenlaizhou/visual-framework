package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * xml文件操作相应工具类
 *
 * @author wenlai
 */
public final class Xmls {

    /**
     * @param documentPath
     *
     * @return
     */
    public static Document buildDocument(String documentPath) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(Files.readFileOrResource(Property.fill(documentPath), Projects.MAIN_CLASS_LOADER)));
            ArrayList<Node> includeNodes = xpathNodesArray("//include", document);
            if (includeNodes != null && includeNodes.size() > 0) {
                includeNodes.parallelStream().forEach(n -> {
                    if (Files.existFilesOrResource(Property.fill(attr("src", n)), Projects.MAIN_CLASS_LOADER)) {
                        try {
                            Node includeNode = document.importNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(Files.readFileOrResource(Property.fill(attr("src", n))))).getDocumentElement(), true);
                            include(includeNode, n);
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    }
                });
            }
            return document;
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

    /**
     * 获取content中的全部属性列表
     *
     * @param content
     *
     * @return
     */
    public static List<String> getProperties(String content) {
        List<String> result = new ArrayList<>();
        String[] properties = Regexs.match("\\$\\{(.*?)\\}", content);
        if (properties != null && properties.length > 0) {
            for (String property : properties)
                result.add(property);
        }
        return result;
    }

    /**
     * 获取content中的全部属性列表，并去重
     *
     * @param content
     *
     * @return
     */
    public static List<String> getPropertiesDistinct(String content) {
        List<String> result = new ArrayList<>();
        String[] properties = Regexs.match("\\$\\{(.*?)\\}", content);
        if (properties != null && properties.length > 0) {
            for (String property : properties)
                if (!result.contains(property)) {
                    result.add(property);
                }
        }
        return result;
    }

    /**
     * 使用xpath从document中搜索NodeList
     *
     * @param express xpath表达式
     * @param node    节点
     *
     * @return 异常返回null
     */
    public static NodeList xpathNodes(final String express, final Node node) {
        try {
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile(express);
            return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }

    /**
     * 根据xpath结果返回node列表
     *
     * @param express xpath表达式
     * @param node    文档
     *
     * @return 返回node列表, 如果有异常，返回空列表
     */
    public static ArrayList<Node> xpathNodesArray(final String express, final Node node) {
        ArrayList<Node> result = new ArrayList<Node>();
        try {
            if (node == null) {
                return result;
            }
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile(express);
            NodeList nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
            int length = 0;
            if (nodeList != null && (length = nodeList.getLength()) > 0) {
                for (int i = 0; i < length; i++)
                    result.add(nodeList.item(i));
            }
            return result;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return result;
    }

    /**
     * 使用xpath从document中搜索Node
     *
     * @param express
     * @param node
     *
     * @return 异常返回null
     */
    public static Node xpathNode(final String express, final Node node) {
        try {
            if (node == null) {
                return null;
            }
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile(express);
            Node result = (Node) expression.evaluate(node, XPathConstants.NODE);
            return result;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }

    /**
     * 从文件或字符串中读取xml document 对象<br>
     * 根节点上具有 <b>non-property-fill="true"</b> 属性，则不进行属性填充
     *
     * @param filePathOrContent
     *
     * @return 异常返回null
     */
    public static Document document(String filePathOrContent) {
        try {
            if (!Strings.isNotNullOrEmpty(filePathOrContent)) {
                return null;
            }
            filePathOrContent = getResourceContent(filePathOrContent);
            filePathOrContent = propertyFill(filePathOrContent);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(filePathOrContent)));
            if (document == null) {
                return null;
            }
            processInclude(document);
            // processRemoteNode(document);
            return document;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }

    /**
     * 处理远程节点
     *
     * @param document
     *
     * @throws Exception
     */
    public static void processRemoteNode(Document document) throws Exception {
        NodeList remoteNodes = xpathNodes("//remote-node", document);
        if (remoteNodes != null && remoteNodes.getLength() > 0) {
            for (int i = 0; i < remoteNodes.getLength(); i++) {
                Node remoteNode = remoteNodes.item(i);
                Node includeNode = document.importNode(ConfigCenter.get(attr("host", remoteNode), Integer.parseInt(attr("port", remoteNode)), attr("password", remoteNode), attr("path", remoteNode)), true);
                include(includeNode, remoteNode);
            }
        }
    }

    /**
     * 处理include节点
     *
     * @param document
     */
    public static void processInclude(Document document) {
        try {
            NodeList includeNodes = xpathNodes("//include", document);
            if (includeNodes != null && includeNodes.getLength() > 0) {
                for (int i = 0; i < includeNodes.getLength(); i++) {
                    try {
                        String includeSrc = attr("src", includeNodes.item(i));
                        Node includeNode = document.importNode(document(includeSrc).getDocumentElement(), true);
                        include(includeNode, includeNodes.item(i));
                    }
                    catch (Exception x) {
                        LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
                    }
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }

    /**
     * 获取路径资源，如果路径上没有资源则返回参数本身
     *
     * @param filePathOrContent
     *
     * @return
     */
    public static String getResourceContent(String filePathOrContent) {
        try {
            if (Files.exist(filePathOrContent)) {
                filePathOrContent = Files.read(filePathOrContent);
            }
            else {
                InputStream stream = ClassLoader.getSystemResourceAsStream(filePathOrContent);
                if (stream != null) {
                    byte[] data = new byte[stream.available()];
                    stream.read(data);
                    filePathOrContent = new String(data, "utf-8");
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return filePathOrContent;
    }

    /**
     * 从文件或字符串中读取xml node 对象<br>
     * 根节点上具有 <b>non-property-fill="true"</b> 属性，则不进行属性填充
     *
     * @param filePathOrContent
     */
    public static Node node(String filePathOrContent) {
        try {
            if (!Strings.isNotNullOrEmpty(filePathOrContent)) {
                return null;
            }
            filePathOrContent = getResourceContent(filePathOrContent);
            filePathOrContent = propertyFill(filePathOrContent);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(filePathOrContent)));
            if (document == null) {
                return null;
            }
            processInclude(document);
            // processRemoteNode(document);
            return document;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return null;
        }
    }

    /**
     * 属性填充
     *
     * @param filePathOrContent
     *
     * @return
     */
    public static String propertyFill(String filePathOrContent) {
        if (filePathOrContent.contains("non-property-analyze")) {
            return filePathOrContent;
        }
        String[] properties = Regexs.match("\\$\\{(.*?)\\}", filePathOrContent);
        if (properties != null && properties.length > 0) {
            for (String property : properties) {
                filePathOrContent = filePathOrContent.replace(String.format("${%1$s}", property), Property.get(property));
            }
        }
        return filePathOrContent;
    }

    /**
     * 从节点中读取首个tagname的子节点
     *
     * @param nodeName
     * @param node
     */
    public static Node firstNamedChild(final String nodeName, final Node node) {
        try {
            if (node != null && Strings.isNotNullOrEmpty(nodeName)) {
                NodeList nodes = node.getChildNodes();
                if (nodes != null && nodes.getLength() > 0) {
                    for (int i = 0; i < nodes.getLength(); i++)
                        if (nodes.item(i) != null && nodeName.equals(nodes.item(i).getNodeName())) {
                            return nodes.item(i);
                        }
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return null;
    }

    /**
     * 从节点中读取相应名称的属性
     *
     * @param attributeName
     * @param node
     */
    public static String attr(final String attributeName, final Node node) {
        try {
            if (node != null) {
                NamedNodeMap attrMap = node.getAttributes();
                if (attrMap != null && attrMap.getLength() > 0) {
                    Node attrNode = attrMap.getNamedItem(attributeName);
                    if (attrNode != null) {
                        return attrNode.getNodeValue();
                    }
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return Strings.EMPTY;
    }

    /**
     * 从节点中读取相应名称的属性，有默认值
     *
     * @param attributeName 属性名
     * @param node          节点
     * @param defaultValue  默认值
     *
     * @return
     */
    public static String attr(final String attributeName, final Node node, final String defaultValue) {
        String result = attr(attributeName, node);
        return Strings.isNotNullOrEmpty(result) ? result : defaultValue;
    }

    /**
     * 获取第一个子元素的字符串内容
     *
     * @param childNodeName 子元素名字
     * @param node          节点
     *
     * @return
     */
    public static String childTextContent(final String childNodeName, final Node node) {
        try {
            ArrayList<Node> childs = childs(childNodeName, node);
            if (childs != null && childs.size() > 0) {
                return childs.get(0).getTextContent().trim();
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return Strings.EMPTY;
    }

    /**
     * 获取第一个子元素的字符串内容
     *
     * @param childNodeName 子元素名字
     * @param node          节点
     * @param defaultValue  默认值
     *
     * @return
     */
    public static String childTextContent(final String childNodeName, final Node node, String defaultValue) {
        String result = childTextContent(childNodeName, node);
        return Strings.isNotNullOrEmpty(result) ? result : defaultValue;
    }

    /**
     * 获取第一个子元素的属性内容
     *
     * @param childNodeName 子元素名字
     * @param attribute     属性名
     * @param node          节点
     *
     * @return 返回属性内容
     */
    public static String childAttribute(final String childNodeName, final String attribute, final Node node) {
        try {
            ArrayList<Node> childs = childs(childNodeName, node);
            if (childs != null && childs.size() > 0) {
                return attr(attribute, childs.get(0));
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return Strings.EMPTY;
    }

    /**
     * 获取第一个子元素的属性内容
     *
     * @param childNodeName 子元素名字
     * @param attribute     属性名
     * @param node          节点
     * @param defaultValue  默认值
     *
     * @return 返回属性内容
     */
    public static String childAttribute(final String childNodeName, final String attribute, final Node node, String defaultValue) {
        String result = childAttribute(childNodeName, attribute, node);
        return Strings.isNotNullOrEmpty(result) ? result : defaultValue;
    }

    /**
     * 设置节点属性，如果存在则覆盖
     *
     * @param attributeName 属性名称
     * @param value         新值
     * @param node          节点
     * @param document      文档
     */
    public static void setAttr(final String attributeName, final String value, final Node node, final Document document) {
        if (!Strings.isNotNullOrEmpty(attributeName)) {
            return;
        }
        Attr attribute = document.createAttribute(attributeName);
        attribute.setNodeValue(value);
        node.getAttributes().setNamedItem(attribute);
    }

    /**
     * 从xmldocument中读取tagname的节点
     *
     * @param tagName
     * @param doc
     */
    public static Node firstTag(final String tagName, final Document doc) {
        if (doc != null) {
            NodeList nodes = doc.getElementsByTagName(tagName);
            if (nodes != null && nodes.getLength() > 0) {
                return nodes.item(0);
            }
        }
        return null;
    }

    /**
     * 得到所有名称列表Node
     *
     * @param nodeName
     * @param node
     */
    public static ArrayList<Node> childs(final String nodeName, final Node node) {
        try {
            if (node != null && Strings.isNotNullOrEmpty(nodeName)) {
                ArrayList<Node> nodeArr = new ArrayList<Node>();
                NodeList nodes = node.getChildNodes();
                if (nodes != null && nodes.getLength() > 0) {
                    for (int i = 0; i < nodes.getLength(); i++)
                        if (nodes.item(i) != null && nodeName.equals(nodes.item(i).getNodeName())) {
                            nodeArr.add(nodes.item(i));
                        }
                }
                return nodeArr;
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return null;
    }

    /**
     * 将node转换成String并添加相应的注释头
     *
     * @param node   节点
     * @param header xml注释头，例如：<br>
     *               OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTD Config 3.0//EN" <br>
     *               OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd"
     */
    public static String toXmlString(final Node node, final HashMap<String, String> header) throws Exception {
        StreamResult strResult = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (header != null && header.entrySet() != null) {
            for (Entry<String, String> kv : header.entrySet())
                transformer.setOutputProperty(kv.getKey(), kv.getValue());
        }
        transformer.transform(new DOMSource(node), strResult);
        return strResult.getWriter().toString();
    }

    /**
     * 将Node转换成String
     *
     * @param node
     */
    public static String toXmlString(final Node node) throws Exception {
        return toXmlString(node, null);
    }

    /**
     * 将Document转换成String
     *
     * @param document
     */
    public static String toXmlString(Document document) throws Exception {
        return toXmlString(document.getFirstChild());
    }

    /**
     * 将Document转换成JSONObject
     *
     * @param document
     *
     * @return
     *
     * @throws Exception
     */
    public static JSONArray toJson(Node document) throws Exception {
        JSONArray result = new JSONArray();
        NodeList nodeList = document.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    result.add(toJsonObject(node));
                }
            }
        }
        return result;
    }

    /**
     * 是否是元素叶子节点
     *
     * @param node 结点
     *
     * @return
     */
    public static boolean isLeafElement(Node node) {
        if (node == null) {
            return false;
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return false;
        }
        NodeList childs = node.getChildNodes();
        if (childs != null && childs.getLength() > 0) {
            for (int i = 0; i < childs.getLength(); i++) {
                Node item = childs.item(i);
                if (item != null && item.getNodeType() == Node.ELEMENT_NODE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否是真正的普通的Node
     *
     * @param node
     *
     * @return
     */
    public static boolean isRealNode(final Node node) {
        return node != null & node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * 将node转换成JSONObject
     *
     * @param node
     *
     * @return
     *
     * @throws Exception
     */
    public static JSONObject toJsonObject(final Node node) throws Exception {
        return toJsonObject(node, "value");
    }

    /**
     * 将node转换成JSONObject
     *
     * @param node     结点
     * @param textName 文本内容key值
     *
     * @return
     *
     * @throws Exception
     */
    public static JSONObject toJsonObject(final Node node, final String textName) throws Exception {
        JSONObject result = new JSONObject();
        if (node == null) {
            return result;
        }
        if (isLeafElement(node) && Strings.isNotNullOrEmpty(node.getTextContent())) {
            result.put(textName, node.getTextContent());
        }
        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null && attrs.getLength() > 0) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                if (attr != null && attr.getNodeType() == Node.ATTRIBUTE_NODE) {
                    result.put(attrs.item(i).getNodeName(), attrs.item(i).getNodeValue());
                }
            }
        }
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    result.put(nodeList.item(i).getNodeName(), toJsonObject(nodeList.item(i), textName));
                }
            }
        }
        return result;
    }

    /**
     * 包含节点
     *
     * @param includedNode 被包含节点
     * @param item         替换当前节点的节点
     */
    public static void include(Node includedNode, Node item) {
        try {
            Node parent = item.getParentNode();
            parent.appendChild(includedNode);
            parent.removeChild(item);
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }

}
