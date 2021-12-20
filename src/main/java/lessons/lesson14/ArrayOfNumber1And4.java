package lessons.lesson14;

public class ArrayOfNumber1And4 {

    public boolean arrayOfNumber1And4(byte[] byteArray) {
        boolean exist1 = false;
        boolean exist4 = false;
        for (byte value : byteArray) {
            if (value == 1) {
                exist1 = true;
            }
            if (value == 4) {
                exist4 = true;
            }
        }
        return exist1 && exist4;
    }
}
