import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ModificarCitasGUI extends JFrame {

    private JTextField txtTelefono;
    private JComboBox<String> cmbServicio;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtnombre;
    private int citaId = -1;
    private String fechaOriginal = "";
    private String horaOriginal = "";

    public ModificarCitasGUI() {
        setTitle("Modificar Citas");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        // Search criteria dropdown
        JLabel lblCriterio = new JLabel("Buscar por:");
        lblCriterio.setBounds(30, 10, 100, 20);
        add(lblCriterio);

        String[] criterios = {"Nombre", "Teléfono"};
        JComboBox<String> cmbCriterio = new JComboBox<>(criterios);
        cmbCriterio.setBounds(30, 30, 120, 25);
        add(cmbCriterio);

        // Search field
        JTextField txtBuscar = new JTextField();
        txtBuscar.setBounds(160, 30, 200, 25);
        add(txtBuscar);

        // Search button
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(380, 30, 100, 25);
        add(btnBuscar);

        // Form fields with labels
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 80, 100, 20);
        add(lblNombre);

        txtnombre = new JTextField();
        txtnombre.setBounds(30, 100, 200, 25);
        add(txtnombre);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(250, 80, 100, 20);
        add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(250, 100, 150, 25);
        add(txtTelefono);

        JLabel lblServicio = new JLabel("Servicio:");
        lblServicio.setBounds(30, 140, 100, 20);
        add(lblServicio);

        String[] servicios = {"Corte de cabello", "Tinte", "Peinado", "Manicura", "Pedicura", "Depilación", "Facial"};
        cmbServicio = new JComboBox<>(servicios);
        cmbServicio.setBounds(30, 160, 200, 25);
        add(cmbServicio);

        JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
        lblFecha.setBounds(30, 200, 150, 20);
        add(lblFecha);

        txtFecha = new JTextField("YYYY-MM-DD");
        txtFecha.setBounds(30, 220, 150, 25);
        add(txtFecha);

        JLabel lblHora = new JLabel("Hora (HH:MM):");
        lblHora.setBounds(200, 200, 100, 20);
        add(lblHora);

        txtHora = new JTextField("HH:MM");
        txtHora.setBounds(200, 220, 100, 25);
        add(txtHora);

        // Buttons
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(30, 280, 120, 30);
        add(btnActualizar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(170, 280, 120, 30);
        add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(310, 280, 120, 30);
        add(btnLimpiar);

        // Event listeners
        btnBuscar.addActionListener(e -> buscarCita(cmbCriterio, txtBuscar));
        btnActualizar.addActionListener(e -> actualizarCita());
        btnEliminar.addActionListener(e -> eliminarCita());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void buscarCita(JComboBox<String> cmbCriterio, JTextField txtBuscar) {
        String criterio = cmbCriterio.getSelectedItem().toString();
        String valor = txtBuscar.getText().trim();

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un valor para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String campoSQL = criterio.equals("Nombre") ? "nombre" : "telefono";
        String sql = "SELECT c.id as cita_id, cl.id as cliente_id, cl.nombre, cl.telefono, cl.servicio, c.fecha, c.hora " +
                "FROM clientes cl " +
                "JOIN citas c ON cl.id = c.cliente_id " +
                "WHERE cl." + campoSQL + " = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, valor);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.citaId = rs.getInt("cita_id");

                txtnombre.setText(rs.getString("nombre"));
                txtTelefono.setText(rs.getString("telefono"));
                cmbServicio.setSelectedItem(rs.getString("servicio"));

                // Guardar valores originales para comparación posterior
                fechaOriginal = rs.getString("fecha");
                horaOriginal = rs.getString("hora");

                txtFecha.setText(fechaOriginal);
                txtHora.setText(horaOriginal);

            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna cita con ese dato.", "Sin resultados", JOptionPane.WARNING_MESSAGE);
                limpiarCampos();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarCita() {
        if (citaId == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque una cita para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos básicos
        if (!validarCamposBasicos()) {
            return;
        }

        // Validar y obtener fecha y hora
        LocalDate nuevaFecha;
        LocalTime nuevaHora;
        try {
            nuevaFecha = LocalDate.parse(txtFecha.getText().trim());
            nuevaHora = LocalTime.parse(txtHora.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha u hora incorrecto.\n" +
                            "Use: YYYY-MM-DD para fecha y HH:MM para hora.\n" +
                            "Ejemplo: 2024-12-25 y 14:30",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar disponibilidad solo si cambió la fecha u hora
        String nuevaFechaStr = nuevaFecha.toString();
        String nuevaHoraStr = nuevaHora.toString();

        if (!nuevaFechaStr.equals(fechaOriginal) || !nuevaHoraStr.equals(horaOriginal)) {
            if (!verificarDisponibilidad(nuevaFecha, nuevaHora)) {
                return;
            }
        }

        // Proceder con la actualización
        String nuevoNombre = txtnombre.getText().trim();
        String nuevoTelefono = txtTelefono.getText().trim();
        String nuevoServicio = cmbServicio.getSelectedItem().toString();

        String sqlUpdateCliente = "UPDATE clientes SET nombre = ?, telefono = ?, servicio = ? WHERE id = (SELECT cliente_id FROM citas WHERE id = ?)";
        String sqlUpdateCita = "UPDATE citas SET fecha = ?, hora = ? WHERE id = ?";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlUpdateCliente);
                 PreparedStatement stmtCita = conn.prepareStatement(sqlUpdateCita)) {

                // Update client information
                stmtCliente.setString(1, nuevoNombre);
                stmtCliente.setString(2, nuevoTelefono);
                stmtCliente.setString(3, nuevoServicio);
                stmtCliente.setInt(4, citaId);
                stmtCliente.executeUpdate();

                // Update appointment information
                stmtCita.setDate(1, java.sql.Date.valueOf(nuevaFecha));
                stmtCita.setTime(2, java.sql.Time.valueOf(nuevaHora));
                stmtCita.setInt(3, citaId);
                stmtCita.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "✅ Cita actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar la cita: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean validarCamposBasicos() {
        String nombre = txtnombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String fecha = txtFecha.getText().trim();
        String hora = txtHora.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty() ||
                fecha.isEmpty() || hora.isEmpty() ||
                fecha.equals("YYYY-MM-DD") || hora.equals("HH:MM")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos correctamente.\n" +
                            "Asegúrese de no dejar los valores por defecto.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha = ? AND hora = ? AND id != ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            stmt.setTime(2, java.sql.Time.valueOf(hora));
            stmt.setInt(3, citaId); // Excluir la cita actual de la verificación

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "⚠️ Ese horario ya está ocupado por otra cita.\n" +
                                    "Por favor elija otro horario.",
                            "Horario no disponible", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
            return true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error al verificar disponibilidad: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private void eliminarCita() {
        if (citaId == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque una cita para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea eliminar esta cita?\n\n" +
                        "👤 Nombre: " + txtnombre.getText() + "\n" +
                        "📞 Teléfono: " + txtTelefono.getText() + "\n" +
                        "💅 Servicio: " + cmbServicio.getSelectedItem() + "\n" +
                        "📅 Fecha: " + txtFecha.getText() + "\n" +
                        "⏰ Hora: " + txtHora.getText(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sqlDeleteCita = "DELETE FROM citas WHERE id = ?";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int clienteId = -1;
                String sqlGetClienteId = "SELECT cliente_id FROM citas WHERE id = ?";
                try (PreparedStatement stmtGetCliente = conn.prepareStatement(sqlGetClienteId)) {
                    stmtGetCliente.setInt(1, citaId);
                    ResultSet rs = stmtGetCliente.executeQuery();
                    if (rs.next()) {
                        clienteId = rs.getInt("cliente_id");
                    }
                }

                try (PreparedStatement stmtDeleteCita = conn.prepareStatement(sqlDeleteCita)) {
                    stmtDeleteCita.setInt(1, citaId);
                    stmtDeleteCita.executeUpdate();
                }

                String sqlCheckClienteOtherCitas = "SELECT COUNT(*) FROM citas WHERE cliente_id = ?";
                try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckClienteOtherCitas)) {
                    stmtCheck.setInt(1, clienteId);
                    ResultSet rs = stmtCheck.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        String sqlDeleteClienteById = "DELETE FROM clientes WHERE id = ?";
                        try (PreparedStatement stmtDeleteCliente = conn.prepareStatement(sqlDeleteClienteById)) {
                            stmtDeleteCliente.setInt(1, clienteId);
                            stmtDeleteCliente.executeUpdate();
                        }
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "✅ Cita eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al eliminar la cita: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtnombre.setText("");
        txtTelefono.setText("");
        cmbServicio.setSelectedIndex(0);
        txtFecha.setText("YYYY-MM-DD");
        txtHora.setText("HH:MM");
        citaId = -1;
        fechaOriginal = "";
        horaOriginal = "";
    }
}