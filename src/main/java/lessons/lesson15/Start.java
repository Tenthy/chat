package lessons.lesson15;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class Start {

    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 1;

    public static void start (Class objClass) {
        if (!checkCountAnnotation(objClass)) {
            throw new RuntimeException();
        }

        //Так как данные в TreeMap хранятся в упорядочном виде и, имея приоритизацию тестов,
        //эта логика кажется мне наиболее удобной.
        Map<Integer, Method> mapMethods = new TreeMap<>();
        Method[] declaredMethods = objClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(BeforeSuite.class)) {
                mapMethods.put(MIN_PRIORITY - 1, declaredMethod);
            }
            if (declaredMethod.isAnnotationPresent(AfterSuite.class)) {
                mapMethods.put(MAX_PRIORITY + 1, declaredMethod);
            }
            if (declaredMethod.isAnnotationPresent(Test.class)) {
                Test priorityFromTest = declaredMethod.getAnnotation(Test.class);
                mapMethods.put(priorityFromTest.priority(), declaredMethod);
            }
        }

        try {
            TestedMethods testedMethods = new TestedMethods();
            for (Integer key : mapMethods.keySet()) {
                mapMethods.get(key).invoke(testedMethods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkCountAnnotation(Class objClass) {
        int countBeforeSuite = 0;
        int countAfterSuite = 0;
        Method[] declaredMethods = objClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(BeforeSuite.class)) {
                countBeforeSuite++;
            }
            if (declaredMethod.isAnnotationPresent(AfterSuite.class)) {
                countAfterSuite++;
            }
        }
        return (countBeforeSuite < 2) && (countAfterSuite < 2);
    }
}
