package pl.dic.sync.client.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class DirSenderService {
    private WatchService watchService;
    private boolean expectedClose;

    public void init(String username, String observedDir) {
        Path dir = obtainDirPath(observedDir);
        watchService = obtainWatchService();
        registerWatchService(watchService, dir);
    }

    public void stopListenDir() {
        try {
            this.expectedClose = true;
            this.watchService.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected stop listenning exception", e);
        }
    }

    @Async
    public void listenDir() {
        while (true) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Unexpected listen stop", e);
            } catch (ClosedWatchServiceException e) {
                if (this.expectedClose) {
                    return;
                } else {
                    throw new IllegalStateException("Unexpected listen close", e);
                }
            }

//            log.info("Path being watched: " + watchKey.watchable());

            if (watchKey.isValid()) {
              for (WatchEvent<?> event : watchKey.pollEvents()) {
//                log.info("Kind: " + event.kind() + ", Context: " + event.context() + ", Count: " + event.count());
              }

              boolean valid = watchKey.reset();
              if (!valid) {
                // The watchKey is not longer registered
              }
            }
          }
    }

    private void registerWatchService(WatchService watchService, Path dir) {
        try {
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot register watch service to listen directory", e);
        }
    }

    private Path obtainDirPath(String observedDir) {
        URI obsDirURL;

        try {
            obsDirURL = new URL("file://" + observedDir).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Cannot find obsered directory " + observedDir, e);
        }
        
        return Paths.get(obsDirURL);
    }

    private WatchService obtainWatchService() {
        WatchService watchService;
        FileSystem fileSystem = FileSystems.getDefault();

        try {
            watchService = fileSystem.newWatchService();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get watch service ", e);
        }

        return watchService;
    }
}