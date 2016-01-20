package com.ecwid.cdownloader;

import com.ecwid.cdownloader.Throttle.GuavaThrottleImpl;
import com.ecwid.cdownloader.Throttle.Throttle;
import com.ecwid.cdownloader.Throttle.TokenBucketThrottleImpl;

public class Main {
    public static void main(String[] args) {
        try {
            /** Create throttle */
//            Throttle throttle = new TokenBucketThrottleImpl();
            Throttle throttle = new GuavaThrottleImpl();

            /** Create console downloader args */
            ConsoleArgs consoleArgs = new ConsoleArgsImpl();

            /** Create console downloader */
            ConsoleDownloader consoleDownloader = new ConsoleDownloader();

            /** Dependency inject */
            consoleDownloader
                    .setThrottle(throttle)
                    .setConsoleArgs(consoleArgs)
            ;

            /** Parse args */
            consoleDownloader.parseArgs(args);

            /** Run console downloader */
            consoleDownloader.run();
        } catch (Exception e) {
            System.err.println("Error on run ConsoleDownloader. " + e.getMessage());
        }
    }
}
