
public class TalheresSingleton {

    private static Talher[] _instance;

    public static Talher[] getInstance(int size) {
        if (_instance == null) {
            _instance = new Talher[size];
        }
        return _instance;
    }

}
