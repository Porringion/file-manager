package GUIClasses.TableClasses;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.ArrayList;

public class CustomTableModel implements TableModel {

    private ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
    private ArrayList<String> tableColumn = new ArrayList<String>();

    public CustomTableModel(File rootFile){
        generateTableColumns();

        for (File file:rootFile.listFiles()) {
            fileList.add(FileInfo.convertFromFile(file));
        }
    }

    private void generateTableColumns() {
        tableColumn.add("Тип");
        tableColumn.add("Имя");
        tableColumn.add("Размер");
    }

    public FileInfo getItemByRowIndex(int rowIndex){
        return fileList.get(rowIndex);
    }

    public int getRowCount() {
        return fileList.size();
    }

    public int getColumnCount() {
        return tableColumn.size();
    }

    public String getColumnName(int columnIndex) {
        return tableColumn.get(columnIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return tableColumn.get(columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {

            case 0:
                return fileList.get(rowIndex).getType();

            case 1:
                return fileList.get(rowIndex).getName();

            case 2:
                return fileList.get(rowIndex).getSize();

            default:
                return "";
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }
}
