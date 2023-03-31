import models.SearchHistory;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SelectDateFrame extends JFrame {
    public SelectDateFrame() throws HeadlessException {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(layout);

        JLabel fromLabel = new JLabel("From: ");
        JLabel toLabel = new JLabel("To: ");
        JDatePickerImpl datePicker1 = createDatePicker();
        JDatePickerImpl datePicker2 = createDatePicker();
        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> {

            if (datePicker1.getModel().getValue() == null || datePicker2.getModel().getValue() == null) {
                JOptionPane.showMessageDialog(this, "Please pick your date", "Warnings", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (((Date) datePicker1.getModel().getValue()).after((Date) datePicker2.getModel().getValue())) {
                JOptionPane.showMessageDialog(this, "Start date must be before end date", "Warnings", JOptionPane.WARNING_MESSAGE);
                return;
            }
            StatisticsPanel panel = new StatisticsPanel(new StatisticsPanel.ITableData() {
                @Override
                public String[] getTableColumns() {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    return Arrays.asList(
                                    "Word",
                                    "From " + formatter.format(datePicker1.getModel().getValue()) +
                                            "               " + "To " +
                                    formatter.format(datePicker2.getModel().getValue())
                            )
                            .toArray(new String[0]);
                }

                @Override
                public TreeMap<String, Integer> getCounter() {
                    try {
                        return SearchHistory.getInstance().getHistoryStatistics((Date) datePicker1.getModel().getValue(), (Date) datePicker2.getModel().getValue());
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            gbc.gridy = 1;
            gbc.gridx = 0;
            gbc.gridwidth = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridheight = 10;
            this.add(panel, gbc);
            SelectDateFrame.this.pack();
        });

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.add(fromLabel, gbc);
        gbc.gridx = 1;
        this.add(datePicker1, gbc);
        gbc.gridx = 2;
        this.add(toLabel, gbc);
        gbc.gridx = 3;
        this.add(datePicker2, gbc);
        gbc.gridx = 4;
        this.add(viewButton, gbc);
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        return new JDatePickerImpl(datePanel, new DateComponentFormatter());
    }

    private static class StatisticsPanel extends JPanel {

        interface ITableData {
            String[] getTableColumns();

            TreeMap<String, Integer> getCounter();
        }

        public StatisticsPanel(ITableData ITableData) throws HeadlessException {
            JTable jTable = new JTable();
            AbstractTableModel tableModel = new AbstractTableModel() {
                final String[] columns = ITableData.getTableColumns();
                private Vector dataVector;

                @Override
                public String getColumnName(int column) {
                    return columns[column];
                }

                @Override
                public @Nullable Object getValueAt(int rowIndex, int columnIndex) {
                    String word = (String) ITableData.getCounter().keySet().toArray()[rowIndex];
                    Integer count = ITableData.getCounter().get(word);
                    return switch (columnIndex) {
                        case 0 -> word;
                        case 1 -> count;
                        default -> null;
                    };
                }

                @Override
                public int getRowCount() {
                    return ITableData.getCounter().keySet().size();
                }

                @Override
                public int getColumnCount() {
                    return columns.length;
                }
            };

            jTable.setModel(tableModel);
            JScrollPane jScrollPane = new JScrollPane(jTable);
            this.add(jScrollPane);
        }
    }
}
