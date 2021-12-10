package lesson13;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainClass {

    public static final int CARS_COUNT = 4;
    public static CyclicBarrier preparingForRace = new CyclicBarrier(CARS_COUNT);
    public static CountDownLatch waitingAtTheFinish = new CountDownLatch(CARS_COUNT);
    public static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (Car car : cars) {
            new Thread(car).start();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        waitingAtTheFinish.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

    public static void whoIsWinner(String nameOfWinner) {
        if (lock.tryLock()) {
            System.out.println(nameOfWinner + " - WIN!");
            lock.lock();
        }
    }
}
