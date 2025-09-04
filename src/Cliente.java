public class Cliente {
    private int id;
    private String nombre;
    private String telefono;
    private String servicio;

    public Cliente(String nombre, String telefono, String servicio) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.servicio = servicio;
    }

    public Cliente(int id, String nombre, String telefono, String servicio) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.servicio = servicio;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getServicio() { return servicio; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setServicio(String servicio) { this.servicio = servicio; }
}