/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package utilities;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author TerryVa
 */
public class bsComm {
    private ScheduledExecutorService scheduler;

    public void startService() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new MyScheduledTask(), 0, 10, TimeUnit.SECONDS);
        System.out.println("Service started. Task scheduled every 10 seconds.");

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownService()));
    }

    public void shutdownService() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("Shutting down service...");
            scheduler.shutdown(); // Initiate graceful shutdown
            try {
                // Wait for existing tasks to complete within a timeout
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow(); // Force shutdown if tasks don't complete
                    System.out.println("Service shutdown forced.");
                } else {
                    System.out.println("Service shut down gracefully.");
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                System.err.println("Service shutdown interrupted.");
            }
        }
    }

    public static void main(String[] args) {
    	bsComm service = new bsComm();
        service.startService();
        // Keep the main thread alive for the service to run (e.g., in a server application)
        // For a simple standalone example, you might add a delay or a loop.
    }
    
    
    
    public class MyScheduledTask implements Runnable {
        private int counter = 0;

        @Override
        public void run() {
            System.out.println("Executing task. Count: " + ++counter);
            // Your task logic here
        }
    }
}
