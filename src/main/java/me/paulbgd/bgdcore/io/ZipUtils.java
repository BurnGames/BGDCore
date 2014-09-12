package me.paulbgd.bgdcore.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ZipUtils {

    public static void writeZip(HashMap<InputStream, String> hashMap, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (Map.Entry<InputStream, String> entry : hashMap.entrySet()) {
            if (entry.getKey().available() == 0) {
                // empty array
                continue;
            }
            zipOutputStream.putNextEntry(new ZipEntry(entry.getValue()));
            IOUtils.copy(entry.getKey(), zipOutputStream);
            IOUtils.closeQuietly(entry.getKey());
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
    }

    public static void zipDirectory(OutputStream outputStream, File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Is not directory!");
        } else if (directory.listFiles() == null) {
            return;
        }
        HashMap<InputStream, String> hashMap = new HashMap<>();
        List<File> files = new ArrayList<>();
        getFiles(directory, files);
        String current = directory.getAbsolutePath();
        for (File file : files) {
            hashMap.put(new FileInputStream(file), file.getAbsolutePath().replace(current, "").substring(1));
        }
        writeZip(hashMap, outputStream);
    }

    private static void getFiles(File directory, List<File> files) {
        if (!directory.isDirectory() || directory.listFiles() == null) {
            return;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                getFiles(file, files);
            } else {
                files.add(file);
            }
        }
    }

    public static HashMap<InputStream, String> readZip(InputStream inputStream) throws IOException {
        HashMap<InputStream, String> list = new HashMap<>();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            if (!entry.isDirectory()) {
                byte[] bytes = new byte[zipInputStream.available()];
                IOUtils.readFully(zipInputStream, bytes);
                list.put(new ByteArrayInputStream(bytes), entry.getName());
            }
            zipInputStream.closeEntry();
            entry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
        return list;
    }

    public static void extractZip(InputStream inputStream, File outputDirectory) throws IOException {
        HashMap<InputStream, String> extracted = readZip(inputStream);
        for (Map.Entry<InputStream, String> entry : extracted.entrySet()) {
            File output = new File(outputDirectory, entry.getValue());
            if (!output.getParentFile().exists() && !output.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory " + output.getParent() + "!");
            }
            if (!output.createNewFile()) {
                throw new IOException("Failed to create file " + output + "!");
            }
            FileUtils.copyInputStreamToFile(entry.getKey(), output);
        }
    }

}
