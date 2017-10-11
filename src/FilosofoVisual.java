
import controls.JFilosofo;
import enums.StatusTalher;
import enums.StatusFilosofo;

public class FilosofoVisual implements Runnable {

    private final int id;
    private int indexTalherDireita;
    private int indexTalherEsquerda;
    private StatusFilosofo statusFilosofo;
    private final JFilosofo jFilosofo;

    public FilosofoVisual(int id, int quantidadeDeFilosofos, JFilosofo jFilosofo) {
        this.id = id;
        this.jFilosofo = jFilosofo;
        this.statusFilosofo = StatusFilosofo.Pensando;
        setTalheres(quantidadeDeFilosofos);
    }

    public int getTalherDireita() {
        return indexTalherDireita;
    }

    public void setTalherDireita(int talherDireita) {
        this.indexTalherDireita = talherDireita;
    }

    public int getTalherEsquerda() {
        return indexTalherEsquerda;
    }

    public void setTalherEsquerda(int talherEsquerda) {
        this.indexTalherEsquerda = talherEsquerda;
    }

    public StatusFilosofo getStatusFilosofo() {
        return statusFilosofo;
    }

    public void setStatusFilosofo(StatusFilosofo statusFilosofo) {
        this.statusFilosofo = statusFilosofo;
    }

    private void setTalheres(int quantidadeDeFilosofos) {
        if (id == 1) {
            indexTalherDireita = quantidadeDeFilosofos - 1;
        } else {
            indexTalherDireita = id - 2;
        }
        indexTalherEsquerda = id - 1;
    }

    private boolean tentarComer() throws InterruptedException {
        Talher talherDireita = TalheresSingleton.getInstance(0)[indexTalherDireita];
        if (talherDireita.getStatusTalher() == StatusTalher.EmUso) {
            return false;
        }
        talherDireita.getSemaforo().acquire();
        talherDireita.setStatusTalher(StatusTalher.EmUso);
        talherDireita.getSemaforo().release();

        Talher talherEsquerda = TalheresSingleton.getInstance(0)[indexTalherEsquerda];
        if (talherEsquerda.getStatusTalher() == StatusTalher.EmUso) {
            talherDireita.getSemaforo().acquire();
            talherDireita.setStatusTalher(StatusTalher.Livre);
            talherDireita.getSemaforo().release();
            return false;
        }
        talherEsquerda.getSemaforo().acquire();
        talherEsquerda.setStatusTalher(StatusTalher.EmUso);
        talherEsquerda.getSemaforo().release();

        jFilosofo.setStatusFilosofo(StatusFilosofo.Comendo);
        return true;
    }

    private void pararDeComer() throws InterruptedException {
        Talher talherDireita = TalheresSingleton.getInstance(0)[indexTalherDireita];
        talherDireita.getSemaforo().acquire();
        talherDireita.setStatusTalher(StatusTalher.Livre);
        talherDireita.getSemaforo().release();

        Talher talherEsquerda = TalheresSingleton.getInstance(0)[indexTalherEsquerda];
        talherEsquerda.getSemaforo().acquire();
        talherEsquerda.setStatusTalher(StatusTalher.Livre);
        talherEsquerda.getSemaforo().release();

        jFilosofo.setStatusFilosofo(StatusFilosofo.Pensando);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (tentarComer()) {
                    Thread.sleep(((int) Math.random() * 2000) + 1000);
                    pararDeComer();
                }
                Thread.sleep(((int) Math.random() * 5000) + 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
