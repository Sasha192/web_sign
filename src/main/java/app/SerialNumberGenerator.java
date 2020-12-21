package app;

import java.math.BigInteger;

public class SerialNumberGenerator {

    // We need guarantees, that two different threads (t1, t2)
    // will never run this method in
    // || t1 - t2 || < 1ms

    public static synchronized BigInteger generate() {
        return BigInteger.valueOf(System.currentTimeMillis());
    }
}
