package latte;

import java.util.ArrayList;

import specification.Borrowed;
import specification.Free;
import specification.Shared;
import specification.Unique;

class BoxMain {
    public static void test(@Borrowed ArrayList<String> list) {
        BoxMain1 b1 = new BoxMain1();
        b1.add(123);
    }
}


class BoxMain1{
    @Unique int value;

    void add(@Borrowed int delta){
        this.value = this.value + delta;
    }

}

class BoxMain2{
    @Shared int val;

    public BoxMain2(@Free int v){
        this.val = v; 
    }
}