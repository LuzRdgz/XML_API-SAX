package api.sax;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class APISAX extends JFrame {
    private JTable tabla;
    private DefaultTableModel tableModel;

    public APISAX() {
        setTitle("Visor de XML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        initComponents();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel();
        tabla = new JTable(tableModel);

        JButton cargar = new JButton("Cargar archivo XML");
        cargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    cargarArchivoXML(selectedFile);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cargar, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarArchivoXML(File file) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(file);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();

            
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            
            for (Element element : elements) {
                tableModel.addColumn(element.getName());
            }

            
            for (Element element : elements) {
                List<String> rowData = new java.util.ArrayList<>();
                for (Element child : element.getChildren()) {
                    rowData.add(child.getText());
                }
                tableModel.addRow(rowData.toArray());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo XML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            APISAX app = new APISAX();
            app.setVisible(true);
        });
    }
    
}
