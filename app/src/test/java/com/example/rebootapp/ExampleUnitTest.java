package com.example.rebootapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public void loginTest() {
        Login test = new Login();
        boolean result = test.logInManual("default@gmail.com", "12345");
        assertEquals(true, result);
    }

    class EmailValidatorTest {
        @Test
        public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
            boolean result = SignUp.isValidEmail("name@email.com");
            assertTrue(result);
        }
    }
}