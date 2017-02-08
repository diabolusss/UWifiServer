package org.rusak.uwifiserver.server;

import functions.Functions;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.rusak.uwifiserver.run.UWifiServerMain;

public class UWifiPacketServer implements Runnable{
    protected int          serverPort;//   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    //Executor just executes stuff you give it.
    //ExecutorService adds startup, shutdown, and the ability to wait for and
    // look at the status of jobs you've submitted for execution on top of
    // Executor (which it extends).
    static ExecutorService client_pool = Executors.newFixedThreadPool(15);

    public UWifiPacketServer(int port){
        this.serverPort = port;
    }

    @Override
    public void run(){
        //this.runningThread = Thread.currentThread(); Simply gives you a link to the current thread.
        //This way you don't have to call Thread.currentThread() all the time,
        //saving you the method call overhead.
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        
        openServerSocket();
        
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                //The submit(...) method is an executor framework extension introduced in ExecutorService
                //interface.
                //Its main difference from execute(Runnable) is that submit(...)
                // can accept a Callable<V> (whereas execute() accepts only Runnable)
                // and returns an instance of Future<V>, which you can use later
                // in the caller to retrieve the result asynchronously 
                // (potentially blocking until the computation performed by the Callable is completed).
                client_pool.execute(
                    new UWifiPacketClientSocket(clientSocket, clientSocket.getRemoteSocketAddress().toString())
                );
                
            } catch (IOException e) {
              //  throw new RuntimeException(
                //    "Error accepting client connection", e);
                Functions.err(UWifiServerMain.LOG_LEVEL, "<UWifiPacketServer.run> Failed to accept new client connection. ERR#"+e.getMessage());
            }
            //new Thread(
            //    new UWifiPacketClientSocket(
            //        clientSocket, "Multithreaded Server")
            //).start();
           
        }  
        client_pool.shutdownNow();
        Functions.inf(UWifiServerMain.LOG_LEVEL, "<UWifiPacketServer.run> Server Stopped");                    
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        if(this.serverSocket == null) return;
        
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            Functions.err(UWifiServerMain.LOG_LEVEL,"<UWifiPacketServer.stop> Cannot open port "+ this.serverPort+". ERR#"+e.getMessage());
          
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            //throw new RuntimeException("Cannot open port "+this.serverPort, e);
            Functions.err(UWifiServerMain.LOG_LEVEL,"<UWifiPacketServer.openServerSocket> Cannot open port "+ this.serverPort+". ERR#"+e.getMessage());
            this.stop();
        }
    }

}