package GUIClasses.TableClasses;

import java.io.File;

//Данный класс содержит информацию о директории.
public class DirInfo {

    private File directory;
    private FileInfo fileInfoDirectory;
    private boolean isRoot;

    public DirInfo(File file, boolean isRoot){
        directory = file;
        fileInfoDirectory = FileInfo.convertFromFile(file);
        this.isRoot = isRoot;
    }

    public File getDirectory() {
        return directory;
    }

    public FileInfo getFileInfoDirectory() {
        return fileInfoDirectory;
    }

    public boolean getIsRoot() {
        return isRoot;
    }
}
