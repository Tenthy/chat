package lessons.lesson14;

import java.util.Arrays;

public class AfterTheLast4 {

    public long[] afterTheLast4(long[] longArray) {
        long[] resultArray;
        if (longArray.length == 1 && longArray[0] == 4) {
            throw new AfterTheLast4Exception();
        }
        for (int i = longArray.length - 1; i > 0; i--) {
            if (longArray[i] == 4) {
                resultArray = new long[longArray.length - i];
                resultArray = Arrays.copyOfRange(longArray, i + 1, longArray.length);
                return resultArray;
            }
        }
        throw new AfterTheLast4Exception();
    }

    public static class AfterTheLast4Exception extends RuntimeException {
        public AfterTheLast4Exception() {
            super("В массиве отсутствует цифра 4 или она там только одна");
        }
    }
}
