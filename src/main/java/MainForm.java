import GUIClasses.TableClasses.CustomTableModel;
import GUIClasses.TableClasses.FileInfo;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;

public class MainForm extends JFrame {

    private JPanel mainFromPanel;
    private JComboBox firstDiskList;
    private JPanel firstListPanel;
    private JComboBox secondDiscList;
    private JPanel secondPanelList;
    private JTable secondFileTable;
    private JScrollPane firstFileTableContainer;
    private JTable firstFileTable;
    private int lastIndex = 0;
    private File curDir;

    private MainForm() {

        setContentPane(mainFromPanel);
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        firstDiskList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File fileDisk = (File) firstDiskList.getSelectedItem();

                if(fileDisk == null)
                    return;

                firstFileTable.setModel(new CustomTableModel(fileDisk));
            }
        });

        secondDiscList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File fileDisk = (File) secondDiscList.getSelectedItem();

                if(fileDisk == null)
                    return;

                secondFileTable.setModel(new CustomTableModel(fileDisk));
            }
        });
        firstFileTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                int index = firstFileTable.getSelectedRow();

                if(index < 0 && e.getKeyCode() != 8)
                    return;

                if(e.getKeyCode() == 10){

                    lastIndex = index;

                    FileInfo item = getFileInfoByIndexFromTableModel((CustomTableModel) firstFileTable.getModel(), index);

                    if(item.getType().equals(FileInfo.TYPE_DIR)) {

                        File selectedDir = new File(item.getFilePath());

                        if (selectedDir.exists())
                            curDir = selectedDir;
                            firstFileTable.setModel(new CustomTableModel(curDir));
                        }
                }

                else if(e.getKeyCode() == 8){

                    File parentFile = curDir.getParentFile();

                    if(parentFile.exists()) {

                        firstFileTable.setModel(new CustomTableModel(parentFile));

                        int rowCount = firstFileTable.getModel().getRowCount()-1;

                        if(rowCount > lastIndex)
                            firstFileTable.setRowSelectionInterval(lastIndex, lastIndex);
                        else
                            firstFileTable.setRowSelectionInterval(rowCount, rowCount);

                        curDir = parentFile;
                    }
                }
            }
        });
    }


    public static void main(String[] args) {
        MainForm mainForm = new MainForm();
    }

    private void createUIComponents() {
//        // TODO: place custom component creation code here

        File[] listRoots = File.listRoots();

        firstDiskList = new JComboBox(listRoots);
        secondDiscList = new JComboBox(listRoots);

        if(firstDiskList.getSelectedItem() != null){
            firstFileTable = new JTable(new CustomTableModel((File) firstDiskList.getSelectedItem()));
            firstFileTable.setVisible(true);
        }

        if(secondDiscList.getSelectedItem() != null){
            secondFileTable = new JTable(new CustomTableModel((File) secondDiscList.getSelectedItem()));
            secondFileTable.setVisible(true);
        }

    }

    private FileInfo getFileInfoByIndexFromTableModel(CustomTableModel tableModel, int rowIndex){
        return tableModel.getItemByRowIndex(rowIndex);
    }
}
