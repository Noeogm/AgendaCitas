import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int id;
    private Cliente cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private String notas;

    public Cita(Cliente cliente, LocalDate fecha, LocalTime hora, String notas) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.notas = notas;
    }

    public Cita(int id, Cliente cliente, LocalDate fecha, LocalTime hora, String notas) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.notas = notas;
    }

    // Getters y setters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getNotas() { return notas; }

    public void setId(int id) { this.id = id; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public void setNotas(String notas) { this.notas = notas; }
}