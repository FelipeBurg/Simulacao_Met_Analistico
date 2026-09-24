
/// AJUSTAR EVENTOS DE SAÍDA QUE ESTÃO ESTRANHO
/// FICOU CONFUSO QUANDO O PROCESSO ENTRA, DE FATO, NO ESCALONADOR
/// AJUSTAR METODOS DE CHEGADA E SAIDA

// Variaveis para gerar os valores aleatórios
private static double X0 = 0.8986;
private static final double parametroA = 0.9866;
private static final double incrementoC = 0.5663;
private static final double incrementoM = 1.0000;

// Variaveis finais
private static final int QTN_ITERACAO = 10;
private static final int TAM_MAX = 2;
private static double TG = 3.0;
private static final int  QTDPROCESSOS = 5;

private static int[] servidor = new int[TAM_MAX];
private static final ArrayList<Double> deltaTempo = new ArrayList<>();
private static final Map<Integer, Double> escalonador = new HashMap<>();


private static final ArrayList<Double> aleatoriosGerados = new ArrayList<>();
private static final ArrayList<Event> listaEventos = new ArrayList<>();

private static ArrayList<Process> processes = new ArrayList<>();
private static ArrayList<Process> filaProcessamento = new ArrayList<>();
private static final ArrayList<Process> filaPerda = new ArrayList<>();


void main() {

    geraProcessos();


    for (int i = 0; i <= QTN_ITERACAO; i++) {
        Process process = nextEvent();

        System.out.println("\nITERAÇÃO: "+i);
        System.out.println("Process: "+process);
        log();


        if(process == null){
            return;
        }

        if (process.getEvent() == Event.IN) {
            eventoChegada(process);
        } else if (process.getEvent() == Event.OUT) {
            eventSaida(process);
        }

        TG += escalonador.get(process.getId());

    }

}


// Generate process with time in and out pseudo random
private static void geraProcessos() {

    double tempo;
    Event event;
    Process process;
    for (int i = 0; i < QTDPROCESSOS; i++) {

        tempo = nextRandom();

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
    double menorTempoChegada = Double.MAX_VALUE;
    double menorTempoSaida = Double.MAX_VALUE;

    if(processes.isEmpty()) {
        return prox;
    }

    for (Process process : processes) {
        double chegada = process.getTempoChegada();
        double saida = process.getTempoSaida();

        boolean chegadaValida = !filaProcessamento.contains(process) && !filaPerda.contains(process);

        boolean saidaValida = filaProcessamento.contains(process) && escalonador.containsKey(process.getId());


        if(chegadaValida && chegada < menorTempoChegada){
            prox = process;
            menorTempoChegada = chegada;

        }

        if(saidaValida && saida < menorTempoSaida){
            prox = process;
            menorTempoSaida = saida;
        }
    }

    //processes.remove(prox);
    prox.setEmProcessamento();
    processes.set(processes.indexOf(prox), prox);
    return prox;
}

// In event
static void eventoChegada(Process process) {

    acumalaTempo(process.getTempoChegada());

    if (filaProcessamento.size() < TAM_MAX) {
        filaProcessamento.add(process);

        if (filaProcessamento.size() <= servidor.length) {
            double saida = tempoSaida();
            escalonador.put(process.getId(), TG + saida);

            process.setEventOut();
            process.setTempoSaida(saida);

            listaEventos.add(Event.IN);
        }

    } else {
        filaPerda.add(process);
        escalonador.put(process.getId(), TG + tempoChegada());
    }


    //idProcessos++;

}


// Exit event
static void eventSaida(Process process) {
    acumalaTempo(process.getTempoSaida());
    filaProcessamento.remove(process);

    if(filaProcessamento.size() > servidor.length) {
        escalonador.put(process.getId(), TG + process.getTempoSaida());
        listaEventos.add(Event.OUT);
    }

}

private static void acumalaTempo(double tempoChegada) {
    deltaTempo.add(tempoChegada);
}

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


static String filasStatus(){
    return "Fila de processos: "+ filaProcessamento +"\nFila de perda: "+filaPerda.toString()+"\nLista eventos: "+listaEventos.toString();

}

static void log() {
    System.out.println(filasStatus());
    System.out.println(tempoStatus());
    System.out.println(escalonadorStatus());
}

static String tempoStatus(){
    return "TG: "+TG+"\nDelta: "+deltaTempo.toString();
}

static String escalonadorStatus(){
    return "Fila escalnador: "+escalonador.toString();
}

//static void numeroAleatorio(double X0, double parametroA, double incrementoC, double incrementoM) {
//
//    double valorGerado;
//
//    for (int i = 0; i < 1000; i++) {
//        valorGerado = (X0*parametroA+incrementoC) % incrementoM;
//
//        valoresGerados.add(valorGerado);
//
//        X0 = valorGerado;
//
//    }
//    //return (X0*parametroA+incrementoC) % incrementoM;
//
//}
