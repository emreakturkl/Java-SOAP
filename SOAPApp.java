import java.io.*;
import java.awt.BorderLayout;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class SOAPApp extends JFrame {

    String endpoint = "http://81.214.73.178/TahsilatService/TahsilatService.asmx";
    String namespace = "http://tempuri.org/";
    String parameter = "referansNo";
    String operation;
    String value;

    private JButton buton;
    private JTextField text;
    private DefaultTableModel model;
    private JTable table;
    private JLabel label;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JPanel panel;
    private JPanel gridNorth;


    public SOAPApp() {

        panel = new JPanel(new BorderLayout());
        gridNorth = new JPanel(new GridLayout(1, 3));

        buton = new JButton("GONDER");
        text = new JTextField(20);
        label = new JLabel();
        label.setForeground(Color.BLUE);

        ButtonGroup group = new ButtonGroup();
        checkBox1 = new JCheckBox("BorcSorgu");
        checkBox1.setSelected(false);
        checkBox2 = new JCheckBox("TahsilatSorgu");
        checkBox2.setSelected(false);

        model = new DefaultTableModel();
        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.yellow);


        FieldHandler handle = new FieldHandler();
        CheckHandler handle1 = new CheckHandler();

        buton.addActionListener(handle);
        checkBox1.addItemListener(handle1);
        checkBox2.addItemListener(handle1);

        group.add(checkBox1);
        group.add(checkBox2);
        gridNorth.add(checkBox1);
        gridNorth.add(checkBox2);
        gridNorth.add(text);
        gridNorth.add(buton);
        panel.add(gridNorth, BorderLayout.NORTH);
        panel.add(label, BorderLayout.SOUTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setTitle("TahsilatService");
        setVisible(true);

    }

    private class CheckHandler implements ItemListener {
        public void itemStateChanged(ItemEvent event) {

            if (event.getItemSelectable() == checkBox1) {
                operation = "BorcSorgu";

            } else if (event.getItemSelectable() == checkBox2) {
                operation = "TahsilatSorgu";

            }
        }
    }

    private class FieldHandler implements ActionListener {

        //String value = "58302861634";
        //String value = "30295872165";

        public void actionPerformed(ActionEvent event) {

            if (model.getRowCount() > 0)
                while (model.getRowCount() > 0)
                    model.removeRow(0);

            if(model.getColumnCount()>0)
                for(int i=model.getColumnCount()-1; i>=0;i--)
                    removeColumn(i,table);

            if (event.getSource() == buton) {

                try {
                    value = text.getText();

                    if (operation == null || text.getText().isEmpty())
                    {
                        JOptionPane.showMessageDialog(panel, "Geçersiz Değer","HATA",JOptionPane.WARNING_MESSAGE);

                    }
                    else {

                        if (operation == "BorcSorgu") {

                            model.addColumn("BorcReferansNo");
                            model.addColumn("GelirID");
                            model.addColumn("BorcTur");
                            model.addColumn("DonemTaksit");
                            model.addColumn("SonOdemeTarih");
                            model.addColumn("Tutar");
                            model.addColumn("Gecikme");
                            model.addColumn("Toplam");


                            SOAPClient client = new SOAPClient(endpoint, namespace, operation, parameter, value);
                            String str = client.callSoapWebService();

                            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                            Document doc = db.parse(new InputSource(new StringReader(str)));


                            String isim = doc.getElementsByTagName("AdSoyad").item(0).getTextContent();
                            NodeList nList = doc.getElementsByTagName("BorcDetay");
                            for (int temp = 0; temp < nList.getLength(); temp++) {
                                Node nNode = nList.item(temp);
                                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElement = (Element) nNode;

                                    model.addRow(new Object[]
                                            {
                                                    eElement.getElementsByTagName("BorcReferansNo").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("GelirID").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("BorcTur").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("DonemTaksit").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("SonOdemeTarih").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("Tutar").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("Gecikme").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("Toplam").item(0).getTextContent(),
                                            });

                                }

                            }

                            model.removeRow(0);
                            double tutar = 0;
                            double gecikme = 0;
                            double toplam = 0;

                            for (int i = 0; i < model.getRowCount(); i++) {
                                tutar += Double.parseDouble((String) model.getValueAt(i, 5));
                                gecikme += Double.parseDouble((String) model.getValueAt(i, 6));
                                toplam += Double.parseDouble((String) model.getValueAt(i, 7));
                            }
                            label.setText(String.format("%20s\t %110.3f %s\t %16.3f %s\t %16.3f %s", isim, tutar,"TL", gecikme,"TL", toplam,"TL"));

                        } else if (operation == "TahsilatSorgu") {

                            model.addColumn("TahsilatReferansNo");
                            model.addColumn("BorcTur");
                            model.addColumn("IslemTarih");
                            model.addColumn("BankaReferansNo");
                            model.addColumn("Tutar");

                            SOAPClient client = new SOAPClient(endpoint, namespace, operation, parameter, value);
                            String str = client.callSoapWebService();

                            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                            Document doc = db.parse(new InputSource(new StringReader(str)));


                            String isim = doc.getElementsByTagName("AdSoyad").item(0).getTextContent();
                            NodeList nList = doc.getElementsByTagName("TahsilatDetay");
                            for (int temp = 0; temp < nList.getLength(); temp++) {
                                Node nNode = nList.item(temp);
                                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElement = (Element) nNode;

                                    model.addRow(new Object[]
                                            {
                                                    eElement.getElementsByTagName("TahsilatReferansNo").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("BorcTur").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("IslemTarih").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("BankaReferansNo").item(0).getTextContent(),
                                                    eElement.getElementsByTagName("Tutar").item(0).getTextContent(),

                                            });

                                }

                            }
                            model.removeRow(0);
                            double tutar = 0;

                            for (int i = 0; i < model.getRowCount(); i++) {
                                tutar += Double.parseDouble((String) model.getValueAt(i, 4));
                            }
                            label.setText(String.format("%20s\t %150.3f %s", isim, tutar,"TL"));

                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

        }
    }
    private void removeColumn(int index, JTable myTable){
        int nRow= myTable.getRowCount();
        int nCol= myTable.getColumnCount()-1;
        Object[][] cells= new Object[nRow][nCol];
        String[] names= new String[nCol];

        for(int j=0; j<nCol; j++){
            if(j<index){
                names[j]= myTable.getColumnName(j);
                for(int i=0; i<nRow; i++){
                    cells[i][j]= myTable.getValueAt(i, j);
                }
            }else{
                names[j]= myTable.getColumnName(j+1);
                for(int i=0; i<nRow; i++){
                    cells[i][j]= myTable.getValueAt(i, j+1);
                }
            }
        }

        //DefaultTableModel newModel= new DefaultTableModel(cells, names);
        model.setDataVector(cells,names);
        myTable.setModel(model);
    }
}
