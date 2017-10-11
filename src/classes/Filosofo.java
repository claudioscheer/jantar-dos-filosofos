package classes;

import controls.JFilosofo;
import enums.StatusTalher;
import enums.StatusFilosofo;

public class Filosofo implements Runnable {

    private final int id;
    private int indexTalherDireita;
    private int indexTalherEsquerda;
    private StatusFilosofo statusFilosofo;
    private final JFilosofo jFilosofo;

    public Filosofo(int id, int quantidadeDeFilosofos, JFilosofo jFilosofo) {
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
        jFilosofo.setStatusFilosofo(statusFilosofo);
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
        Talher talherDireita = TalheresSingleton.getInstance()[indexTalherDireita];
        talherDireita.getSemaforo().acquire();
        if (talherDireita.getStatusTalher() == StatusTalher.EmUso) {
            talherDireita.getSemaforo().release();
            return false;
        }

        talherDireita.setStatusTalher(StatusTalher.EmUso);
        talherDireita.getSemaforo().release();

        Talher talherEsquerda = TalheresSingleton.getInstance()[indexTalherEsquerda];
        talherEsquerda.getSemaforo().acquire();
        if (talherEsquerda.getStatusTalher() == StatusTalher.EmUso) {
            talherEsquerda.getSemaforo().release();
            talherDireita.getSemaforo().acquire();
            talherDireita.setStatusTalher(StatusTalher.Livre);
            talherDireita.getSemaforo().release();
            return false;
        }
        talherEsquerda.setStatusTalher(StatusTalher.EmUso);
        talherEsquerda.getSemaforo().release();

        setStatusFilosofo(StatusFilosofo.Comendo);
        return true;
    }

    private void pararDeComer() throws InterruptedException {
        TalheresSingleton.getInstance()[indexTalherDireita].setStatusTalher(StatusTalher.Livre);
        TalheresSingleton.getInstance()[indexTalherEsquerda].setStatusTalher(StatusTalher.Livre);

        setStatusFilosofo(StatusFilosofo.Pensando);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (tentarComer()) {
                    // Tempo que o filósofo fica comendo.
                    Thread.sleep(((int) Math.random() * 4000) + 500);
                    pararDeComer();
                }
                // Tempo que o filósofo fica pensando.
                Thread.sleep(((int) Math.random() * 2000) + 500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
