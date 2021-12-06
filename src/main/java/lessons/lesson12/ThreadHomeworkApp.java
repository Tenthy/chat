package lessons.lesson12;

public class ThreadHomeworkApp {

    public static void main(String[] args) {
        CalledObject calledObject = new CalledObject();
        Thread threadA = new Thread(calledObject::printA);
        Thread threadB = new Thread(calledObject::printB);
        Thread threadC = new Thread(calledObject::printC);
        threadA.start();
        threadB.start();
        threadC.start();
    }

    public static class CalledObject {
        private final Object lock = new Object();
        private volatile char currentLetter = 'A';

        public void printA() {
            synchronized (lock) {
                try {
                    for (int i = 0; i < 5; i++) {
                        while (currentLetter != 'A') {
                            lock.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter = 'B';
                        lock.notifyAll();
                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        public void printB() {
            synchronized (lock) {
                try {
                    for (int i = 0; i < 5; i++) {
                        while (currentLetter != 'B') {
                            lock.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter = 'C';
                        lock.notifyAll();
                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        public void printC() {
            synchronized (lock) {
                try {
                    for (int i = 0; i < 5; i++) {
                        while (currentLetter != 'C') {
                            lock.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter = 'A';
                        lock.notifyAll();
                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
