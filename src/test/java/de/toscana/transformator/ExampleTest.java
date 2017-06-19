package de.toscana.transformator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExampleTest {
    ExampleClass exampleClass;

    @Before
    public void setup() {
        exampleClass = new ExampleClass();
    }

    @Test
    public void isTrue(){
       boolean result = exampleClass.testClass("Hello World!");
       assertEquals(true,result);
    }
    @Test
    public void isFalse(){
        boolean result = exampleClass.testClass("Hello Wurld!");
        assertEquals(false,result);
    }
}
