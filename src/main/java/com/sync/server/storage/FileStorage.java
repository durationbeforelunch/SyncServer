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

    // RU: Временное хранилище
    // EN: Temporary storage
    private List<ExtendedFile> extendedFileList;
    private File origin;

    // RU: Метод, который вызывает рекурсивную инициализацию структуры переданной папки
    // EN: Method, which one calls for recursively initialization of passed directory structure
    private void updateFileStructure() {

        List<File> tmp = new ArrayList<>();

/*       RU: Здесь маппим объекты File в ExtendedFile, одновременно проходя по структуре папки
         origin.toPath().getNameCount() для того, чтобы делать подпуть

         EN: Mapping an object File to an ExtendedFile, while walking the directory structure
         origin.toPath().getNameCount() for making subPath

         */
        extendedFileList = getFileList(origin, tmp)
                .stream()
                .map(e -> new ExtendedFile(e, origin.toPath().getNameCount()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<File> getFileList(File file, List<File> list) {

        // RU: Рекурсивно идём до последней папки и собираем файлы от конца и до начала
        // EN: Recursively walking till end and collecting files/directories
        if (file.isDirectory()) {

            for (File $file : file.listFiles()) {

                getFileList($file, list);
                list.add($file);

            }

        }

        return list;

    }

    // RU: Простой сеттер, инициализатор
    // EN: Plain setter, initializer
    public void setOrigin(String path) {
        origin = new File(path);
        updateFileStructure();
    }

    // RU: Метод для использования REST контроллером
    // EN: Method for REST controller
    public ExtendedFile getById(int id) {
        if (extendedFileList.size() < id) throw new IndexOutOfBoundsException();
        else return extendedFileList.get(id);
    }

    // RU: Метод для использования REST контроллером
    // EN: Method for REST controller
    public List<ExtendedFile> getAll() {
        return new ArrayList<>(extendedFileList);
    }

}
