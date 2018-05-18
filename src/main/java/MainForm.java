import GUIClasses.TableClasses.CustomTableModel;
import GUIClasses.TableClasses.DirInfo;
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

    //Стек содержит путь по которому идет программа
    private Stack<DirInfo> firstFilePath;
    private Stack<DirInfo> secondFilePath;


    private MainForm() {

        //Отображаю форму на панели
        setContentPane(mainFromPanel);
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        //Обработка выбора диска в чек боксе
        firstDiskList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File fileDisk = (File) firstDiskList.getSelectedItem();

                if(fileDisk == null || !fileDisk.exists())
                    return;

                //Помещаем файл диска в стэк
                firstFilePath.clear();
                firstFilePath.push(new DirInfo(fileDisk, true));
                firstFileTable.setModel(new CustomTableModel(fileDisk));
            }
        });

        secondDiscList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File fileDisk = (File) secondDiscList.getSelectedItem();

                if(fileDisk == null)
                    return;

                secondFilePath.clear();
                secondFilePath.push(new DirInfo(fileDisk, true));
                secondFileTable.setModel(new CustomTableModel(fileDisk));
            }
        });

        firstFileTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                int index = firstFileTable.getSelectedRow();

                if(index > -1 && e.getKeyCode() == 10){
                    onEnter(index, firstFilePath, firstFileTable);
                }
                else if(e.getKeyCode() == 8){
                    onBackspace(firstFilePath, firstFileTable);
                }

            }
        });

    }

    private void onBackspace(Stack<DirInfo> filePath, JTable table){
        DirInfo lastDir = filePath.peek();

        if(lastDir.getIsRoot())
            return;

        filePath.pop();

        DirInfo curDir = filePath.peek();

        CustomTableModel tableModel = new CustomTableModel(curDir.getDirectory());

        table.setModel(tableModel);

        int index = tableModel.getRowIndexByItem(lastDir.getFileInfoDirectory());

        table.setRowSelectionInterval(index, index);

    }

    private void onEnter(int index, Stack<DirInfo> filePath, JTable table){
        FileInfo item = getFileInfoByIndexFromTableModel((CustomTableModel) table.getModel(), index);

        if(item.getType().equals(FileInfo.TYPE_DIR)){

            File curDir = new File(item.getFilePath());
            filePath.push(new DirInfo(curDir, false));
            table.setModel(new CustomTableModel(curDir));
        }
    }


    public static void main(String[] args) {
        MainForm mainForm = new MainForm();
    }

    private void createUIComponents() {
//        // TODO: place custom component creation code here

        firstFilePath = new Stack<DirInfo>();
        secondFilePath = new Stack<DirInfo>();

        File[] rootList = File.listRoots();

        firstDiskList = new JComboBox(rootList);
        secondDiscList = new JComboBox(rootList);

        File curDisk;

        if(firstDiskList.getSelectedItem() != null){

            curDisk = (File) firstDiskList.getSelectedItem();

            firstFileTable = new JTable(new CustomTableModel(curDisk));
            firstFileTable.setVisible(true);

            firstFilePath.push(new DirInfo(curDisk, true));
        }

        if(secondDiscList.getSelectedItem() != null){

            curDisk = (File) secondDiscList.getSelectedItem();

            secondFileTable = new JTable(new CustomTableModel(curDisk));
            secondFileTable.setVisible(true);

            secondFilePath.push(new DirInfo(curDisk, true));
        }

    }

    private FileInfo getFileInfoByIndexFromTableModel(CustomTableModel tableModel, int rowIndex){
        return tableModel.getItemByRowIndex(rowIndex);
    }
}
