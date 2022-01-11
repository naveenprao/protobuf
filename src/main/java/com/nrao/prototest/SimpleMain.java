package com.nrao.prototest;

import com.google.protobuf.util.JsonFormat;
import example.simple.Simple;
import example.simple.Simple.SimpleMessage.Builder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static example.simple.Simple.SimpleMessage.newBuilder;

public class SimpleMain {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Builder builder = newBuilder();
        // create a simple field
        Simple.SimpleMessage msg = builder.setId(43).setIsSimple(true).setName("mymsg").build();
        // repeated field
        Simple.SimpleMessage repeatedMsg = builder.addAllSampleList(Arrays.asList(101, 102, 103)).build();

        // Write to file and Read it.
        try {
            FileOutputStream outputStream = new FileOutputStream("simple_message.bin");
            msg.writeTo(outputStream);

            byte[] bytes = msg.toByteArray();

            FileInputStream fileInputStream = new FileInputStream("simple_message.bin");
            Simple.SimpleMessage msgFromFile = Simple.SimpleMessage.parseFrom(fileInputStream);

            // JSON
            System.out.println("OriginalJson:" + JsonFormat.printer().print(msg));
            System.out.println("DeserializedJson" + JsonFormat.printer().print(msgFromFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(msg);
        System.out.println(repeatedMsg);
    }
}
