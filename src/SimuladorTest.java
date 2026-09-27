import java.util.List;

public final class SimuladorTest {
    public static void main(String[] args) {
        testaGeradorLCG();
        testaLimiteDoGerador();
        testaOrdemCronologica();
        System.out.println("Testes basicos aprovados.");
    }

    private static void testaGeradorLCG() {
        GeradorLCG gerador = new GeradorLCG(1, 5, 1, 16, List.of());
        assertEquals(6.0 / 16.0, gerador.proximo(), "primeiro valor do LCG");
        assertEquals(15.0 / 16.0, gerador.proximo(), "segundo valor do LCG");
    }

    private static void testaLimiteDoGerador() {
        GeradorLCG gerador = new GeradorLCG(1, 1, 1, 2, List.of());
        for (int i = 0; i < GeradorLCG.LIMITE; i++) {
            gerador.proximo();
        }
        try {
            gerador.proximo();
            throw new AssertionError("O limite de aleatorios nao foi aplicado");
        } catch (LimiteAleatoriosException esperado) {
            assertEquals(GeradorLCG.LIMITE, gerador.consumidos(), "contador no limite");
        }
    }

    private static void testaOrdemCronologica() {
        EventoSimulacao primeiro = new EventoSimulacao(2.0, EventoSimulacao.Tipo.CHEGADA,
                "-1", "Q1", new Cliente(1), 1);
        EventoSimulacao segundo = new EventoSimulacao(1.0, EventoSimulacao.Tipo.SAIDA,
                "Q1", "-1", new Cliente(2), 2);
        if (primeiro.compareTo(segundo) <= 0) {
            throw new AssertionError("Eventos nao foram ordenados pelo instante absoluto");
        }
    }

    private static void assertEquals(double esperado, double atual, String descricao) {
        if (Double.compare(esperado, atual) != 0) {
            throw new AssertionError(descricao + ": esperado " + esperado + ", obtido " + atual);
        }
    }

    private static void assertEquals(int esperado, int atual, String descricao) {
        if (esperado != atual) {
            throw new AssertionError(descricao + ": esperado " + esperado + ", obtido " + atual);
        }
    }
}