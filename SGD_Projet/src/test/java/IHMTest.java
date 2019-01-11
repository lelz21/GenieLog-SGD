
import java.awt.AWTEvent;
import java.awt.Event;
import org.junit.Before;
import static org.junit.Assert.*;
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
    @Before
    public void setUp() {
        vue = new IHM();
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
}
