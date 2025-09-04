import javax.swing.*;
import java.awt.*;
import java.time.*;
//import java.util.*;
import java.sql.*;
import java.util.Set;
import java.util.HashSet;

public class SeleccionarSlotGUI extends JFrame {

    private JComboBox<LocalDate> cmbFechas;
    private DefaultListModel<LocalTime> modeloHoras;
    private JList<LocalTime> listaHoras;

    public SeleccionarSlotGUI() {
        setTitle("Seleccionar Fecha y Hora");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        // Panel superior: fechas
        cmbFechas = new JComboBox<>();
        cargarFechas();
        cmbFechas.addActionListener(e -> cargarHorasDisponibles());

        JPanel panelFecha = new JPanel();
        panelFecha.add(new JLabel("Selecciona una fecha:"));
        panelFecha.add(cmbFechas);
        add(panelFecha, BorderLayout.NORTH);

        // Panel central: lista de horas
        modeloHoras = new DefaultListModel<>();
        listaHoras = new JList<>(modeloHoras);
        add(new JScrollPane(listaHoras), BorderLayout.CENTER);

        // Panel inferior: botón
        JButton btnSeleccionar = new JButton("Agendar en este horario");
        btnSeleccionar.addActionListener(e -> abrirFormulario());
        add(btnSeleccionar, BorderLayout.SOUTH);

        cargarHorasDisponibles(); // inicial
    }

    private void cargarFechas() {
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i <= 45; i++) {
            cmbFechas.addItem(hoy.plusDays(i));
        }
    }

    private void cargarHorasDisponibles() {
        modeloHoras.clear();
        LocalDate fechaSeleccionada = (LocalDate) cmbFechas.getSelectedItem();
        Set<LocalTime> ocupadas = obtenerHorasOcupadas(fechaSeleccionada);

        for (int hora = 9; hora <= 18; hora++) {
            LocalTime slot = LocalTime.of(hora, 0);
            if (!ocupadas.contains(slot)) {
                modeloHoras.addElement(slot);
            }
        }
    }

    private Set<LocalTime> obtenerHorasOcupadas(LocalDate fecha) {
        Set<LocalTime> ocupadas = new HashSet<>();
        String sql = "SELECT hora FROM citas WHERE fecha = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ocupadas.add(rs.getTime("hora").toLocalTime());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar horarios ocupados.");
            e.printStackTrace();
        }

        return ocupadas;
    }

    private void abrirFormulario() {
        LocalDate fecha = (LocalDate) cmbFechas.getSelectedItem();
        LocalTime hora = listaHoras.getSelectedValue();

        if (fecha != null && hora != null) {
            AgendaCitasGUI ventana = new AgendaCitasGUI(fecha, hora);
            ventana.setVisible(true);
            dispose(); // cerrar esta ventana
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una fecha y hora válida.");
        }
    }
}