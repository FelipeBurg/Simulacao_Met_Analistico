
/// AJUSTAR EVENTOS DE SAÍDA QUE ESTÃO ESTRANHO
/// FICOU CONFUSO QUANDO O PROCESSO ENTRA, DE FATO, NO ESCALONADOR
/// AJUSTAR METODOS DE CHEGADA E SAIDA

private static double X0 = 0.8986;
private static final double parametroA = 0.9866;
private static final double incrementoC = 0.5663;
private static final double incrementoM = 1.0000;


private static final int TAM_MAX = 10;
private static double TG = 3.0;
//private static int idProcessos = 0;


private static final int  QTDPROCESSOS = 5;




private static int[] servidor = new int[TAM_MAX];

private static ArrayList<Double> aleatoriosGerados = new ArrayList<>();
private static ArrayList<Event> listaEventos = new ArrayList<>();

private static  ArrayList<Double> deltaTempo = new ArrayList<>();

//private static Queue<Double> valoresGerados = new  LinkedList<>();
private static ArrayList<Process> fila = new ArrayList<>();
private static ArrayList<Process> filaPerda = new ArrayList<>();
private static Map<Integer, Double> escalonador = new HashMap<>();


void main() {

    geraProcessos();


    for (int i = 100; i >= 0; i--) {
        Process process = nextEvent();

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
        if (i % 2 == 0) {
            event = Event.IN;
        } else {
            event = Event.OUT;
        }

        process = new Process(tempo, i, event);

        // Append in the event list
        fila.add(process);
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

    if(fila.isEmpty()) {
        return prox;
    }

    for (Process process : fila) {
        if (process.getTempoChegada() < menorTempo) {
            prox = process;
            menorTempo = process.getTempoChegada();
            //fila.remove(process);
        }
    }

    fila.remove(prox);
    escalonador.put(prox.getId(), menorTempo);
    return prox;
}

// In event
static void eventoChegada(Process process) {

    acumalaTempo(process.getTempoChegada());

    log();

    if (fila.size() < TAM_MAX) {
        fila.add(process);
        if (fila.size() <= servidor.length) {
            double saida = tempoSaida();
            escalonador.put(process.getId(), TG + saida);
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

    if(fila.size() > servidor.length) {
        escalonador.put(process.getId(), TG + tempoSaida());
        listaEventos.add(Event.OUT);
        fila.remove(process);
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
    return "Fila de processos: "+fila.toString() +"\nFila de perda: "+filaPerda.toString()+"\nLista eventos: "+listaEventos.toString();

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
