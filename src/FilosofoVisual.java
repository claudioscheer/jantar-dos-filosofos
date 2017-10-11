
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FilosofoVisual implements Runnable {

    private final int id;
    private final int quantidadeDeFilosofos;
    private int indexTalherDireita;
    private int indexTalherEsquerda;
    private StatusFilosofo statusFilosofo;
    private int linha;
    private int coluna;
    private JPanel[][] panelsFilosofos;

    public FilosofoVisual(int id, int quantidadeDeFilosofos, JPanel[][] panelsFilosofos, int linha, int coluna) {
        this.id = id;
        this.quantidadeDeFilosofos = quantidadeDeFilosofos;
        this.panelsFilosofos = panelsFilosofos;
        this.coluna = coluna;
        this.linha = linha;
        this.statusFilosofo = StatusFilosofo.Pensando;
        setTalheres();
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

    private void setTalheres() {
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

        JPanel panel = panelsFilosofos[linha][coluna];
        panel.removeAll();
        panel.add(new JLabel(String.format("Filósofo %s comendo.", id)));
        panel.revalidate();
        panel.repaint();
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

        JPanel panel = panelsFilosofos[linha][coluna];
        panel.removeAll();
        panel.add(new JLabel(String.format("Filósofo %s pensando.", id)));
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (tentarComer()) {
                    System.out.println(String.format("Filósofo %s comeu.", id));
                    Thread.sleep(((int) Math.random() * 2000) + 1000);
                    pararDeComer();
                    System.out.println(String.format("Filósofo %s parou de comer.", id));
                } else {
                    // System.out.println(String.format("Filósofo %s tentou comer.", id));
                }
                Thread.sleep(((int) Math.random() * 5000) + 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
