package pl.dic.sync.client.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pl.dir.sync.common.dto.FileKeyDto;

@Service
@Slf4j
public class FileSenderService {
    private static final int _900kB = 1024 * 900;
    private static final int LARGE_TRANSFER_DETECTION_TIMEOUT = 200;
    private WatchService watchService;
    private boolean expectedClose;
    @Autowired
    private UserProducerService userProducerService;

    public void stopListenDir() {
        try {
            this.expectedClose = true;
            this.watchService.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected stop listenning exception", e);
        }
    }

    @Async
    public void listenDir(String username, String observedDir) {
        Path dir = obtainDirPath(observedDir);
        watchService = obtainWatchService();
        registerWatchService(watchService, dir);

        WatchKey watchKey = takeDirEvent();
        while (watchKey != null) {
            log.info("Path being watched: " + watchKey.watchable());

            if (watchKey.isValid()) {
                interprateDirEvent(dir, watchKey, username);

                boolean valid = watchKey.reset();
                if (!valid) {
                    log.info("The watchKey is not longer registered");
                }
            }

            watchKey = takeDirEvent();
        }
    }

    private void interprateDirEvent(Path dir, WatchKey watchKey, String username) {
        List<WatchEvent<?>> allWatchEvents = new ArrayList<>(watchKey.pollEvents());
        for (int i = 0 ; i < allWatchEvents.size() ; i++) {
            WatchEvent<?> event = allWatchEvents.get(i);
            Path destFile = (Path) event.context();
            log.debug("Kind: " + event.kind() + ", destFile: " + destFile + ", Count: " + event.count());
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {

                sendFile(dir, destFile, username);

                largeFileTransferKeepALive(allWatchEvents);
            }
        }
    }

    public void sendFile(String dirAP, String destFileName, String userName) {
        Path dir = Paths.get(dirAP);
        Path destFile = Paths.get(destFileName);

        sendFile(dir, destFile, userName);
    }

    private void sendFile(Path dir, Path destFile, String username) {
        File f = new File(dir.toFile().getAbsolutePath() + File.separator + destFile);
        if (f.isDirectory()) {
            FileKeyDto key = resolveKey(dir, f);
            key.setDir(true);
            try {
                this.userProducerService.sendFile(username, key, new byte[0]);
            } catch (InterruptedException | ExecutionException e) {
                log.info("cannot send dir", e);
            }
        } else {
            sendRegualFile(destFile, dir, f, username);
        }
    }

    private void largeFileTransferKeepALive(List<WatchEvent<?>> allWatchEvents) {
        try {
            WatchKey key = watchService.poll(LARGE_TRANSFER_DETECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            if (key != null) {
                allWatchEvents.addAll(key.pollEvents());
            }
        } catch (InterruptedException e1) {
            log.error("iterrupted poll", e1);
        }
    }

    private void sendRegualFile(Path destFile, Path dir, File f, String username) {
        try (InputStream is = new FileInputStream(f);
                BufferedInputStream in = new BufferedInputStream(is)) {
            byte[] bbuf = new byte[_900kB];
            int len;
            while ((len = in.read(bbuf)) != -1) {
                FileKeyDto key = resolveKey(dir, f);
                if (len < _900kB) {
                    byte[] bbufEof = new byte[len];
                    System.arraycopy(bbuf, 1, bbufEof, 0, len);
                    this.userProducerService.sendFile(username, key, bbufEof);
                } else {
                    this.userProducerService.sendFile(username, key, bbuf);
                }
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("cannot send message to topic {}", username, e);
        }
    }

    private FileKeyDto resolveKey(Path dir, File destFile) {
        String destAbs = destFile.getAbsolutePath();
        int wathDirLength = dir.toFile().getAbsolutePath().length();
        String relativePath = destAbs.substring(wathDirLength);
        FileKeyDto dto = new FileKeyDto();
        dto.setRelativePath(relativePath);
        return dto;
    }

    private WatchKey takeDirEvent() {
        WatchKey watchKey;
        try {
            watchKey = watchService.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Unexpected listen stop", e);
        } catch (ClosedWatchServiceException e) {
            if (this.expectedClose) {
                return null;
            } else {
                throw new IllegalStateException("Unexpected listen close", e);
            }
        }
        return watchKey;
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