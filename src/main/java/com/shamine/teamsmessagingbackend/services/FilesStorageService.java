package com.shamine.teamsmessagingbackend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class FilesStorageService {

    public boolean save(MultipartFile file, String path, String fileName) {
        try {
            File folder = new File(path + "/");
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("folder created");
                }
            }
            OutputStream out = new FileOutputStream(new File(path + File.separator + fileName));
            InputStream fileContent = file.getInputStream();
            int read;
            final byte[] bytes = new byte[1024];

            while ((read = fileContent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.close();
            fileContent.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
