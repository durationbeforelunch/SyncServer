package com.sync.server.model;

import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Getter
public class ExtendedFile implements Comparable<String> {

    private final String fileName;
    private final String hashMD5;
    private final String subPath;
    private final Path path;
    private final File file;
    private final boolean isDirectory;

    public ExtendedFile(File file, int cutOffIndex) {
        this.file = file;
        this.path = file.toPath();
        this.fileName = file.getName();
        this.isDirectory = file.isDirectory();
        this.subPath = this.path.subpath(cutOffIndex, this.path.getNameCount()).toString();
        this.hashMD5 = setHashMD5();
    }

    private String setHashMD5() {

        String mdHash = null;

        try {
            mdHash = MD5.getMD5(path);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return mdHash;

    }

    @Override
    public String toString() {
        return String.format("%-20s %-50s %-40s %-100s %s",
                (isDirectory ? "FOLDER" : "FILE"),
                fileName,
                (hashMD5 != null ? hashMD5 : "NO MD5 HASH"),
                subPath,
                path.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedFile object = (ExtendedFile) o;
        return Objects.equals(fileName, object.fileName) &&
                Objects.equals(hashMD5, object.hashMD5) &&
                Objects.equals(subPath, object.getSubPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName,
                            hashMD5,
                            subPath);
    }

    @Override
    public int compareTo(@NonNull String comparableString) {
        return this.fileName.compareTo(comparableString);
    }
}