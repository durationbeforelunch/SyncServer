package com.sync.server.network.controller;

import com.sync.server.model.ExtendedFile;
import com.sync.server.storage.FileStorage;
import com.sync.server.view.UserView;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Log4j2
@RestController
@RequestMapping
public class RestfulController {

    private final UserView userView;
    private final FileStorage fileStorage;

    public RestfulController(UserView userView,FileStorage fileStorage) {
        this.userView = userView;
        this.fileStorage = fileStorage;
    }

    @GetMapping("/files")
    public List<ExtendedFile> getAllFiles() {
        return fileStorage.getAll();
    }

    // RU: Эндпоинт скачивания с сервера
    // EN: Download endpoint
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> uploadFileById(@PathVariable int id) {

        Path path;

        try {
            path = fileStorage.getById(id).getPath();
        } catch (IndexOutOfBoundsException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File file = new File(path.toString());

        ByteArrayResource byteArrayResource = null;

        try {
            byteArrayResource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        userView.appendLog(String.format("Скачивание файла - %s", file.getName())); // Downloading log
        log.info("Скачивание файла - {}", file.getName()); // Downloading log

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);

    }

}




