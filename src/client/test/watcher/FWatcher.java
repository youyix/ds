package client.test.watcher;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class FWatcher {

	private final WatchService watcher;
	private final Path dir;
	
	FWatcher(Path dir) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
	    this.dir = dir;
	}
	
	
	
	void processEvents() {
        while (true) {
            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }
                
                if ( kind == ENTRY_CREATE ) {
                	//The filename is the context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();

                    System.out.println("C: " + filename);
                }
                
                if ( kind == ENTRY_MODIFY ) {
                	//The filename is the context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
                    Path child = dir.resolve(filename);
                    
//                    child.

                    System.out.println("M: " + filename);
                }
                
            }

            //Reset the key -- this step is critical if you want to receive
            //further watch events. If the key is no longer valid, the directory
            //is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                    break;
            }
        }
    }
	
	private static void usage() {
        System.err.println("usage: java Email dir");
        System.exit(-1);
    }
	
	public static void main(String[] args) throws IOException {
        //parse arguments
//        if (args.length < 1)
//            usage();

        //register directory and process its events
//        Path dir = Paths.get(args[0]);
        Path dir = Paths.get("/Users/niezhenfei/kk");
        new FWatcher(dir).processEvents();
    }

}
