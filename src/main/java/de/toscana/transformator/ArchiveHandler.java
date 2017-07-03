package de.toscana.transformator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by nick on 28.06.17.
 */
public class ArchiveHandler {
    private final File archive;
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveHandler.class);

    /**
     * Exceptions for the Archive Handler
     */
    public class ArchiveException extends Exception {
        public ArchiveException(String message){
            super(message);
        }
        public ArchiveException(Throwable cause) {
            super(cause);
        }
    }


    /**
     * Checks if the given archive is valid
     * @param file the Archive
     * @throws ArchiveException
     */
    public ArchiveHandler(File file) throws ArchiveException {
        archive = file;
        String fileName = file.getName();
        if(!file.exists()) throw new ArchiveException("There is no file with the name "+file.getName()+".");
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (!isZip(extension)) throw new ArchiveException(fileName+" is not a valid archive file.");
    }


    private static boolean isZip(String extension) {
        return "zip".equalsIgnoreCase(extension);
    }

    /**
     * parses the archive file for the model.xml
     * @return model.xml as one line string
     * @throws ArchiveException
     */
    public String getModelXmlFromZip() throws ArchiveException {
        ZipEntry modelFile = null;
        ZipFile zipfile = null;
        try {
            ZipEntry entry;
            zipfile = new ZipFile(archive);
            Enumeration e = zipfile.entries();
            //parse all elements in the archive
            while(e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if("model.xml".equals(entry.getName())) {
                    modelFile = entry;
                    break;
                }
            }
        } catch(Exception e) {
            throw new ArchiveException(e);
        }
        if(modelFile == null){
            throw new ArchiveException("Parsing the archive went wrong. Check if a model.xml is existent.");
        }

        return transformXmlFileToString(modelFile, zipfile);
    }

    /**
     * gets the model.xml and transforms it the a one line string without whitespace between elements
     * @param modelFile model.xml
     * @param zipfile the archive file
     * @return the file as one line string
     */
    private String transformXmlFileToString(ZipEntry modelFile, ZipFile zipfile) throws ArchiveException {
        StringBuilder builder = new StringBuilder();
        InputStream stream;
        try {
            stream = zipfile.getInputStream(modelFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while( (line = reader.readLine()) != null )
            {
                line = line.trim();
                builder.append(line);
            }
        } catch (IOException e) {
            throw new ArchiveException("Transforming the XML to a single line string failed.");
        }
        return builder.toString();
    }
}
