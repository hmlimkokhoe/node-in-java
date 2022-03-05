package com.mycompany.app;

/*
 * Copied from: https://github.com/caoccao/Javet/blob/main/src/test/java/com/caoccao/javet/tutorial/HelloJavet.java
 *
 */

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;

public class HelloJavet {

    public static void main(String[] args) throws JavetException {
        HelloJavet helloJavet = new HelloJavet();
        helloJavet.printHelloJavet();
        helloJavet.printOnePlusOne();
        helloJavet.playWithPoolAndConsole();
    }

    public void playWithPoolAndConsole() throws JavetException {
        // Create a Javet engine pool.
        try (IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>()) {
            // Get a Javet engine from the pool.
            try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
                // Get a V8 runtime from the engine.
                V8Runtime v8Runtime = javetEngine.getV8Runtime();
                // Create a Javet console interceptor.
                JavetStandardConsoleInterceptor javetConsoleInterceptor =
                        new JavetStandardConsoleInterceptor(v8Runtime);
                // Register the Javet console to V8 global object.
                javetConsoleInterceptor.register(v8Runtime.getGlobalObject());
                // V8 console log is redirected to JVM console log.
                v8Runtime.getExecutor("console.log('Hello Javet from Pool');").executeVoid();
                // Unregister the Javet console to V8 global object.
                javetConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
                // close() is not necessary because the Javet engine pool handles that.
                v8Runtime.lowMemoryNotification();
                // Force V8 to GC.
            }
        }
    }

    public void printHelloJavet() throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Step 2: Execute a string as JavaScript code and print the result to console.
            System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString()); // Hello Javet
            // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
        }
    }

    public void printOnePlusOne() throws JavetException {
        // Step 1: Create a Node runtime from V8 host in try-with-resource.
        try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
            // Step 2: Execute a string as JavaScript code and print the result to console.
            System.out.println("1 + 1 = " + nodeRuntime.getExecutor("1 + 1").executeInteger()); // 2
            // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
        }
    }
}