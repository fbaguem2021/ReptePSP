/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.classes;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;



/**
 * this is a class for reading and writing of text files
 * @author Francesc Bagué Martí
 * @version 1.0
 * @since 2022/02/16
 */
public abstract class FileManager {
    private String filePath;
    private File file;
    
    private FileManager(String filepath) {
        this.filePath = filepath;
        this.file = new File(filePath);
    }
    public static Reader getReader(String filepath) {
        return new Reader(filepath);
    }
    public static Writer getWriter(String filepath) {
        return new Writer(filepath);
    }
    /**
     * @return Returns a FileReader
     * @throws FileNotFoundException Throws a FileNotFoundException
     */
    private FileReader getFileReader() throws FileNotFoundException {
        return new FileReader(file);
    }
    /**
     * @return Returns a FileWriter
     * @throws IOException Throws an IOException
     */
    private FileWriter getFileWriter() throws IOException {
        return new FileWriter(file);
    }
    
    /**
     * @return Returns a File
     */
    public File getFile() {
        return this.file;
    }
    /**
     * @return Returns a String
     */
    public String getFilePath() {
        return filePath;
    }
    /**
     * @param newFilePath New file path
     */
    public void setFile(String newFilePath) {
        this.filePath = newFilePath;
        this.file = new File(newFilePath);
    }
    /**
     * @param newFile New File object
     */
    public void setFile(File newFile) {
        this.filePath = newFile.getPath();
        this.file = newFile;
    }
    
    /**
     * Method that starts the Reader / Writer
     * @throws FileNotFoundException Throws a FileNotFoundException
     * @throws IOException Throws an IOException
     */
    abstract public void start() throws FileNotFoundException, IOException;
    /**
     * Method that closes the Reader / Writer
     * @throws IOException Throws an IOException
     */
    abstract public void close() throws IOException;
    
    /**
     * Class for the reading of text files
     */
    public static class Reader extends FileManager {
        private BufferedReader reader;
        private boolean hasBeenStarted = false;

        /**
         * Constructor for the Reader
         * @param file The file's path
         */
        private Reader(String file) {
            super(file);
        }
        
        @Override
        public void start() throws FileNotFoundException {
            this.reader = new BufferedReader(super.getFileReader());
            this.hasBeenStarted = true;
        }
        @Override
        public void close() throws IOException {
            this.reader.close();
            this.hasBeenStarted = false;
        }
        /**
         * Method that reads a single character as an integer
         * @return Returns an integer value
         * @throws IOException Throws an exception
         */
        public int read() throws IOException {
            if (this.hasBeenStarted) {
                try {
                    return this.reader.read();
                } catch (IOException ex) {
                    throw new IOException(ex);
                }
            } else
                throw new IOException();
        }
        /**
         * Method that reads a single line from a text file
         * @return Returns a String object
         * @throws IOException Throws a custom Exception
         */
        public String readLine() throws IOException {
            if (this.hasBeenStarted)
                try {
                    return this.reader.readLine();
                } catch (IOException ex) {
                    throw new IOException(ex);
                }
            else 
                throw new IOException();
        }
        /**
         * Method that reads all the text inside a file and returns it inside an ArrayList
         * @return Returns an ArrayList object
         * @throws IOException Throws a custom Exception
         */
        public ArrayList<String> readAll() throws IOException {
            if (this.hasBeenStarted) {
                ArrayList<String> content = new ArrayList<>();
                String line;
                try {
                    while ( (line = this.reader.readLine()) != null ) {
                        content.add(line);
                    }
                    return content;
                } catch (IOException ex) {
                    throw new IOException(ex);
                }
            } else 
                throw new IOException();
                
        }

    }
    
    /**
     * Class for the writing of text files
     */
    public static class Writer extends FileManager {
        private BufferedWriter writer;
        private boolean hasBeenStarted = false;

        /**
         * Constructor for the Writer
         * @param file The file's path
         */
        private Writer(String file) {
            super(file);
        }
        
        @Override
        public void start() throws FileNotFoundException, IOException {
            this.writer = new BufferedWriter(super.getFileWriter());
            this.hasBeenStarted = true;
        }

        @Override
        public void close() throws IOException {
            this.writer.close();
            this.hasBeenStarted = false;
        }
        
        /**
         * Method that writes a single line in a text file
         * @param line Line that will be written
         * @throws IOException Throws a custom Exception
         */
        public void writeLine(String line) throws IOException {
            if (this.hasBeenStarted)
                try {
                    this.writer.write(line);
                } catch (IOException ex) {
                    throw new IOException(ex);
                }
            else 
                throw new IOException();
        }
        /**
         * Method that writes a whole text file
         * @param lines Array with all the lines that will be written in the file
         * @throws IOException Throws a custom Exception
         */
        public void writeAll(String[] lines) throws IOException {
            if (this.hasBeenStarted) {
                try {
                    for (String line : lines)
                        this.writer.write(line);
                } catch (IOException ex) {
                    throw new IOException(ex);
                }
                        
            } else 
                throw new IOException();
        }
        /**
         * Method that writes a whole text file
         * @param lines List with all the lines that will be written in the file
         * @throws IOException Throws a custom Exception
         */
        public void writeAll(ArrayList<String> lines) throws IOException {
            if (this.hasBeenStarted) {
                try {
                    int i = 1;
                    for (String line : lines)
                        if (i != lines.size()) {
                            this.writer.write(line+"\n");
                            i++;
                        } else {
                            this.writer.write(line);
                        }

                } catch (IOException ex) {
                    throw new IOException(ex);
                }
                        
            } else 
                throw new IOException();
        }
    }
}