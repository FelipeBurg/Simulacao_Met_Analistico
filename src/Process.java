public class Process {
    private  double tempoChegada;
    private  double tempoSaida = 0.0;
    private  int id;
    private Event event;


    public Process(double tempoChegada, int id, Event event) {
        this.tempoChegada = tempoChegada;
        this.id = id;
        this.event = event;

    }

    void setTempoSaida(double tempoSaida){
        this.tempoSaida = tempoSaida;
    }

    double getTempoChegada(){
        return this.tempoChegada;
    }

    double getTempoSaida(){
        return this.tempoSaida;
    }

    int  getId(){
        return this.id;
    }

    Event getEvent(){
        return this.event;
    }

    public String toString(){
        return "ID: "+getId()+"\nTempo chegada: "+getTempoChegada()+"\nTempo saída: "+getTempoSaida();
    }
}
