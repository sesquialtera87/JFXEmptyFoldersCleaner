package org.mth.jfxemptyfolderscleaner;

import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import javafx.concurrent.Task;

/**
 *
 * @author Mattia Marelli
 * @since 5-ott-2014
 */
public class ScanEmptyFoldersTask extends Task implements FileVisitor<Path> {

    /**
     * Percorso root da cui partire per la scansione del file-tree
     */
    private final Path rootPath;
    /**
     * Numero di cartelle vuote trovate durante la scansione
     */
    private int emptyFoldersCount = 0;
    /**
     * Numero di cartelle visionate
     */
    private int scannedFoldersCount = 0;
    /**
     * Instante in cui è iniziata la task
     */
    private long initialTime;
//    private final Queue<BranchLevel> branch;
//    private int maxDepth = 0;

    public ScanEmptyFoldersTask(String path) {
        rootPath = Paths.get(path);
//        branch = new ConcurrentLinkedQueue<>();
//        branch.add(new BranchLevel(rootPath));
    }

    private void publishMessage(String message) {
        TextLogger.publishMessage(message);
    }

    @Override
    protected final Object call() throws Exception {
        System.err.println("start!");
        initialTime = System.currentTimeMillis(); // tiene traccia dell'inizio del processo ...

        publishMessage(format("Process started at %s", new Date(initialTime)));
        publishMessage("Scanning subtree for empty folders ...");

        Files.walkFileTree(rootPath, this);

        publishMessage(TextLogger.FINISH_MESSAGE);

        return null;
    }

    @Override
    protected void succeeded() {
        long time = System.currentTimeMillis() - initialTime; // total time elapsed ...
        time /= 1000; // ... in seconds!

        publishMessage(format("Visited %d folders", scannedFoldersCount));
        publishMessage(format("Found %d empty folder(s)  [Total time: %d seconds]", emptyFoldersCount, time));
    }

    @Override
    public final FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {
            System.out.println("prev: " + dir);
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
            publishMessage(TextLogger.BLANK_MESSAGE);
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (isCancelled()) {
            return FileVisitResult.TERMINATE;
        } else {

//        BranchLevel level = branch.poll();
            scannedFoldersCount++;
            File file = dir.toFile();
            int n = file.list().length;

            if (n == 0) {
                System.out.println(dir.toString());
                if (file.isHidden()) {
                    publishMessage(format("\tEmpty folder (hidden) -> %s", dir));
                } else {
                    publishMessage(format("\tEmpty folder -> %s", dir));
                }

                emptyFoldersCount++;
            }

            return FileVisitResult.CONTINUE;
        }
    }

    static class BranchLevel {

        /**
         * Percorso della directory
         */
        private Path path;
        /**
         * Numero di elementi contenuti nella cartella (file, sottocartelle)
         */
        private int childCount = 0;
        private int emptyFiles = 0;
        private boolean scanEmptyFiles = true;

        public BranchLevel(Path path) {
            this.path = path;
        }

        public boolean isScanEmptyFiles() {
            return scanEmptyFiles;
        }

        public void setScanEmptyFiles(boolean scanEmptyFiles) {
            this.scanEmptyFiles = scanEmptyFiles;
        }

        public int getEmptyFiles() {
            return emptyFiles;
        }

        public void setEmptyFiles(int emptyFiles) {
            this.emptyFiles = emptyFiles;
        }

        public int getChildCount() {
            return childCount;
        }

        public void setChildCount(int childCount) {
            this.childCount = childCount;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public void incrementChildCount() {
            childCount++;
        }

        public void incrementEmptyFilesCount() {
            emptyFiles++;
        }
    }
}
