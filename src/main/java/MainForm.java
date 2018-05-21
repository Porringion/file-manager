import GUIClasses.TableClasses.ActionsDialog;
import GUIClasses.TableClasses.CustomTableModel;
import GUIClasses.TableClasses.DirInfo;
import GUIClasses.TableClasses.FileInfo;
import com.sun.jna.platform.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
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

    private Desktop desktop;

    //Стек содержит путь по которому идет программа
    private Stack<DirInfo> firstFilePath;
    private Stack<DirInfo> secondFilePath;

    private KeyAdapter tableAdapter = null;
    private ActionListener comboboxActionListener = null;



    private void initListeners(){

        tableAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                JTable table = (JTable) e.getSource();
                Stack<DirInfo> dirInfoStack;

                if(table.getName().equals("firstFileTable"))
                    dirInfoStack = firstFilePath;
                else
                    dirInfoStack = secondFilePath;

                int index = table.getSelectedRow();


                if(index > -1 && e.getKeyCode() == 10){
                    onEnter(index, dirInfoStack, table);
                }
                else if(e.getKeyCode() == 8){
                    onBackspace(dirInfoStack, table);
                }
                else if(e.getKeyCode() == 127 || e.getKeyCode() == 119){
                    onDeleteFile(table);
                }
            }
        };

        comboboxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox comboBox = (JComboBox) e.getSource();
                JTable table;
                Stack<DirInfo> dirInfoStack;

                if(comboBox.getName().equals("firstDiskList")){
                    table = firstFileTable;
                    dirInfoStack = firstFilePath;
                }
                else {
                    table = secondFileTable;
                    dirInfoStack = secondFilePath;
                }

                File fileDisk = (File) comboBox.getSelectedItem();

                if(fileDisk == null || !fileDisk.exists())
                    return;

                //Помещаем файл диска в стэк
                dirInfoStack.clear();
                dirInfoStack.push(new DirInfo(fileDisk, true));
                table.setModel(new CustomTableModel(fileDisk));
            }
        };

    }

    private MainForm() {

        firstFileTable.setName("firstFileTable");
        secondFileTable.setName("secondFileTable");
        firstDiskList.setName("firstDiskList");
        secondDiscList.setName("secondDiskList");

        desktop = Desktop.getDesktop();

        //Отображаю форму на панели
        setContentPane(mainFromPanel);
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initListeners();

        firstDiskList.addActionListener(comboboxActionListener);
        secondDiscList.addActionListener(comboboxActionListener);

        firstFileTable.addKeyListener(tableAdapter);
        secondFileTable.addKeyListener(tableAdapter);

    }

    private void onDeleteFile(JTable table){

        table.getSelectedRow();

        new ActionsDialog(getArrFilePath(table, table.getSelectedRows()));

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
        else if(item.getType().equals(FileInfo.TYPE_FILE)){

            try {
                Desktop.getDesktop().open(new File(item.getFilePath()));
            } catch (IOException e) {
                //Обработка не могу открыть файл
                e.printStackTrace();
            }

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

    private String[] getArrFilePath(JTable table, int[] arrIndex){

        CustomTableModel tableModel = (CustomTableModel) table.getModel();

        String[] arrPath = new String[arrIndex.length];
        int counter = 0;

        for (int index:arrIndex) {

            arrPath[counter] = getFileInfoByIndexFromTableModel(tableModel, index).getFilePath();
            counter++;
        }

        return arrPath;
    }

    private FileInfo getFileInfoByIndexFromTableModel(CustomTableModel tableModel, int rowIndex){
        return tableModel.getItemByRowIndex(rowIndex);
    }
}
