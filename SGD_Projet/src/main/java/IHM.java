/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.awt.Point;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author vb394093
 */
public class IHM extends javax.swing.JFrame {

    /**
     * Creates new form IHM
     */
   private MongoCollection<Document> d;
    public boolean bool=false;
    public boolean bb=false;
    public final ArrayList<String> cols;
    public ArrayList<Document> docs;
    public IHM() {
        cols = new ArrayList<>(Arrays.asList("titre", "date", "editeur", "prix", "serie", "extensions", "pegi", "nb_joueurs", "plateformes", "type"));
        docs = new ArrayList<Document>();
        initComponents();
        
        char [] pass = new char[10];
        String s="vb394093"; pass = s.toCharArray();
        MongoCredential credential = MongoCredential.createCredential("vb394093", "vb394093", pass);
        //MongoClient client = new MongoClient(new ServerAddress("mongo", 27017), Arrays.asList(credential));
        MongoClient client = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));
        MongoDatabase db = client.getDatabase("vb394093");
        d = db.getCollection("Sgd_jeu");
        FindIterable fi = d.find(Filters.eq("genre", "jeu"));
        MongoCursor mc = fi.iterator();
        recherche();
        setTable((int)d.count(Filters.eq("genre", "jeu")), 0);
        /*jTableAffichage.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                TableModel model = (TableModel)e.getSource();
                Object data = model.getValueAt(row, column);
                System.out.println(data);
             }
        });*/
    }
    public int recherche()
    {
        docs.clear();
        Bson frech = Aggregates.match(Filters.regex((String)rechercheComboBox.getSelectedItem(), jTextField1.getText(), "i"));
        
        Bson fgenre = Aggregates.match(Filters.eq("genre", (String)genreComboBox.getSelectedItem()));
       
        Bson fplats = Aggregates.match(Filters.in("plateformes",
                XBoxCheckBox.isSelected()? "Xbox" : "null",
                PsCheckBox.isSelected()? "PlayStation" : "null",
                PcCheckBox.isSelected()? "Pc" : "null",
                NintendoCheckBox.isSelected()? "Nintendo" : "null"));
        
        Bson fprix = Aggregates.match(Filters.lt("prix", jSlider1.getValue()));
        
        Bson fnbJ = Aggregates.match(nbJoueurComboBox.getSelectedIndex() != 0 ?
                Filters.eq("nb_joueurs", (String)nbJoueurComboBox.getSelectedItem()) :
                Filters.regex("nb_joueurs", ""));
        
        AggregateIterable iter;
        if (((String)genreComboBox.getSelectedItem()).equals("jeu"))
            iter = d.aggregate(Arrays.asList(fgenre, frech, fplats, fprix, fnbJ));
        else
            iter = d.aggregate(Arrays.asList(fgenre, frech));
        MongoCursor mc = iter.iterator();
        int n = 0;
        while(mc.hasNext())
        {
            docs.add((Document)mc.next());
            System.out.println(docs.get(n));
            n++;
        }
        return n;
    }
    public void setTable(int n, int row)
    {
        DefaultTableModel dm = (DefaultTableModel)jTableAffichage.getModel();
        dm.setRowCount(n);
        jTableAffichage.setModel(dm);
        
        int i = 0;
        for (Document doc : docs)
        {
            int j = 0;
            for (String col : cols)
            {
                jTableAffichage.setValueAt(doc.get(col), i, j);
                j++;
            }
            i++;
        }
        if (!docs.isEmpty())
        {
            Document doc = docs.get(row);
            String desc = doc.get("description", String.class);
            ArrayList listcomm = doc.get("commentaires", ArrayList.class);
            
            if (desc != null)
                descriptionTextArea.setText(desc);
            else
                descriptionTextArea.setText("");
            if (listcomm != null)
            {
                commentaireList.setListData((String[])listcomm.toArray(new String[0]));
                ArrayList note = docs.get(row).get("notes", ArrayList.class);    
                double s = 0;
                for (Object o : note)
                    s = s + Double.valueOf(o + "");
                jLabel4.setText("Note : " + new DecimalFormat("##.##").format((s / note.size())));
            }
            else
            {
                commentaireList.setListData("".split(""));
                jLabel4.setText("");
            }
        }
    }
    public int displayed()
    {
        int s = jTableAffichage.getSelectedRow();
        return s > 0? s : 0;
    }
    public Object[][] getObjects()
    {
        Object[][] obs = new Object[docs.size()][];
        int i = 0;
        for (Document doc : docs)
        {
            obs[i] = new Object[doc.size()];
            int j = 0;
            for (String col : cols)
            {
                obs[i][j] = doc.get(col);
                j++;
            }
            i++;
        }
        return obs;
    }
    public boolean ValidRow(int i)
    {
        ArrayList<Integer> l = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 7, 8));
        for (int j = 0; j < jTableAffichage.getColumnCount(); j++)
        {
            System.out.println(i + " " + j);
            if (jTableAffichage.getValueAt(i, l.get(i)) == null)
               return false;
        }
        return true;
    }
    public boolean ValidRows()
    {
        
        for(int i = 0; i < jTableAffichage.getRowCount(); i++)
           if (!ValidRow(i))
               return false; 
        return true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        rechercheButton = new javax.swing.JButton();
        rechercheComboBox = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        commentaireTextArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAffichage = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        commentaireList = new javax.swing.JList<>();
        nbJoueurComboBox = new javax.swing.JComboBox<>();
        XBoxCheckBox = new javax.swing.JCheckBox();
        PsCheckBox = new javax.swing.JCheckBox();
        NintendoCheckBox = new javax.swing.JCheckBox();
        PcCheckBox = new javax.swing.JCheckBox();
        jSlider1 = new javax.swing.JSlider();
        genreComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        LabelPrix = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSlider2 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton3.setVisible(false);
        jButton4 = new javax.swing.JButton();
        jButton4.setVisible(false);
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mediatheque");

        jTextField1.setToolTipText("");

        rechercheButton.setText("Go");
        rechercheButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercheButtonActionPerformed(evt);
            }
        });

        rechercheComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "titre", "editeur", "type" }));

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(descriptionTextArea);

        commentaireTextArea.setColumns(20);
        commentaireTextArea.setRows(5);
        commentaireTextArea.setToolTipText("");
        commentaireTextArea.setAutoscrolls(false);
        jScrollPane3.setViewportView(commentaireTextArea);

        jTableAffichage.setAutoCreateRowSorter(true);
        jTableAffichage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Titre", "Date", "Ëditeur", "Prix", "Serie", "Extensions", "Pegi", "Nb_joueurs", "Plateformes", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableAffichage.setAutoscrolls(false);
        jTableAffichage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableAffichageMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableAffichage);
        if (jTableAffichage.getColumnModel().getColumnCount() > 0) {
            jTableAffichage.getColumnModel().getColumn(0).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(1).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(2).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(3).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(4).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(5).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(6).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(7).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(8).setResizable(false);
            jTableAffichage.getColumnModel().getColumn(9).setResizable(false);
        }

        jScrollPane4.setViewportView(commentaireList);

        nbJoueurComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tous", "1j", "1j à 2j", "1j à 3j", "1j à 4j" }));

        XBoxCheckBox.setSelected(true);
        XBoxCheckBox.setText("XBox");

        PsCheckBox.setSelected(true);
        PsCheckBox.setText("PlayStation");

        NintendoCheckBox.setSelected(true);
        NintendoCheckBox.setText("Nintendo");

        PcCheckBox.setSelected(true);
        PcCheckBox.setText("Pc");

        jSlider1.setMaximum(800);
        jSlider1.setValue(40);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        genreComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "jeu", "serie" }));

        jLabel1.setText("Prix : ");

        LabelPrix.setText("40€");

        jButton1.setText("Administrateur");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("commenter");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jSlider2.setMaximum(5);
        jSlider2.setValue(4);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jLabel2.setText("Votre note :");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(jSlider2.getValue() + "");

        jLabel4.setText("Note :");

        jButton3.setText("+");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("-");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("ok");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.setVisible(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(genreComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rechercheButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rechercheComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nbJoueurComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(XBoxCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PsCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(NintendoCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PcCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addGap(2, 2, 2)
                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LabelPrix)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addComponent(jButton1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(rechercheButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(genreComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rechercheComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nbJoueurComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(XBoxCheckBox)
                            .addComponent(PsCheckBox)
                            .addComponent(NintendoCheckBox)
                            .addComponent(PcCheckBox)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(LabelPrix)
                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton3)
                                    .addComponent(jButton4))
                                .addGap(10, 10, 10)))))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton5)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rechercheButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheButtonActionPerformed
        int n = recherche();
        setTable(n, displayed());
    }//GEN-LAST:event_rechercheButtonActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        LabelPrix.setText(jSlider1.getValue()+"€");
    }//GEN-LAST:event_jSlider1StateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    jButton3.setVisible(!bool);
    jButton4.setVisible(!bool);
    descriptionTextArea.setEditable(!bool);
    jTableAffichage.setModel(new javax.swing.table.DefaultTableModel(getObjects(), new String [] {"Titre", "Date", "Editeur", "Prix", "Serie", "Extensions", "Pegi", "Nb Joueurs", "Plateformes", "Types"}) {
    @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return bool; }
    });
    rechercheButton.setEnabled(bool);
    if (!bool)
    {
    jTableAffichage.getModel().addTableModelListener(new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (!bb)
            {
                String s = jTableAffichage.getValueAt(e.getFirstRow(), e.getColumn()).toString();
                
                if (e.getFirstRow() < docs.size())
                {   
                    System.out.println(jTableAffichage.getValueAt(e.getFirstRow(), 0));
                    if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']')
                    {
                        List l = Arrays.asList(s.replaceAll("\\[", "").replaceAll("\\]", "").split(", "));
                        d.updateOne(Filters.eq("titre", docs.get(displayed()).getString("titre")), Updates.set(cols.get(e.getColumn()), l));
                    }
                    else if(e.getColumn() == 3)
                        d.updateOne(Filters.eq("titre", docs.get(displayed()).getString("titre")), Updates.set(cols.get(e.getColumn()), (Double)jTableAffichage.getValueAt(e.getFirstRow(), e.getColumn())));
                    else
                        d.updateOne(Filters.eq("titre", docs.get(displayed()).getString("titre")), Updates.set(cols.get(e.getColumn()), jTableAffichage.getValueAt(e.getFirstRow(), e.getColumn())));
                }
                else
                {
                    if (ValidRow(e.getFirstRow()))
                    {
                        System.out.println("valid");
                        jButton5.setVisible(true);
                    }
                    else
                        System.out.println("non valid");
                }
            }
        }
    });
    }
    if (!bool)
            jButton1.setText("Déconnexion");
    else
     {
         int n = recherche();
         setTable(n, displayed());
         jButton1.setText("Administrateur");
     }
     bool = !bool;
    
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTableAffichageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAffichageMouseClicked
        Document doc = docs.get(jTableAffichage.rowAtPoint(evt.getPoint()));
        String desc = doc.get("description", String.class);
        ArrayList listcomm = doc.get("commentaires", ArrayList.class);
        
        if (jTableAffichage.rowAtPoint(evt.getPoint()) < docs.size() && desc != null && listcomm != null)
        {
            descriptionTextArea.setText(desc);
            commentaireList.setListData((String[])listcomm.toArray(new String[0]));
            ArrayList note = docs.get(jTableAffichage.rowAtPoint(evt.getPoint())).get("notes", ArrayList.class);
            double s = 0;
            for (Object n : note)
                s = s + Double.valueOf(n + "");
            jLabel4.setText("Note : " + new DecimalFormat("##.##").format((s / note.size())));
        }
        else
        {
            descriptionTextArea.setText("");
            commentaireList.setListData("".split(""));
            jLabel4.setText("");
        }
    }//GEN-LAST:event_jTableAffichageMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (!commentaireTextArea.getText().isEmpty())
        {
            d.updateOne(Filters.eq("titre", docs.get(displayed()).getString("titre")), Updates.push("commentaires", commentaireTextArea.getText()));
            d.updateOne(Filters.eq("titre", docs.get(displayed()).getString("titre")), Updates.push("notes", jSlider2.getValue()));
            recherche();
            commentaireList.setListData((String[])docs.get(displayed()).get("commentaires", ArrayList.class).toArray(new String[0]));
            ArrayList note = docs.get(displayed()).get("notes", ArrayList.class);
            double s = 0;
            for (Object n : note)
                s = s + Double.valueOf(n + "");
            jLabel4.setText("Note : " + new DecimalFormat("##.##").format((s / note.size())));
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        jLabel3.setText(jSlider2.getValue() + "");
    }//GEN-LAST:event_jSlider2StateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        bb = true;
        DefaultTableModel dm = (DefaultTableModel)jTableAffichage.getModel();
        Document doc = docs.get(displayed());
        dm.removeRow(jTableAffichage.getSelectedRow());
        jTableAffichage.setModel(dm);
        d.deleteOne(Filters.eq("titre", doc.getString("titre")));
        bb = false;
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    Document doc = new Document();
    String s; 

    for (int i = 0; i < jTableAffichage.getColumnCount(); i++)
    {
       if (!(jTableAffichage.getValueAt(jTableAffichage.getRowCount() - 1, i) == null))
       {
           s = jTableAffichage.getValueAt(jTableAffichage.getRowCount() - 1, i).toString();
           if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']')
           {
                List l = Arrays.asList(s.replaceAll("\\[", "").replaceAll("\\]", "").split(", "));
                doc.append(cols.get(i), l);
           }
           else
               doc.append(cols.get(i), jTableAffichage.getValueAt(jTableAffichage.getRowCount() - 1, i));
       }
    }
    doc.append("description", descriptionTextArea.getText());
    doc.append("genre", genreComboBox.getSelectedItem().toString());
    d.insertOne(doc);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (ValidRows())
        {
            bb = true;
            int n = recherche();
            setTable(n + 1, displayed());
            bb = false;
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IHM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new IHM().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelPrix;
    private javax.swing.JCheckBox NintendoCheckBox;
    private javax.swing.JCheckBox PcCheckBox;
    private javax.swing.JCheckBox PsCheckBox;
    private javax.swing.JCheckBox XBoxCheckBox;
    public javax.swing.JList<String> commentaireList;
    public javax.swing.JTextArea commentaireTextArea;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JComboBox<String> genreComboBox;
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton2;
    public javax.swing.JButton jButton3;
    public javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    public javax.swing.JTable jTableAffichage;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox<String> nbJoueurComboBox;
    public javax.swing.JButton rechercheButton;
    private javax.swing.JComboBox<String> rechercheComboBox;
    // End of variables declaration//GEN-END:variables
}
