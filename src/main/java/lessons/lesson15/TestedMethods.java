package lessons.lesson15;

public class TestedMethods {

    @BeforeSuite
    public void beforeSuiteMethod() {
        System.out.println("BeforeSuite Method");
    }

    @AfterSuite
    public void afterSuiteMethod() {
        System.out.println("AfterSuite Method");
    }

    @Test(priority = 3)
    public void method1() {
        System.out.println("Done! Test #1 Priority = 3");
    }

    @Test(priority = 1)
    public void method2() {
        System.out.println("Done! Test #2 Priority = 1");
    }

    @Test(priority = 5)
    public void method3() {
        System.out.println("Done! Test #3 Priority = 5");
    }

    @Test(priority = 4)
    public void method4() {
        System.out.println("Done! Test #4 Priority = 4");
    }

    @Test(priority = 2)
    public void method5()  {
        System.out.println("Done! Test #5 Priority = 2");
    }

    @Test(priority = 6)
    public void method6() {
        System.out.println("Done! Test #6 Priority = 6");
    }

    @Test(priority = 7)
    public void method7() {
        System.out.println("Done! Test #7 Priority = 7");
    }

    @Test(priority = 10)
    public void method8() {
        System.out.println("Done! Test #8 Priority = 10");
    }

    @Test(priority = 9)
    public void method9() {
        System.out.println("Done! Test #9 Priority = 9");
    }

    @Test(priority = 8)
    public void method10()  {
        System.out.println("Done! Test #10 Priority = 8");
    }
}
