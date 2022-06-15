/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.classes;

public class MyThread extends Thread {
    private Thread thread;
    private setOnThreadRun onThreadRun;
    private setOnThreadRunArgs onThreadRunArgs;
    public MyThread(){}
    public MyThread(Runnable runnable) {
        this.thread = new Thread(runnable);
    }
    public void run() {
        if (onThreadRun != null){
            onThreadRun.onThread();
            onThreadRun = null;
        } else if (onThreadRunArgs != null) {
            onThreadRunArgs.onThread();
            onThreadRunArgs = null;
        } else {
            for (int i=0; i<5;i++){
                System.out.println("Thread"+i);
            }
        }
    }
    public void start(){
        this.thread.start();
    }
    public void startThread(setOnThreadRun onThreadRun) {
        this.onThreadRun = onThreadRun;
        this.start();
    }

    public void setOnThreadRun(setOnThreadRunArgs onThreadRun) {
        this.onThreadRunArgs = onThreadRun;
        this.start();
    }
    
    public static interface setOnThreadRun{
        public abstract void onThread();
    }
    public static interface setOnThreadRunArgs{
        public abstract void onThread(Object... args);
    }
}
