package vn.zalopay.benchmark.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.WireFormat.FieldType;

import org.json.simple.JSONArray;

import java.util.LinkedList;
import java.util.List;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

public class ClientMessageParse {

    /**
     * Parse Message base on protofile and MethodName
     * Still need improvement.
     */
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
      if(descriptor.findMethodByName(methodfromui)!=null){
          for(FieldDescriptor field :descriptor.findMethodByName(methodfromui).getInputType().getFields())
          {
            
            System.out.println(field.getName()+" : " + field.getLiteType().toString() );
            if(field.getLiteType().equals(FieldType.MESSAGE))
            {
              resultjson.add(field.getName(), MessagetoJson(field.getMessageType().getFields()));
              
            }
            else{resultjson.add(field.getName(),  (new JsonParser().parse(field.getLiteType().toString()) ));}
            
          }
          parseresult = resultjson.toString();
          
           break;
          }        
    }    


    return parseresult;
  }
    /**
     * Convert Message Type in Message to Json. 
     * In recursive way.
     */
  private static JsonElement MessagetoJson(List<FieldDescriptor> fields)
  {
    JsonObject temp = new JsonObject();
    for(FieldDescriptor field: fields)
    {
      if(field.getLiteType().equals(FieldType.MESSAGE))
      { temp.add(field.getName(), MessagetoJson(field.getMessageType().getFields()) );}
      else{
        temp.add(field.getName(), new JsonParser().parse(field.getLiteType().toString()) );
      }     
    }


    return temp;

  }

}
