
import java.awt.AWTEvent;
import java.awt.Event;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ab750519
 */
public class IHMTest {
    static IHM vue;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    @Before
    public void setUp() {
        vue = new IHM();
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testClicSurAdmin(){
        boolean prevBool= vue.bool;
        boolean prevVisibleJ3=vue.jButton3.isVisible();
        boolean prevVisibleJ4=vue.jButton4.isVisible();
        boolean prevEnableGo=vue.rechercheButton.isEnabled();
        
        vue.jButton1.doClick();
        
        assertEquals(prevBool,!vue.bool);
        assertEquals(prevVisibleJ3,!vue.jButton3.isVisible());
        assertEquals(prevVisibleJ4,!vue.jButton4.isVisible());
        assertEquals(prevEnableGo,!vue.rechercheButton.isEnabled());
    }
    @Test
    public void testGoClic()
    {
        vue.rechercheButton.doClick();
        int n = vue.docs.size();
        assertEquals(vue.jTableAffichage.getModel().getRowCount(), n);
    }
    @Test
    public void testPlusClic()
    {
        
        int n = vue.jTableAffichage.getModel().getRowCount();
        vue.jButton3.doClick();
        assertEquals(vue.jTableAffichage.getModel().getRowCount(), n + 1);
    }
    @Test
    public void testMoinsClic()
    {
        int n = vue.jTableAffichage.getModel().getRowCount();
        vue.jTableAffichage.changeSelection(0, 0, false, false);
        vue.jButton4.doClick();
        assertEquals(vue.jTableAffichage.getModel().getRowCount(), n - 1);
    }
    @Test
    public void testComment()
    {
        vue.commentaireTextArea.setText("Test");
        int n = vue.commentaireList.getModel().getSize();
        vue.jTableAffichage.changeSelection(0, 0, false, false);
        
        vue.jButton2.doClick();
        assertEquals(vue.commentaireList.getModel().getSize(), n + 1);
    }
    @Test
    public void testSlider()
    {
        vue.jSlider2.setValue(5);
        String s = vue.jLabel3.getText();
        int val = Integer.parseInt(s);
        assertEquals(5, val);
    }
    @Test
    public void testJTable()
    {
        vue.jTableAffichage.changeSelection(0, 0, false, false);
        String expected = vue.docs.get(0).getString("titre");
        int x = vue.jTableAffichage.getSelectedRow();
        int y = vue.jTableAffichage.getSelectedColumn();
        String actual = (String)vue.jTableAffichage.getModel().getValueAt(x, y);
        System.out.println(expected + " " + actual);
        assertEquals(expected, actual);
    }
}
