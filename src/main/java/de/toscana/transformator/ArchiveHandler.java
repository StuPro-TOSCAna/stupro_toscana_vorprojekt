package de.toscana.transformator;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by nick on 28.06.17.
 */
public class ArchiveHandler {
    public class ArchiveException extends Exception {
        public ArchiveException(String message){
            super(message);
        }
    }
    final File archive;
    public ArchiveHandler(File file) throws ArchiveException {
        archive = file;
        String fileName = file.getName();
        if(!file.exists()) throw new ArchiveException("There is no file with the name "+file.getName()+".");

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (!validateExtension(extension)) throw new ArchiveException(fileName+" is not a valid archive file.");
    }


    private static boolean validateExtension(String extension) {
        return "zip".equalsIgnoreCase(extension);
    }


    public String getModelXml() throws ArchiveException {
        ZipEntry model = null;
        ZipFile zipfile = null;
        try {
            ZipEntry entry;
            zipfile = new ZipFile(archive);
            Enumeration e = zipfile.entries();
            while(e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if("model.xml".equals(entry.getName())) {
                    model = entry;
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(model == null){
            throw new ArchiveException("Parsing the archive went wrong. Check if a model.xml is existent.");
        }

        return parseXmlFileToString(model, zipfile);
    }

    private String parseXmlFileToString(ZipEntry model, ZipFile zipfile) {
        StringBuilder builder = new StringBuilder();
        InputStream stream;
        try {
            stream = zipfile.getInputStream(model);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while( (line = reader.readLine()) != null )
            {
                line = line.trim();
                builder.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        return builder.toString();
    }
}
