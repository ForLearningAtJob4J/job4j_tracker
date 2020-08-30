package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ValidateInputTest {
    // поле содержит дефолтный вывод в консоль.
    private final PrintStream stdout = System.out;
    // буфер для результата.
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void loadOutput() {
        System.setOut(new PrintStream(this.out));
    }

    @After
    public void backOutput() {
        System.setOut(this.stdout);
    }

    @Test
    public void whenInvalidInput() {
        ValidateInput input = new ValidateInput(
                new StubInput(new String[] {"one", "1"})
        );
        input.askInt("Enter");
        assertThat(
                out.toString(),
                is(String.format("Please enter valid data again.%n"))
        );
    }

    @Test
    public void whenInvalidInputMaxNumber() {
        ValidateInput input = new ValidateInput(
                new StubInput(new String[] {"8", "1"})
        );
        input.askInt("Enter", 6);
        assertThat(
                out.toString(),
                is(String.format("Please select key from menu.%n"))
        );
    }

    @Test
    public void whenInvalidInputMinNumber() {
        ValidateInput input = new ValidateInput(
                new StubInput(new String[] {"-100", "1"})
        );
        input.askInt("Enter", 6);
        assertThat(
                out.toString(),
                is(String.format("Please select key from menu.%n"))
        );
    }
}