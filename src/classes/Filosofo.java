package classes;

import controls.JFilosofo;
import enums.StatusTalher;
import enums.StatusFilosofo;

public class Filosofo implements Runnable {

    private final int id;
    private int indexTalherDireita;
    private int indexTalherEsquerda;
    // Não é usado mas vai ficar.
    private StatusFilosofo statusFilosofo;
    private final JFilosofo jFilosofo;

    public Filosofo(int id, int quantidadeDeFilosofos, JFilosofo jFilosofo) {
        this.id = id;
        this.jFilosofo = jFilosofo;
        setTalheres(quantidadeDeFilosofos);
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
            // Não pôde pegar a talher da direita.
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

            // Não pôde pegar a talher da esquerda, então, solta a talher da direita.
            return false;
        }
        talherEsquerda.setStatusTalher(StatusTalher.EmUso);
        talherEsquerda.getSemaforo().release();

        return true;
    }

    private void pararDeComer() throws InterruptedException {
        TalheresSingleton.getInstance()[indexTalherDireita].setStatusTalher(StatusTalher.Livre);
        TalheresSingleton.getInstance()[indexTalherEsquerda].setStatusTalher(StatusTalher.Livre);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // O filósofo vai tentar comer e fica 1.5 segundos com fome só para dar tempo de mostrar isso em tela.
                setStatusFilosofo(StatusFilosofo.ComFome);
                Thread.sleep(1500);
                if (tentarComer()) {
                    // Conseguiu pegar as duas talheres.
                    setStatusFilosofo(StatusFilosofo.Comendo);
                    // Tempo que o filósofo fica comendo.
                    Thread.sleep((int) (Math.random() * 1000) + 500);
                    setStatusFilosofo(StatusFilosofo.Pensando);
                    pararDeComer();
                }
                // Tempo que o filósofo fica pensando.
                Thread.sleep((int) (Math.random() * 1000) + 500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
