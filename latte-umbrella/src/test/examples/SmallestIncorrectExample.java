import specification.Borrowed;
import specification.Unique;

public class SmallestIncorrectExample {

    @Unique Object value;
    
    public void test ( @Borrowed Object value) {
        this.value = value;
    }
    
    
}
