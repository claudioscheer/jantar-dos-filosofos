
public class JantarDosFilosofos {

    private static final int QUANTIDADE_FILOSOFOS = 7;

    public static void main(String[] args) {
        initTalheres();
        initFilosofos();
    }

    private static void initTalheres() {
        Talher[] talheres = TalheresSingleton.getInstance(QUANTIDADE_FILOSOFOS);
        for (int i = 0; i < QUANTIDADE_FILOSOFOS; i++) {
            talheres[i] = new Talher();
        }
    }

    private static void initFilosofos() {
        for (int i = 1; i <= QUANTIDADE_FILOSOFOS; i++) {
            new Thread(new Filosofo(i, QUANTIDADE_FILOSOFOS)).start();
        }
    }

}
