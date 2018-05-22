import GUIClasses.TableClasses.*;
import com.sun.jna.platform.FileUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
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
    private JScrollPane secondFileTableContainer;

    //Стек содержит путь по которому идет программа
    private Stack<DirInfo> firstFilePath;
    private Stack<DirInfo> secondFilePath;

    private KeyAdapter tableAdapter = null;
    private ActionListener comboboxActionListener = null;
    private MouseAdapter tableMouseAdapter;

    private int firstTablePosition, secondTablePosition;

    private final String FIRST_TABLE_NAME = "firstFileTable";
    private final String SECOND_TABLE_NAME = "secondFileTable";

    private final String FIRST_COMBOBOX_NAME = "firstDiskList";
    private final String SECOND_COMBOBOX_NAME = "secondDiskList";

    private String[] arrPathToCopy;

    private void refreshTableGUI(JTable table, int index, Stack<DirInfo> dirInfoStack, boolean createFile){

        table.setModel(new CustomTableModel(dirInfoStack.peek().getDirectory()));

        if(createFile)
            return;

        int curFocusIndex = 0;

        if(table.getRowCount() < index)
            curFocusIndex = table.getRowCount()-1;
        else
            curFocusIndex = index-1;

        table.setRowSelectionInterval(curFocusIndex, curFocusIndex);
    }

    private void initListeners(){

        tableAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                JTable table = (JTable) e.getSource();
                Stack<DirInfo> dirInfoStack;

                int index = table.getSelectedRow();

                setTablePositionByTableName(table.getName(), index);

                if(table.getName().equals(FIRST_TABLE_NAME)){
                    dirInfoStack = firstFilePath;
                    firstTablePosition = index;
                }
                else {
                    dirInfoStack = secondFilePath;
                    secondTablePosition = index;
                }

                if(index > -1 && e.getKeyCode() == KeyEvent.VK_ENTER){
                    onEnter(index, dirInfoStack, table);
                }
                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    onBackspace(dirInfoStack, table);
                }
                else if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_F10){
                    onDeleteFile(table);
                    refreshTableGUI(table, index, dirInfoStack, false);
                }
                else if(e.getKeyCode() == KeyEvent.VK_F12){
                    //Создаю директорию
                    onCreateFile(dirInfoStack.peek().getDirectory().getAbsolutePath(), true);
                    refreshTableGUI(table, index, dirInfoStack, true);
                }
                else if( e.getKeyCode() == KeyEvent.VK_F11){
//                    Создаю файл
                    onCreateFile(dirInfoStack.peek().getDirectory().getAbsolutePath(), false);
                    refreshTableGUI(table, index, dirInfoStack, true);
                }
                else if (e.getKeyCode() == KeyEvent.VK_TAB){
                    onTab(table);
                }
                else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C){
                    //Копирование сохранение данных выделенных файлов или папок

                    arrPathToCopy = getArrFilePath(table, table.getSelectedRows());
                }
                else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V){

                    // с помощью cmd
                    //                    try {
//                        Runtime.getRuntime().exec("cmd.exe /C xcopy F:\\111 F:\\1111\\111 /s /e");
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }

                    //с помощью java
                    new CoppyDialog(arrPathToCopy, dirInfoStack.peek().getDirectory());
                    //Вставка данных которые помечены для копирования
                }
            }
        };

        comboboxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox comboBox = (JComboBox) e.getSource();
                JTable table;
                Stack<DirInfo> dirInfoStack;

                if(comboBox.getName().equals(FIRST_COMBOBOX_NAME)){
                    table = firstFileTable;
                    dirInfoStack = firstFilePath;
                    firstTablePosition = 0;
                }
                else {
                    table = secondFileTable;
                    dirInfoStack = secondFilePath;
                    secondTablePosition = 0;
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

        tableMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                JTable table = (JTable) e.getSource();

                setTablePositionByTableName(table.getName(), table.getSelectedRow());

                if (table.getName().equals(FIRST_TABLE_NAME)){
                    secondFileTable.clearSelection();
                }
                else
                    firstFileTable.clearSelection();

                if(e.getClickCount() > 1){
                    boolean f = true;
                }

            }
        };

    }

    private void setTablePositionByTableName(String tableName, int index){

        if(tableName.equals(FIRST_TABLE_NAME))
            firstTablePosition = index;
        else
            secondTablePosition = index;
    }

    private void initComponentsSettings(){
        firstFileTable.setName(FIRST_TABLE_NAME);
        secondFileTable.setName(SECOND_TABLE_NAME);
        firstDiskList.setName(FIRST_COMBOBOX_NAME);
        secondDiscList.setName(SECOND_COMBOBOX_NAME);

        firstFileTable.getColumnModel().setSelectionModel(new CustomCollumnSelectionModel());
        secondFileTable.getColumnModel().setSelectionModel(new CustomCollumnSelectionModel());

        firstDiskList.addActionListener(comboboxActionListener);
        secondDiscList.addActionListener(comboboxActionListener);

        firstFileTable.addKeyListener(tableAdapter);
        secondFileTable.addKeyListener(tableAdapter);

        firstFileTable.addMouseListener(tableMouseAdapter);
        secondFileTable.addMouseListener(tableMouseAdapter);
    }

    private MainForm() {

        initListeners();

        initComponentsSettings();

        //Отображаю форму на панели
        setContentPane(mainFromPanel);
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void onCreateFile(String curDir, boolean isDir){
        new CreateFileDialog(curDir, isDir);
    }

    private void onDeleteFile(JTable table){

        table.getSelectedRow();

        new DeleteFileDialog(getArrFilePath(table, table.getSelectedRows()));
    }

    private void onTab(JTable table){

        if(table.getName().equals(FIRST_TABLE_NAME)){
            secondFileTable.grabFocus();
            table.clearSelection();
            secondFileTable.setRowSelectionInterval(secondTablePosition, secondTablePosition);
        }
        else {
            firstFileTable.grabFocus();
            table.getSelectionModel().clearSelection();
            firstFileTable.setRowSelectionInterval(firstTablePosition, firstTablePosition);
        }
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

        setTablePositionByTableName(table.getName(), index);
    }

    private void onEnter(int index, Stack<DirInfo> filePath, JTable table){
        FileInfo item = getFileInfoByIndexFromTableModel((CustomTableModel) table.getModel(), index);

        if(item.getType().equals(FileInfo.TYPE_DIR)){

            File curDir = new File(item.getFilePath());
            filePath.push(new DirInfo(curDir, false));
            table.setModel(new CustomTableModel(curDir));

            setTablePositionByTableName(table.getName(), 0);
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
