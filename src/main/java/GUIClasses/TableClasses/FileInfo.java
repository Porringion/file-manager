package GUIClasses.TableClasses;

import java.io.File;

public class FileInfo {
    private String filePath;
    private long size;
    private String name;
    private String type;
    int lastIndex;

    public static String TYPE_DIR = "dir";
    public static String TYPE_FILE = "file";

    FileInfo(){
        size = 0;
        name = "";
        type = "";
        filePath = "";
        lastIndex = 0;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static FileInfo convertFromFile(File file){

        FileInfo fileInfo = new FileInfo();

        if(file == null)
            return fileInfo;

        if(file.isDirectory())
            fileInfo.type = "dir";
        else
            fileInfo.type = "file";

        fileInfo.size = file.length();
        fileInfo.name = file.getName();
        fileInfo.filePath = file.getAbsolutePath();

        return fileInfo;
    }
}
