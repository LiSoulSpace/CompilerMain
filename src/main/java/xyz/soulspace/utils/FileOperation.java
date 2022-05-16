package xyz.soulspace.utils;

import java.io.*;

/**
 * @author :lisoulspace
 * @create :2022-05-15 10:34
 */
public class FileOperation {
    public static void printStringToFile(String filename,String... contents) {
        File outputFile = new File(filename);
        if (!outputFile.exists()){
            try {
                boolean newFile = outputFile.createNewFile();
                if (!newFile) System.out.println("Creating file failed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.newLine();
            for (String ele : contents) {
                writer.write(ele);
                writer.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
