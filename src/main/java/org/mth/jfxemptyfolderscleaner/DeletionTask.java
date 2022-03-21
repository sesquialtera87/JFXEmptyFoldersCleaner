package org.mth.jfxemptyfolderscleaner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Queue;

import javafx.concurrent.Task;

/**
 * @author Mattia Marelli
 * @since 5-ott-2014
 */
public class DeletionTask extends Task<Void> implements FileVisitor<Path> {

    /**
     * Numero di cartelle eliminate
     */
    private int deletedFoldersCounter = 0;
    /**
     * Percorso del filesystem in cui cercare le cartelle vuote
     */
    private final Path rootPath;
    private long startTime;
    private Queue<String> messageQueue;

    /**
     * @param rootPath Stringa che indica il percorso da cui partire per la
     *                 ricerca di cartelle vuote
     */
    public DeletionTask(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    public final void setMessageQueue(Queue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    private void publishMessage(String message) {
        messageQueue.add(message);
    }

    @Override
    protected Void call() throws Exception {
        startTime = System.currentTimeMillis();

        publishMessage(String.format("Process started at %s", new Date(startTime)));
        publishMessage("Deleting empty folders ...");

        Files.walkFileTree(rootPath, this);

//        return deletedFoldersCounter;
        return null;
    }

    @Override
    protected void succeeded() {
        long time = System.currentTimeMillis() - startTime;
        time /= 1000;

        publishMessage(String.format("Deleted %d empty folder(s)  [Total time: %d seconds]", deletedFoldersCounter, time));
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {
            System.err.println("failed visiting " + file);
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {
            int n = dir.toFile().list().length;

            if (n == 0) {
                Files.delete(dir);
                publishMessage(String.format("\tDeleted folder -> %s", dir));
                deletedFoldersCounter++;
            }

            return FileVisitResult.CONTINUE;
        }
    }

}
