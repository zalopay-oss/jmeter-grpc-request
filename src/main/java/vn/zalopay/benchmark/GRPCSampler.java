package vn.zalopay.benchmark;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.exception.ShortExceptionName;

public class GRPCSampler extends AbstractSampler implements ThreadListener {

  private static final Logger log = LoggerFactory.getLogger(GRPCSampler.class);
  private static final long serialVersionUID = 232L;

  public static final String METADATA = "GRPCSampler.metadata";
  public static final String LIB_FOLDER = "GRPCSampler.libFolder";
  public static final String PROTO_FOLDER = "GRPCSampler.protoFolder";
  public static final String HOST = "GRPCSampler.host";
  public static final String PORT = "GRPCSampler.port";
  public static final String FULL_METHOD = "GRPCSampler.fullMethod";
  public static final String REQUEST_JSON = "GRPCSampler.requestJson";
  public static final String DEADLINE = "GRPCSampler.deadline";
  public static final String TLS = "GRPCSampler.tls";

  private transient ClientCaller clientCaller = null;

  private static AtomicInteger classCount = new AtomicInteger(0); // keep track of classes created

  public GRPCSampler() {
    classCount.incrementAndGet();
    trace("GRPCSampler()");
  }

  /**
   * @return a string for the sampleResult Title
   */
  private String getTitle() {
    return this.getName();
  }

  private void trace(String s) {
    if (log.isDebugEnabled()) {
      log.debug("{} ({}) {} {} {}", Thread.currentThread().getName(), classCount.get(),
          getTitle(), s, this.toString());
    }
  }

  private void init() {
    clientCaller = new ClientCaller(
        getHostPort(),
        getProtoFolder(),
        getLibFolder(),
        getFullMethod(),
        isTls(),
        getMetadata());
  }

  @Override
  public SampleResult sample(Entry ignored) {
    SampleResult res = new SampleResult();
    res.setSampleLabel(getName());

    String req = clientCaller.buildRequest(getRequestJson());
    res.setSamplerData(req);
    res.sampleStart();

    try {
      try {
        DynamicMessage resp = clientCaller.call(getDeadline());

        try {
          res.sampleEnd();
          res.setSuccessful(true);
          res.setResponseData(JsonFormat.printer().print(resp).getBytes());
          res.setResponseMessage("Success");
          res.setDataType(SampleResult.TEXT);
          res.setResponseCodeOK();
        } catch (InvalidProtocolBufferException e) {
          errorResult(res, e);
        }
      } catch (RuntimeException e) {
        errorResult(res, e);
      }
    } catch (StatusRuntimeException e) {
      errorResult(res, e);
    }
    return res;
  }

  @Override
  public void clear() {
    super.clear();
  }

  @Override
  public void threadStarted() {
    log.info("{}\ttestStarted", whoAmI());
    init();
  }

  @Override
  public void threadFinished() {
    log.info("{}\ttestEnded", whoAmI());

    if (clientCaller != null) {
      clientCaller.shutdown();
    }
  }

  private String whoAmI() {
    return Thread.currentThread().getName() +
        "@" +
        Integer.toHexString(hashCode()) +
        "-" +
        getName();
  }

  private void errorResult(SampleResult res, Exception e) {
    res.sampleEnd();
    res.setSuccessful(false);
    res.setResponseMessage("Exception: " + ShortExceptionName.shortest(e.getCause().getMessage()));
    res.setResponseData(e.getMessage().getBytes());
    res.setDataType(SampleResult.TEXT);
    res.setResponseCode("500");
  }

  /**
   * GETTER AND SETTER
   */

  public String getMetadata() {
    return getPropertyAsString(METADATA);
  }

  public void setMetadata(String metadata) {
    setProperty(METADATA, metadata);
  }

  public String getLibFolder() {
    return getPropertyAsString(LIB_FOLDER);
  }

  public void setLibFolder(String libFolder) {
    setProperty(LIB_FOLDER, libFolder);
  }

  public String getProtoFolder() {
    return getPropertyAsString(PROTO_FOLDER);
  }

  public void setProtoFolder(String protoFolder) {
    setProperty(PROTO_FOLDER, protoFolder);
  }

  public String getFullMethod() {
    return getPropertyAsString(FULL_METHOD);
  }

  public void setFullMethod(String fullMethod) {
    setProperty(FULL_METHOD, fullMethod);
  }

  public String getRequestJson() {
    return getPropertyAsString(REQUEST_JSON);
  }

  public void setRequestJson(String requestJson) {
    setProperty(REQUEST_JSON, requestJson);
  }

  public String getDeadline() {
    return getPropertyAsString(DEADLINE);
  }

  public void setDeadline(String deadline) {
    setProperty(DEADLINE, deadline);
  }

  public boolean isTls() {
    return getPropertyAsBoolean(TLS);
  }

  public void setTls(boolean tls) {
    setProperty(TLS, tls);
  }

  public String getHost() {
    return getPropertyAsString(HOST);
  }

  public void setHost(String host) {
    setProperty(HOST, host);
  }

  public String getPort() {
    return getPropertyAsString(PORT);
  }

  public void setPort(String port) {
    setProperty(PORT, port);
  }

  private String getHostPort() {
    return getHost() + ":" + getPort();
  }
}
