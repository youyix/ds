package client.watcher;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

/**
 * File watcher(s).
 * @author Zhenfei Nie<zhen.fei.nie@usi.ch>
 * @author Xiyu Lyu<xi.yu.lyu@usi.ch>
 *
 */
public class Watcher extends Thread{
	
	public static void main(String[] args) throws IOException {
		Watcher w = new Watcher(Paths.get("/Users/niezhenfei/kk"));
		w.start();
	}
	
	private final WatchService watcher;
	public final Path dir;
	private volatile boolean flag = true;
	
	List<Watcher> wooos;
	
	public Watcher( Path dir ) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
	    this.dir = dir;
	    
	    wooos = new ArrayList<Watcher>();
	    
	    init();
	}
	
	public void init() throws IOException {
		File root = dir.toFile();
		File list[] = root.listFiles(); 
		for ( File f: list ) {
			if ( f.isDirectory() ) {
				Watcher w = new Watcher(f.toPath());
				wooos.add(w);
				w.start();
			} else {
			}
		}
	}

	public void start() {
		System.err.println("Thread for " + dir);
		super.start();
	}
	
	public void terminates() {
		for( Watcher w: wooos ) {
    		w.terminates();
    	}
		flag = false;
	}
	
	@SuppressWarnings("rawtypes")
	public void run() {
		while (flag) {
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
                
                if ( kind == ENTRY_DELETE ) {
                	@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
                    System.out.println("Delete: " + filename + " " + this.dir);
                    
                	for( Watcher w: wooos ) {
                		if ( w.dir.endsWith(ev.context().toString()) ) {
                			w.terminates();
                		}
                	}
                }
                
                if ( kind == ENTRY_CREATE ) {
                	//The filename is the context of the event.
                    @SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();

                    System.out.println("Create: " + filename + " " + this.dir);
                    
                    File nf = dir.resolve(filename).toFile();
                    if ( nf.isDirectory() ) {
                    	Watcher w = null;
						try {
							w = new Watcher(nf.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
        				wooos.add(w);
        				
        				w.start();
                    }
                }
                
                if ( kind == ENTRY_MODIFY ) {
                	//The filename is the context of the event.
                    @SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
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
		System.err.println("Terminated: " + dir);
	}
}
