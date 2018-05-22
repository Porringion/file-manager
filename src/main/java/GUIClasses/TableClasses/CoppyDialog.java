package GUIClasses.TableClasses;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class CoppyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar coppyProgressBar;
    private File copyTo;
    private String[] arrPath;

    public CoppyDialog(String[] arrPath, File to) {

        this.copyTo = to;
        this.arrPath = arrPath;

        setContentPane(contentPane);
        setModal(true);
        pack();
//        getRootPane().setDefaultButton(buttonOK);
//
//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        startCopyTo();

        setVisible(true);
    }

//    private void onOK() {
//        // add your code here
//        dispose();
//    }

    private void startCopyTo() {

        for (String path: arrPath) {

            File curFile = new File(path);

            if(!curFile.isDirectory()){
                copyFile(curFile, copyTo);
                continue;
            }

            File rootDir = copyTo;

            copyDir(curFile, rootDir);
        }

    }

    private void copyDir(File curFile, File rootDir){

        File newDir = new File(rootDir, curFile.getName());


    }

    private void copyFile(File fileForCopy, File copyToFile){

        String copyDir = copyTo.getAbsolutePath();

        InputStream is = null;
        OutputStream os = null;

//        try {
//
//            is = new FileInputStream(source);
//            os = new FileOutputStream(new File(copyDir + "\\" + source.getName()));
//
//            byte[] buffer = new byte[1024];
//            int length;
//
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            is.close();
//            os.close();
//        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
