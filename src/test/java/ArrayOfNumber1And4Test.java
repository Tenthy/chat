import lessons.lesson14.ArrayOfNumber1And4;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArrayOfNumber1And4Test {

    ArrayOfNumber1And4 obj;

    @BeforeEach
    void init() {
        System.out.println("Initialization");
        obj = new ArrayOfNumber1And4();
    }

    @AfterEach
    void finish() {
        System.out.println("Test is finished");
    }

    @Test
    void testMethod() {
        Assertions.assertTrue(obj.arrayOfNumber1And4(new byte[]{1, 1, 1, 4}));
        Assertions.assertTrue(obj.arrayOfNumber1And4(new byte[]{4, 1, 4, 1}));
        Assertions.assertFalse(obj.arrayOfNumber1And4(new byte[]{1, 1, 1, 1}));
        Assertions.assertFalse(obj.arrayOfNumber1And4(new byte[]{4, 4, 4, 4}));
    }
}
