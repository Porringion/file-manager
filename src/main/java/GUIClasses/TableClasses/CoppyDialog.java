package GUIClasses.TableClasses;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CoppyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar copyProgressBar;
    private File copyTo;
    private String[] arrPath;
    private int fileCount = 0;
    private int curValue = 1;

    public CoppyDialog(String[] arrPath, File to) {

        this.copyTo = to;
        this.arrPath = arrPath;

        setContentPane(contentPane);
        setModal(true);
        pack();

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        int fileCount = getFileCount(convertArrPathToArrFile(arrPath));

        copyProgressBar.setMinimum(0);
        copyProgressBar.setMaximum(fileCount);

        Thread thread = new Thread("copyThread") {
            @Override
            public void run() {
                startCopyTo();
            }
        };
        thread.start();

        setVisible(true);
    }


    private File[] convertArrPathToArrFile(String[] arrPath){

        File[] listFile = new File[arrPath.length];
        int counter = 0;

        for (String path:arrPath) {
            listFile[counter] = new File(path);
            counter++;
        }

        return listFile;
    }

    private int getFileCount(File[] arrFile){

        int count = 0;

        for (File file:arrFile) {

            if(!file.isDirectory())
                count++;
            else
                count += getFileCount(file.listFiles());
        }

        return count;
    }


    private void startCopyTo() {

        for (String path: arrPath) {

            File curFile = new File(path);

            if(!curFile.isDirectory()){
                copyFile(curFile, new File(copyTo, curFile.getName()));
                continue;
            }

            File rootDir = copyTo;

            copyDir(curFile, rootDir);
        }

        dispose();
    }

    private void copyDir(File curFile, File rootDir){

        //Создаем папку которую копируем
        File newDir = new File(rootDir, curFile.getName());

        if(!newDir.exists())
            newDir.mkdir();

//        Просматриваем файлы в директории откуда копируем
        for (File childFile:curFile.listFiles()) {

            if(childFile.isDirectory())
                copyDir(childFile, newDir);
            else {
                copyFile(childFile, new File(newDir, childFile.getName()));
            }
        }
    }

    private void setProgress(int value){
        copyProgressBar.setValue(value);
    }

    private void copyFile(File fileForCopy, File copyToFile){

        InputStream is = null;
        OutputStream os = null;

        try {

            is = new FileInputStream(fileForCopy);
            os = new FileOutputStream(copyToFile);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            setProgress(curValue++);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
