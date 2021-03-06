/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.classes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class with various methods oriented to the usage of {@link Socket},
 * and for the sending and receiving of objects.
 * @author Francesc Bague Marti
 * @version 1.0 2022/02/02
 */
public class MySocket {
    private String ip;
    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean isClient;
    private boolean multiClient;
    
    //region CONSTRUCTORS
    public MySocket(){
        
    }
    /**
     * Constructor for a client socket
     * @param ip String with the server's IP address
     * @param port Integer with the server's PORT
     */
    public MySocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.isClient = true;
        this.multiClient = false;
    }
    /**
     * Constructor for a server's socket
     * @param serverSocket ServerSocket object that will be used for the connection
     */
    public MySocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.isClient = false;
        this.multiClient = true;
    }
    /**
     * Constructor for a server's socket
     * @param PORT Integer with the port where the socket will be located
     */
    public MySocket(int PORT) {
        this.port = PORT;
        this.isClient = false;
    }
    //endregion

    //region SOCEKT STATUS
    /**
     * Method that starts the client's socket connection
     * @throws IOException Throws an Exception
     */
    public void start() throws IOException {
        if (isClient)
            this.socket = new Socket(ip, port);
        else
            throw new IOException("Este m??todo solo funciona si el socket es de un cliente");
    }
    /**
     * Method that makes the server's socket accept a new client connection
     * @throws IOException Throws an Exception
     */
    public void accept() throws IOException {

        if (isClient)
            throw new IOException("Este m??todo solo funciona si el socket es un servidor.");
        else if (multiClient) {
            this.socket = serverSocket.accept();
        } else {
            this.serverSocket = new ServerSocket(this.port);
            this.socket = serverSocket.accept();
        }
    }
    /**
     * Method that closes the socket's connection
     * @throws IOException Throws an Exception
     */
    public void close() throws IOException {
        if (isClient) {
            this.socket.close();
        } else {
            this.socket.close();
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }
    //endregion

    //region GETTERS
    /**
     * Method that returns the client's IP address
     * @return Returns a String object
     */
    public String getIP() {
        return this.socket.getInetAddress().getHostName();
    }
    /**
     * Method that returns an OutputStream object
     * @return Returns an OutputStream object
     * @throws IOException Throws an IOException
     */
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }
    /**
     * Method that returns an InputStream object
     * @return Returns an InputStream object
     * @throws IOException Throws an IOException
     */
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }
    //endregion

    //region SENDERS
    /**
     * Method for the sending of an object between the server and the client
     * @param object Object that will be sent
     * @throws IOException Throws an exception
     */
    public void send(Object object) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(object);
    }
    /**
     * Mehod for the sending of an <b>object array</b> between the server and the client
     * @param objects Object array that will be sent
     * @throws IOException  Throws an exception
     */
    public void send(Object[] objects) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject((Object) objects);
    }
    /**
     * Mehod for the sending of an <b>String array</b> between the server and the client
     * @param lines String array that will be sent
     * @throws IOException  Throws an exception
     */
    public void send(String[] lines) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject((Serializable) lines);
    }
    /**
     * Method for the sending of an <b>integer</b> between the server and the client
     * @param num Integer that will be sent
     * @throws IOException Throws an exception
     */
    public void sendInt(int num) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeInt(num);
    }
    /**
     * Method for the sending of an <b>array of bytes</b> between the server and the client
     * @param bytes Byte array that will be sent
     * @throws IOException Throws an exception
     */
    public void sendBytes(byte[] bytes) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.write(bytes);
    }
    /**
     * Method for the sending of an <b>array of bytes</b> between the server and the client
     * @param bytes Byte array that will be sent
     * @param length Length of the Byte array
     * @throws IOException Throws an exception
     */
    public void sendBytes(byte[] bytes, int length) throws IOException {
        
        OutputStream os = this.socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.write(bytes);
    }
    //endregion

    //region READERS
    /**
     * Method that reads an object
     * @return Returns an object
     * @throws IOException Throws an exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }
    /**
     * Method that reads an <b>object array</b>
     * @return Returns an object array
     * @throws IOException Throws an exception
     * @throws ClassNotFoundException Throws an exception
     */
    public Object[] readObjects() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return (Object[]) ois.readObject();
    }
    /**
     * Method that reads an <b>object array</b>
     * @return Returns an object array
     * @throws IOException Throws an exception
     * @throws ClassNotFoundException Throws an exception
     * @deprecated This method is deprecated. Use readObjects instead
     */
    @Deprecated
    public Object[] readObject_Array() throws IOException, ClassNotFoundException {
        ObjectInputStream ois;
        
        int size = new ObjectInputStream(this.socket.getInputStream()).readInt();
        
        Object[] objects = new Object[size];
        for (int i = 0; i < size; i++) {
            ois = new ObjectInputStream(this.socket.getInputStream());
            objects[i] = ois.readObject();
        }
        
        return objects;
    }
    /**
     * Method that reads a String
     * @return Returns a String object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public String readString() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return (String) ois.readObject();
    }
    /**
     * Method that reads a char
     * @return Returns a char object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public char readChar() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readChar();
    }
    /**
     * Method that reads an int
     * @return Returns an int object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public int readInt() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readInt();
    }
    /**
     * Method that reads a double
     * @return returns a double object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public double readDouble() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readDouble();
    }
    /**
     * Method that reads a float
     * @return Returns a float object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public float readFloat() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readFloat();
    }
    /**
     * Method that reads a long
     * @return Returns a long object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public long readLong() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readLong();
    }
    /**
     * Method that reads a short
     * @return Returns a short object
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public short readShort() throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readShort();
    }
    /**
     * Method that reads a byte
     * @return Returns a byte object
     * @throws IOException Throws an Exception
     * @throws ClassCastException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public float readByte() throws IOException, ClassCastException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readByte();
    }
    /**
     * Method that reads an <b>array of bytes</b>
     * @param length The maximum number of bytes to read
     * @return Returns a byte array
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public byte[] readBytes(int length) throws IOException, ClassNotFoundException {
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readNBytes(length);
    }
    /**
     * Method that reads an <b>array of bytes</b>
     * @return Returns a byte array
     * @throws IOException Throws an Exception
     * @throws ClassNotFoundException Throws an Exception
     */
    public byte[] readBytes() throws IOException, ClassNotFoundException {
        int length = this.readInt();
        InputStream is = this.socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readNBytes(length);
    }
    //endregion

    //region FILES
    public void sendFile(String filePath) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(filePath);

        int fileByte = fis.read(); // Lee el primer byte del archivo.
    
        while(fileByte != -1)
        {
           OutputStream os = this.socket.getOutputStream();
           os.write(fileByte);
           fileByte = fis.read();
        }

        fis.close();
    }
    /**
     * Method that sends a file via webSocket
     * @param filePath The file's path
     * @param header The header that will be sent
     * @throws FileNotFoundException Throws an Exception
     * @throws IOException  Throws an exception
     */
    public void sendFile(String filePath, String header) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(filePath);
        //Path path = Paths.get(filePath);
        //long fileSize = Files.size(path);

        int fileByte = fis.read(); // Lee el primer byte del archivo.
        int counter=0;

        byte[] headerBytes = header.getBytes();
        OutputStream os;
        // Sends the header
        while(counter < headerBytes.length) {
            os = this.socket.getOutputStream();
            os.write(headerBytes, counter, headerBytes.length);

            counter += headerBytes.length;
        }
        //sends the file
        while(fileByte != -1)
        {
           os = this.socket.getOutputStream();
           os.write(fileByte);
           fileByte = fis.read();
        }

        fis.close();
    }
    /**
     * Method that recieves a file sent via socket
     * @param filePath Location where the file will be saved
     * @throws FileNotFoundException Throws an Exception
     * @throws IOException Throws an Exception
     */
    public void downloadFile(String filePath) throws FileNotFoundException, IOException{
        
        FileOutputStream fos = new FileOutputStream(filePath);
        InputStream is = this.socket.getInputStream();
        
        int fileByte = is.read();
        
        while(fileByte != -1)
        {
           fos.write(fileByte);
           fileByte = is.read();

           //System.out.println(counter + "-" + fileSize); // Transfer percentage
           //counter++;
        }

        fos.close();
    }
    //endregion
}
