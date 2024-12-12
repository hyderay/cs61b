package flik;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {

    @Test
    public void isSameNumberTest() {
        Flik test = new Flik();
        assertTrue(test.isSameNumber(10, 10));
        assertFalse(test.isSameNumber(5, 10));
    }

    @Test
    public void largeAmountTest() {
        Flik test = new Flik();
        int j = 0;

        for (int i = 0; i < 500; i++) {
            assertTrue(String.valueOf(i) + " should be equal to " + String.valueOf(j), test.isSameNumber(i, j));
            j++;
        }
    }
}
