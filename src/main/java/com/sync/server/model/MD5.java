package com.sync.server.model;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private MD5() {}

    public static String getMD5(Path path) throws NoSuchAlgorithmException, IOException {

        if (path.toFile().isDirectory()) return null;
        if (Files.size(path) > Integer.MAX_VALUE - 8) return null;

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(Files.readAllBytes(path));

        byte[] digest = messageDigest.digest();

        return DatatypeConverter.printHexBinary(digest).toUpperCase();

    }

}
