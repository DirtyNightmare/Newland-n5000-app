package com.ominidata.device.io;

/**
 * Created by kewin on 18-12-2016.
 */

import java.io.*;

public class FileChecker {

    private static final String FILE_DIR = "/sdcard/Data/pics";
    private static final String FILE_TEXT_EXT = ".txt";

    public static void main(String args[]) {
        new FileChecker().deleteFile(FILE_DIR,FILE_TEXT_EXT);
    }

    public void deleteFile(String folder, String ext){

        GenericExtFilter filter = new GenericExtFilter(ext);
        File dir = new File(folder);

        //list out all the file name with .txt extension
        String[] list = dir.list(filter);

        if (list.length == 0) return;

        File fileDelete;

        for (String file : list){
            String temp = new StringBuffer(FILE_DIR)
                    .append(File.separator)
                    .append(file).toString();
            fileDelete = new File(temp);
            boolean isdeleted = fileDelete.delete();
            System.out.println("file : " + temp + " is deleted : " + isdeleted);
        }
    }

    //inner class, generic extension filter
    public class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
}