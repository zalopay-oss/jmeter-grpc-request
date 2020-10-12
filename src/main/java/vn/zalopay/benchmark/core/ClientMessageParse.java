package vn.zalopay.benchmark.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;

import org.json.simple.JSONArray;

import java.util.LinkedList;
import java.util.List;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

public class ClientMessageParse {


  public static String Parse(String protoFile, String libFolder,String MethodName)
  {
    String parseresult = "";
    String methodfromui = MethodName.split("/")[1];
    JsonObject resultjson = new JsonObject();
    final DescriptorProtos.FileDescriptorSet fileDescriptorSet;
    try {
      fileDescriptorSet = ProtocInvoker.forConfig(protoFile, libFolder).invoke();
    } catch (Throwable t) {
      throw new RuntimeException("Unable to resolve service by invoking protoc", t);
    }
    ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
    
    for (ServiceDescriptor descriptor : serviceResolver.listServices()) {
      for (MethodDescriptor method : descriptor.getMethods()) {
        if (method.getName().equals(methodfromui))
        {
          for(FieldDescriptor field :method.getInputType().getFields())
          {
            
            resultjson.add(field.getName(),  (new JsonParser().parse(field.getLiteType().toString()) ));
          }
          parseresult = resultjson.toString();
          break;
        }
      }
    }    


    return parseresult;
  }
}
