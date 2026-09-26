
/// AJUSTAR EVENTOS DE SAÍDA QUE ESTÃO ESTRANHO
/// FICOU CONFUSO QUANDO O PROCESSO ENTRA, DE FATO, NO ESCALONADOR
/// AJUSTAR METODOS DE CHEGADA E SAIDA

// Variaveis para gerar os valores aleatórios
private static double X0 = 0.8986;
private static final double parametroA = 0.9866;
private static final double incrementoC = 0.5663;
private static final double incrementoM = 1.0000;
private static final ArrayList<Double> aleatoriosGerados = new ArrayList<>();

// Variaveis finais
private static final int QTD_ITERACAO = 10;
private static final int TAM_MAX = 2;
private static double TG = 3.0;
private static final int QTD_PROCESSOS = 5;
private static final int QTD_SERVIDORES = 2;

// Servidores <idServidor, IdProcesso>
private static Map<Integer, Integer> servidores = new HashMap<>();
private static final ArrayList<Double> deltaTempo = new ArrayList<>();
private static final Map<Integer, Double> escalonador = new HashMap<>();

private static final ArrayList<Event> listaEventos = new ArrayList<>();

// Processos gerados
private static ArrayList<Process> processes = new ArrayList<>();

// Fila de processos onde os processos irão entrar pra ir aos servidores
private static ArrayList<Process> filaProcessamento = new ArrayList<>();
private static final ArrayList<Process> filaPerda = new ArrayList<>();

private static final ArrayList<Process> filaProcessoSaida = new ArrayList<>();


void main() {

    initServidores();
    geraProcessos();

    System.out.println("Processos gerados:");
    processes.forEach(p -> System.out.println(p));


    for (int i = 0; i <= QTD_ITERACAO; i++) {
        Process process = nextEvent();

        System.out.println("\nITERAÇÃO: " + i);
        System.out.print("Process: " + process);
        log();


        if (process == null) {
            return;
        }

        if (process.getEvent() == Event.IN) {
            eventoChegada(process);
        } else if (process.getEvent() == Event.OUT) {
            eventSaida(process);
        }

        Double proxTempo = escalonador.get(process.getId());

        if (proxTempo != null) {
            TG = proxTempo;
        }


    }

}


// Generate process with time in and out pseudo random
private static void geraProcessos() {

    double tempo;
    Event event;
    Process process;
    for (int i = 1; i < QTD_PROCESSOS + 1; i++) {

        tempo = tempoChegada();

        // Define event type
//        if (i % 2 == 0) {
//            event = Event.IN;
//        } else {
//            event = Event.OUT;
//        }

        process = new Process(tempo, i, Event.IN);

        // Append in the event list
        processes.add(process);
    }

}


// Generate and return a pseudo random value
static double nextRandom() {
    X0 = ((parametroA * X0) + incrementoC) % incrementoM;
    aleatoriosGerados.add(X0);
    return X0 / incrementoM;
}

// Return the next event
static Process nextEvent() {
    Process prox = null;
    double menorTempo = Double.MAX_VALUE;

    // Pega o processo que possui menor tempo de chega ou saída como próximo evento
    for (Process process : processes) {
        if (process.getTempoChegada() < menorTempo) {
            prox = process;
            menorTempo = process.getTempoChegada();
            prox.setEvent(Event.IN);
        }
    }

    for (Process process : filaProcessoSaida) {
        if (process.getTempoSaida() < menorTempo) {
            prox = process;
            menorTempo = process.getTempoSaida();
            prox.setEvent(Event.OUT);
        }
    }

    if (prox != null) {


        if (prox.getEvent() == Event.IN) {
            processes.remove(prox);
        } else {
            filaProcessoSaida.remove(prox);
        }
    }

    return prox;
}

// In event
static void eventoChegada(Process process) {

    acumalaTempo(process.getTempoChegada());

    int servidorlivre = getServidoresLivres();


    // Se fila de processamento ainda tem espaço, adiciona. Senão, perde processo
    if (filaProcessamento.size() < TAM_MAX) {
        filaProcessamento.add(process);

        // se ainda tem servidores livre, adiciona e um servidor e calcula uma saída.
        if (filaProcessamento.size() <= servidorlivre) {
            double saida = tempoSaida();

            // Calcula uma nova saída para ele
            escalonador.put(process.getId(), TG + saida);

            // Adiciona em um servidor o id do processo atual, ou seja, tona ele ocupado
            adicionaProcessoAoServidor(process.getId());

            // Define o tempo de saída do processo
            process.setTempoSaida(saida);
            listaEventos.add(Event.OUT);

            filaProcessoSaida.add(process);
        }

    } else {
        filaPerda.add(process);
        //escalonador.put(process.getId(), TG + tempoChegada());
    }
}


// Exit event
static void eventSaida(Process process) {

    acumalaTempo(process.getTempoSaida());

    // Processo saiu, apenas remove ele
    filaProcessamento.remove(process);


    // Libera servidor
    liberaProcessoDoServidor(process.getId());


    // Caso ainda tenha processo pra ser escalonado, coloca no servidor
    if (!filaProcessamento.isEmpty() && getServidoresLivres() > 0) {

        // Para o próximo processa, calcula um tempo de saída e adiciona a um servidor
        Process nextProcess = filaProcessamento.getFirst();
        double saida = tempoSaida();
        process.setTempoSaida(saida);


        // pega tempo de saída o próximo processo e adiciona a um servidor
        escalonador.put(nextProcess.getId(), TG + nextProcess.getTempoSaida());

        listaEventos.add(Event.OUT);
    }

}

// Inicializa todos os servidores como vazio (0)
static void initServidores() {
    for (int i = 0; i < QTD_SERVIDORES; i++) {
        servidores.put(i, 0);
    }
}

private static void acumalaTempo(double tempoChegada) {
    deltaTempo.add(tempoChegada);
}


// Define o primeiro servidor livre para o processo
static void adicionaProcessoAoServidor(int idProcesso) {

    for (int i = 0; i < QTD_SERVIDORES; i++) {
        if (servidores.get(i) == 0) {
            servidores.put(i, idProcesso);
            break;
        }
    }
}


// Define o servidor que estava como o processo como livre
static void liberaProcessoDoServidor(int idProcesso) {

    for (int i = 0; i < QTD_SERVIDORES; i++) {
        if (servidores.get(i) == idProcesso) {
            servidores.put(i, 0);
        }
    }

}

static int getServidoresLivres() {
    int count = 0;

    for (int i = 0; i < QTD_SERVIDORES; i++) {

        if (servidores.get(i) == 0) {
            count++;
        }
    }

    return count;

}


/// METODOS DE LOGS - NÃO PRECISAM DE ALTERACAO AGORA
static String filasStatus() {
    return "Fila de processos: " + filaProcessamento + "\nFila de perda: " + filaPerda.toString() + "\nLista eventos: " + listaEventos.toString();

}

static String tempoStatus() {
    return "TG: " + TG + "\nDelta: " + deltaTempo.toString();
}

static String escalonadorStatus() {
    return "Fila escalnador: " + escalonador.toString();
}

static void log() {
    System.out.println(filasStatus());
    System.out.println(tempoStatus());
    System.out.println(escalonadorStatus());
}


/// APENAS CALCULAM O TEMPO DE CHEGADA E SAÍDA, NÃO PRECISA MUDAR AGORA
static double tempoSaida() {
    int tempo1 = 4;
    int tempo2 = 5;

    return (tempo2 - tempo1) * nextRandom() + 1;
}

static double tempoChegada() {
    int tempo1 = 3;
    int tempo2 = 5;

    return (tempo2 - tempo1) * nextRandom() + 1;
}

