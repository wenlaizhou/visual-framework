/**
 * @项目名称: framework
 * @文件名称: ServerInit.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: ServerInit
 */
package cn.framework.core.container;

import cn.framework.core.pool.ThreadPool;
import cn.framework.core.resource.ErrorUI;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Reflects;
import cn.framework.core.utils.Strings;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.net.InetAddress;
import java.util.ArrayList;

import static cn.framework.core.utils.Xmls.*;

/**
 * @author wenlai
 *         set CATALINA_OPTS=\
 *         -Dcom.sun.management.jmxremote \
 *         -Dcom.sun.management.jmxremote.port=%my.jmx.port% \
 *         -Dcom.sun.management.jmxremote.ssl=false \
 *         -Dcom.sun.management.jmxremote.authenticate=false
 */
@Service(ServerInitProvider.BEAN_NAME)
public class ServerInitProvider implements InitProvider {

    public static final String BEAN_NAME = "serverInit";

    private static volatile boolean INITED = false;

    public String host;

    public int port;

    public String hostStr;

    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public synchronized void init(final Context context) throws Exception {
        try {
            if (INITED) {
                return;
            }
            Node serverNode = xpathNode("//server", context.getConf());
            context.getTomcat().setPort(0);
            /**
             * build connector
             */
            Connector connector = new Connector(Http11Nio2Protocol.class.getName());
            Node connectoreNode = xpathNode(".//connector", serverNode);
            if (connectoreNode != null) {
                this.host = childAttribute("address", "value", connectoreNode, "0.0.0.0");
                connector.setAttribute("address", this.host);
                this.port = Integer.parseInt(childAttribute("port", "value", connectoreNode, "8080"));
                connector.setPort(this.port);
                if (this.host.equals("0.0.0.0")) {
                    this.host = InetAddress.getLocalHost().toString();
                }
                this.hostStr = Strings.append(this.host, ":", this.port);
                /**
                 * build connector attributes
                 */
                NamedNodeMap attrs = connectoreNode.getAttributes();
                if (attrs != null && attrs.getLength() > 0) {
                    for (int i = 0; i < attrs.getLength(); i++) {
                        Node attribute = attrs.item(i);
                        connector.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
                    }
                }
                /**
                 * TODO add https support
                 * <!-- Define a SSL Coyote HTTP/1.1 Connector on port 8443 -->
                 <Connector
                 protocol="org.apache.coyote.http11.Http11NioProtocol"
                 port="8443" maxThreads="200"
                 scheme="https" secure="true" SSLEnabled="true"
                 keystoreFile="${user.home}/.keystore" keystorePass="changeit"
                 clientAuth="false" sslProtocol="TLS"/>
                 */
                /**
                 * build https
                 */
                //connector.setScheme("https");
                //connector.setSecure(true);
                //connector.setAttribute("SSLEnabled", "true");
                //connector.setAttribute("keystoreFile", Projects.CONF_DIR + "/framework.keystore");
                //connector.setAttribute("keystorePass", "wenlai");
                //connector.setAttribute("clientAuth", "false");
                //connector.setAttribute("sslProtocol", "TLS");
                ////<Listener className="org.apache.catalina.core.AprLifecycleListener"
                ////SSLEngine="on" SSLRandomSeed="builtin" />
                //AprLifecycleListener listener = new AprLifecycleListener();
                //                listener.setSSLEngine("on");
                //                listener.setSSLRandomSeed("builtin");
                //                connector.addLifecycleListener(new AprLifecycleListener());
            }
            else {
                buildDefaultConnector(connector);
            }
            connector.setAllowTrace(true);
            context.getContext().setSwallowOutput(true);
            context.getTomcat().getService().addConnector(connector);
            context.getTomcat().setConnector(connector);
            context.getTomcat().getHost().setAppBase("ROOT");
            /**
             * build thread-pool
             */
            Node threadPoolNode = xpathNode(".//thread-pool", serverNode);
            if (threadPoolNode != null) {
                String commonPoolSize = childAttribute("common", "size", threadPoolNode);
                String scheduleSize = childAttribute("schedule", "size", threadPoolNode);
                Reflects.setField(ThreadPool.class.getName(), "commonPoolSize", Strings.parseInt(commonPoolSize, 30), null);
                Reflects.setField(ThreadPool.class.getName(), "schedulePoolSize", Strings.parseInt(scheduleSize, 10), null);
            }
            /**
             * build context-params
             */
            Node contextParamsNode = xpathNode("//context-params", context.getConf());
            if (contextParamsNode != null) {
                ArrayList<Node> contextParams = xpathNodesArray(".//context-param", contextParamsNode);
                if (contextParams != null && contextParams.size() > 0) {
                    for (Node contextParamNode : contextParams) {
                        context.getContext().addParameter(attr("name", contextParamNode), attr("value", contextParamNode));
                    }
                }
            }
            /**
             * build listeners
             */
            Node listenersNode = xpathNode("//listeners", context.getConf());
            if (listenersNode != null) {
                ArrayList<Node> listenerNodes = xpathNodesArray(".//listener", listenersNode);
                if (listenerNodes != null && listenerNodes.size() > 0) {
                    for (Node listenerNode : listenerNodes) {
                        try {
                            context.getContext().addApplicationListener(attr("class", listenerNode));
                            Exceptions.logProcessor().logger().info("add listener : {}", attr("class", listenerNode));
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    }
                }
            }

            /**
             * add default-servlet
             * remove by wenlai at 2016-03-16
             */
            //            KVMap defaultServletParams = new KVMap();
            //            defaultServletParams.addKV("debug", 0);
            //            defaultServletParams.addKV("listing", false);
            //            defaultServletParams.addKV("gzip", true);
            //            defaultServletParams.addKV("fileEncoding", "");
            //            context.addServlet("default-servlet", DefaultServlet.class.getName(), "/", defaultServletParams, 1);
            /**
             * add error page
             */
            context.addServlet("error-page", ErrorUI.class.getName(), "/Error.html");
            ErrorPage notFound = new ErrorPage();
            notFound.setErrorCode(404);
            notFound.setLocation("/Error.html");
            context.getContext().addErrorPage(notFound);
            ErrorPage forbidden = new ErrorPage();
            forbidden.setErrorCode(401);
            forbidden.setLocation("/Error.html");
            context.getContext().addErrorPage(forbidden);
            ErrorPage error = new ErrorPage();
            error.setErrorCode(500);
            error.setLocation("/Error.html");
            context.getContext().addErrorPage(error);
        }
        catch (RuntimeException x) {
            throw x;
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }
    }

    /**
     * 构建默认connector<br>
     * 默认端口8080
     *
     * @param connector connector
     */
    private void buildDefaultConnector(Connector connector) throws Exception {
        connector.setAttribute("address", "0.0.0.0");
        connector.setPort(8808);
        this.port = 8808;
        this.host = InetAddress.getLocalHost().toString();
        this.hostStr = Strings.append(this.host, ":", this.port);
    }

}
