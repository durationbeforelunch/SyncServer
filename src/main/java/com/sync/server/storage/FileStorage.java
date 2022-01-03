package com.sync.server.storage;

import com.sync.server.model.ExtendedFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class FileStorage {

    // Временное хранилище
    private List<ExtendedFile> extendedFileList;
    private File origin;

    // Метод, который вызывает рекурсивную инициализацию структуры переданной папки
    private void updateFileStructure() {

        List<File> tmp = new ArrayList<>();

        // Здесь маппим объекты File в ExtendedFile, одновременно проходя по структуре папки
        // origin.toPath().getNameCount() для того, чтобы делать подпуть
        extendedFileList = getFileList(origin, tmp)
                .stream()
                .map(e -> new ExtendedFile(e, origin.toPath().getNameCount()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<File> getFileList(File file, List<File> list) {

        // Рекурсивно идём до последней папки и собираем файлы от конца и до начала
        if (file.isDirectory()) {

            for (File $file : file.listFiles()) {

                getFileList($file, list);
                list.add($file);

            }

        }

        return list;

    }

    // Простой сеттер, инициализатор
    public void setOrigin(String path) {
        origin = new File(path);
        updateFileStructure();
    }

    // Метод для использования REST контроллером
    public ExtendedFile getById(int id) {
        if (extendedFileList.size() < id) throw new IndexOutOfBoundsException();
        else return extendedFileList.get(id);
    }

    // Метод для использования REST контроллером
    public List<ExtendedFile> getAll() {
        return new ArrayList<>(extendedFileList);
    }

}
