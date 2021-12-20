import lessons.lesson14.AfterTheLast4;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AfterTheLast4Test {

    AfterTheLast4 obj;

    @BeforeEach
    void init() {
        System.out.println("Initialization");
        obj = new AfterTheLast4();
    }

    @AfterEach
    void finish() {
        System.out.println("Test is finished");
    }

    @Test
    void testMethod() {
        Assertions.assertArrayEquals(new long[] {1, 7}, obj.afterTheLast4(new long[] {2, 4, 2, 4, 1, 7}));
        Assertions.assertArrayEquals(new long[] {3}, obj.afterTheLast4(new long[] {4, 3, 3, 4, 3}));
    }

    @Test
    void testWithException() {
        Assertions.assertThrows(AfterTheLast4.AfterTheLast4Exception.class, () -> obj.afterTheLast4(new long[] {1, 2, 3, 5}));
        Assertions.assertThrows(AfterTheLast4.AfterTheLast4Exception.class, () -> obj.afterTheLast4(new long[] {4}));
    }
}
