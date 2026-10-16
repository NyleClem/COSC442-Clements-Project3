import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VendingMachineExceptionTest {

    @Test
    void testDefaultConstructorCreatesException() {
        // Act
        VendingMachineException exception = new VendingMachineException();

        // Assert
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithReasonSetsMessage() {
        // Arrange
        String reason = "Invalid price";

        // Act
        VendingMachineException exception =
                new VendingMachineException(reason);

        // Assert
        assertEquals(reason, exception.getMessage());
    }
}