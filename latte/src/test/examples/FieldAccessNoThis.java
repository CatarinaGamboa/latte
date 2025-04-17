
package latte;

import specification.Shared;
import specification.Unique;

public class FieldAccessNoThis {
    @Unique Object value;

    public FieldAccessNoThis(@Shared Object t){
        value = t;
    }
}
