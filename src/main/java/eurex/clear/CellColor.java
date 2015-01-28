package eurex.clear;

/**
 * Created by carvcal on 28.01.2015.
 */

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellColor  extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
    {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        CustomDataModel tableModel = (CustomDataModel) table.getModel();

        // l.setBackground(Color.white);
        // l.setForeground(Color.BLUE);

        if (tableModel.getStatus(row) == "High") {
            l.setBackground(Color.RED);
            l.setForeground(Color.white);
            return l;
        }

        if (tableModel.getStatus(row) == "Medium") {
            l.setBackground(Color.YELLOW);
            l.setForeground(Color.BLACK);
            return l;
        }

        if (tableModel.getStatus(row) == "Low") {
            l.setBackground(Color.DARK_GRAY);
            l.setForeground(Color.white);
            return l;
        }
        else
        {
           //l.setBackground(Color.GREEN);
            l.setBackground(Color.white);
            l.setForeground(Color.BLUE);
            return l;
        }
        //Return the JLabel which renders the cell.
        // return l;
    }
}


