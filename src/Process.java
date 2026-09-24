public class Process {
    private  double tempoChegada;
    private  double tempoSaida = Double.MAX_VALUE;
    private  int id;
    private Event event;
    private boolean emProcessamento;


    public Process(double tempoChegada, int id, Event event) {
        this.tempoChegada = tempoChegada;
        this.id = id;
        this.event = event;
        this.emProcessamento = false;

    }

    void setTempoSaida(double tempoSaida){
        this.tempoSaida = tempoSaida;
    }


    void setEventOut(){
        this.event = Event.OUT;
    }

    void setEmProcessamento(){
        this.emProcessamento = true;
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

    boolean getEmProcessamento(){
        return this.emProcessamento;
    }

    Event getEvent(){
        return this.event;
    }

    public String toString(){
        return "\nID: "+getId()+" Tempo chegada: "+getTempoChegada()+" Tempo saída: "+getTempoSaida();
    }
}
