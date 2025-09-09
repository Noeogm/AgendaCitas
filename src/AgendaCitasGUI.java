import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AgendaCitasGUI extends JFrame {

    // Constantes para dimensiones y posiciones
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;
    private static final int LABEL_WIDTH = 100;
    private static final int FIELD_WIDTH = 200;
    private static final int COMPONENT_HEIGHT = 25;
    private static final int MARGIN = 30;
    private static final int VERTICAL_SPACING = 40;

    // Componentes como campos de clase para mejor acceso
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JComboBox<String> cmbServicio;
    private JTextField txtFecha;
    private JTextField txtHora;

    // Constructor para modo edición (fecha/hora predefinidas)
    public AgendaCitasGUI(LocalDate fecha, LocalTime hora) {
        this();
        txtFecha.setText(fecha.toString());
        txtHora.setText(hora.toString());
        txtFecha.setEditable(false);
        txtHora.setEditable(false);
    }

    // Constructor principal
    public AgendaCitasGUI() {
        initializeWindow();
        createComponents();
        setupEventHandlers();
    }

    private void initializeWindow() {
        setTitle("Agenda de Citas");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
    }

    private void createComponents() {
        int yPosition = MARGIN;

        // Nombre
        addLabelAndField("Nombre:", yPosition,
                txtNombre = new JTextField());
        yPosition += VERTICAL_SPACING;

        // Teléfono
        addLabelAndField("Teléfono:", yPosition,
                txtTelefono = new JTextField());
        yPosition += VERTICAL_SPACING;

        // Servicio
        JLabel lblServicio = new JLabel("Servicio:");
        lblServicio.setBounds(MARGIN, yPosition, LABEL_WIDTH, COMPONENT_HEIGHT);
        add(lblServicio);

        String[] servicios = {"Corte de cabello", "Tinte", "Peinado",
                "Manicura", "Pedicura", "Depilación", "Facial"};
        cmbServicio = new JComboBox<>(servicios);
        cmbServicio.setBounds(MARGIN + LABEL_WIDTH + 10, yPosition,
                FIELD_WIDTH, COMPONENT_HEIGHT);
        add(cmbServicio);
        yPosition += VERTICAL_SPACING;

        // Fecha
        addLabelAndField("Fecha:", yPosition,
                txtFecha = new JTextField("YYYY-MM-DD"));
        yPosition += VERTICAL_SPACING;

        // Hora
        addLabelAndField("Hora:", yPosition,
                txtHora = new JTextField("HH:MM"));
        yPosition += VERTICAL_SPACING + 10;

        // Botón registrar
        JButton btnRegistrar = new JButton("Registrar Cita");
        btnRegistrar.setBounds(MARGIN + LABEL_WIDTH + 10, yPosition, 150, 30);
        add(btnRegistrar);
    }

    private void addLabelAndField(String labelText, int yPosition, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setBounds(MARGIN, yPosition, LABEL_WIDTH, COMPONENT_HEIGHT);
        add(label);

        field.setBounds(MARGIN + LABEL_WIDTH + 10, yPosition,
                FIELD_WIDTH, COMPONENT_HEIGHT);
        add(field);
    }

    private void setupEventHandlers() {
        // Encontrar el botón por sus componentes
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Registrar Cita")) {
                ((JButton) comp).addActionListener(e -> handleRegistrarCita());
                break;
            }
        }
    }

    private void handleRegistrarCita() {
        // Validar campos vacíos
        if (!validarCampos()) {
            return;
        }

        // Obtener datos del formulario
        FormData formData = obtenerDatosFormulario();
        if (formData == null) {
            return;
        }

        // Verificar disponibilidad
        if (!verificarDisponibilidad(formData.fecha, formData.hora)) {
            return;
        }

        // Procesar registro
        procesarRegistro(formData);
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String horaStr = txtHora.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty() ||
                fechaStr.isEmpty() || horaStr.isEmpty() ||
                fechaStr.equals("YYYY-MM-DD") || horaStr.equals("HH:MM")) {
            mostrarError("Por favor completa todos los campos correctamente.");
            return false;
        }
        return true;
    }

    private FormData obtenerDatosFormulario() {
        try {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String servicio = (String) cmbServicio.getSelectedItem();
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());

            return new FormData(nombre, telefono, servicio, fecha, hora);
        } catch (DateTimeParseException ex) {
            mostrarError("Formato de fecha u hora incorrecto.\nUsa: YYYY-MM-DD para fecha y HH:MM para hora.");
            return null;
        }
    }

    private boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha = ? AND hora = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            stmt.setTime(2, java.sql.Time.valueOf(hora));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    mostrarError("Ese horario ya está ocupado. Por favor elige otro.");
                    return false;
                }
            }
            return true;

        } catch (SQLException ex) {
            mostrarError("Error al verificar disponibilidad: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private void procesarRegistro(FormData data) {
        try {
            Cliente cliente = new Cliente(data.nombre, data.telefono, data.servicio);
            Clientedb clienteDAO = new Clientedb();
            int clienteId = clienteDAO.insertarCliente(cliente);

            if (clienteId == -1) {
                mostrarError("Error al registrar el cliente.");
                return;
            }

            Cita cita = new Cita(cliente, data.fecha, data.hora, "");
            Citadb citadb = new Citadb();
            boolean exito = citadb.insertarCita(cita);

            if (exito) {
                mostrarExito("Cita registrada correctamente.");
                limpiarCampos();
            } else {
                mostrarError("Error al registrar la cita.");
            }

        } catch (Exception ex) {
            mostrarError("Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        if (txtFecha.isEditable()) { // Solo limpiar si no están bloqueados
            txtNombre.setText("");
            txtTelefono.setText("");
            cmbServicio.setSelectedIndex(0);
            txtFecha.setText("YYYY-MM-DD");
            txtHora.setText("HH:MM");
        } else {
            txtNombre.setText("");
            txtTelefono.setText("");
            cmbServicio.setSelectedIndex(0);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // Clase interna para encapsular los datos del formulario
    private static class FormData {
        final String nombre;
        final String telefono;
        final String servicio;
        final LocalDate fecha;
        final LocalTime hora;

        FormData(String nombre, String telefono, String servicio,
                 LocalDate fecha, LocalTime hora) {
            this.nombre = nombre;
            this.telefono = telefono;
            this.servicio = servicio;
            this.fecha = fecha;
            this.hora = hora;
        }
    }
}