package vn.zalopay.benchmark.testbean;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TypeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcRequestBeanInfo extends BeanInfoSupport {
    private static final Logger log = LoggerFactory.getLogger(GrpcRequestBeanInfo.class);

    public GrpcRequestBeanInfo() {
        super(GrpcRequest.class);
        log.info("Entered access log sampler bean info");

        PropertyDescriptor p;

        createPropertyGroup("serverInfoGroup", new String[]{"hostPort", "fullMethod", "tls"});
        p = property("hostPort");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("fullMethod");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("tls");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.FALSE);

        createPropertyGroup("requestInfoGroup", new String[]{"protoFolder", "libFolder", "requestFile", "deadline", "metadata","requestJson"});
        p = property("protoFolder");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("libFolder");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("requestFile");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("deadline");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, 0L);

        p = property("metadata");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("requestJson", TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.FALSE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(TEXT_LANGUAGE, "javascript");

        log.info("Got to end of access log sampler bean info init");
    }

}
