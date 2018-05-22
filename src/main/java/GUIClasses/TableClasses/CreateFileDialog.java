package GUIClasses.TableClasses;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class CreateFileDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField fileName;
    private JLabel dialogHeader;

    private String rootPath;
    private boolean isDir;

    public CreateFileDialog(String rootPath, boolean isDir) {

        this.rootPath = rootPath;
        this.isDir = isDir;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if(isDir)
            dialogHeader.setText("Введите папки:");
        else
            dialogHeader.setText("Введите название файла:");

        setVisible(true);
    }

    private void onOK() {
        // add your code here

        if(fileName.getText().isEmpty() || fileName.getText() == null)
            return;

        File newFile = new File(rootPath, fileName.getText());

        if(isDir)
            newFile.mkdir();
        else {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
