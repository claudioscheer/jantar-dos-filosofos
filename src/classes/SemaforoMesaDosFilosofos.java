package classes;

import java.util.concurrent.Semaphore;

public class SemaforoMesaDosFilosofos {

    private static Semaphore _instance;

    public static Semaphore getInstance() {
        if (_instance == null) {
            _instance = new Semaphore(1);
        }
        return _instance;
    }
}
