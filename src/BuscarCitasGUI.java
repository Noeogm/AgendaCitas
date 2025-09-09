import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BuscarCitasGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private int citasHoy = 0;
    private int citasMañana = 0;
    private JLabel lblContador;
    private JComboBox<String> cmbFiltro;
    private JTextField txtBuscar;

    public BuscarCitasGUI() {
        setTitle("Reservas Programadas");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        // Panel superior con información y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout());

        // Panel de información (izquierda)
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        lblContador = new JLabel("Citas para hoy: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 14));
        lblContador.setForeground(new Color(0, 128, 0));

        JLabel lblMañana = new JLabel("Citas para mañana: 0");
        lblMañana.setFont(new Font("Arial", Font.BOLD, 14));
        lblMañana.setForeground(new Color(0, 100, 200));

        panelInfo.add(lblContador);
        panelInfo.add(lblMañana);
        panelSuperior.add(panelInfo, BorderLayout.WEST);

        // Panel de filtros (centro)
        JPanel panelFiltros = new JPanel(new FlowLayout());
        panelFiltros.add(new JLabel("Filtrar por:"));

        String[] filtros = {"Todas las citas", "Solo hoy", "Solo mañana", "Esta semana", "Próximas"};
        cmbFiltro = new JComboBox<>(filtros);
        panelFiltros.add(cmbFiltro);

        panelFiltros.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        panelFiltros.add(txtBuscar);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltro());
        panelFiltros.add(btnFiltrar);

        panelSuperior.add(panelFiltros, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla con modelo mejorado
        String[] columnas = {"Nombre", "Teléfono", "Servicio", "Fecha", "Hora", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        tabla = new JTable(modelo);
        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Panel inferior con botones y reloj
        JPanel panelInferior = new JPanel(new BorderLayout());

        // Botones (izquierda)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> {
            modelo.setRowCount(0);
            cargarDatos();
        });

        JButton btnExportar = new JButton("📊 Exportar");
        btnExportar.addActionListener(e -> exportarDatos());

        JButton btnDetalles = new JButton("👁️ Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetalles());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnDetalles);

        panelInferior.add(panelBotones, BorderLayout.WEST);

        // Reloj (derecha)
        JLabel lblReloj = new JLabel();
        lblReloj.setFont(new Font("Arial", Font.BOLD, 12));
        lblReloj.setHorizontalAlignment(SwingConstants.RIGHT);
        panelInferior.add(lblReloj, BorderLayout.EAST);

        Timer timer = new Timer(1000, e -> {
            String fechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .format(java.time.LocalDateTime.now());
            lblReloj.setText("Hora Actual: " + fechaHora + " ");
        });
        timer.start();

        add(panelInferior, BorderLayout.SOUTH);

        // Mouse listener para doble clic
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    mostrarDetalles();
                }
            }
        });
    }

    private void configurarTabla() {
        // Configurar anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120); // Teléfono
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Servicio
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);  // Hora
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado

        // Renderer personalizado para colorear filas según la fecha
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String fechaStr = table.getValueAt(row, 3).toString();
                    LocalDate fechaCita = LocalDate.parse(fechaStr.substring(0, 10));
                    LocalDate hoy = LocalDate.now();

                    if (fechaCita.equals(hoy)) {
                        c.setBackground(new Color(144, 238, 144)); // Verde claro para hoy
                    } else if (fechaCita.equals(hoy.plusDays(1))) {
                        c.setBackground(new Color(173, 216, 230)); // Azul claro para mañana
                    } else if (fechaCita.isBefore(hoy)) {
                        c.setBackground(new Color(255, 182, 193)); // Rosa claro para pasadas
                    } else {
                        c.setBackground(Color.WHITE); // Blanco para futuras
                    }
                }

                return c;
            }
        };

        // Aplicar renderer a todas las columnas
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Configurar selección
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
    }

    private void cargarDatos() {
        String sql = """
            SELECT 
                cl.nombre,
                cl.telefono,
                cl.servicio,
                c.fecha,
                c.hora
            FROM citas c
            JOIN clientes cl ON c.cliente_id = cl.id
            ORDER BY c.fecha, c.hora
        """;

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            LocalDate hoy = LocalDate.now();
            LocalDate mañana = hoy.plusDays(1);
            citasHoy = 0;
            citasMañana = 0;

            while (rs.next()) {
                LocalDate fechaCita = rs.getDate("fecha").toLocalDate();
                LocalTime horaCita = rs.getTime("hora").toLocalTime();

                // Contar citas
                if (fechaCita.equals(hoy)) {
                    citasHoy++;
                } else if (fechaCita.equals(mañana)) {
                    citasMañana++;
                }

                // Determinar estado
                String estado = determinarEstado(fechaCita, horaCita);

                Object[] fila = {
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("servicio"),
                        rs.getDate("fecha"),
                        rs.getTime("hora"),
                        estado
                };
                modelo.addRow(fila);
            }

            // Actualizar contadores
            actualizarContadores();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String determinarEstado(LocalDate fechaCita, LocalTime horaCita) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahoraHora = LocalTime.now();

        if (fechaCita.isBefore(hoy)) {
            return "Pasada";
        } else if (fechaCita.equals(hoy)) {
            if (horaCita.isBefore(ahoraHora)) {
                return "Pasada";
            } else if (horaCita.minusHours(1).isBefore(ahoraHora)) {
                return "Próxima";
            } else {
                return "Hoy";
            }
        } else if (fechaCita.equals(hoy.plusDays(1))) {
            return "Mañana";
        } else {
            return "Futura";
        }
    }

    private void actualizarContadores() {
        lblContador.setText("Citas para hoy: " + citasHoy);
        // Actualizar también el label de mañana si lo tienes
        Component[] components = ((JPanel) getContentPane().getComponent(0)).getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel panelInfo = (JPanel) components[0];
            if (panelInfo.getComponentCount() > 1) {
                JLabel lblMañana = (JLabel) panelInfo.getComponent(1);
                lblMañana.setText("Citas para mañana: " + citasMañana);
            }
        }
    }

    private void aplicarFiltro() {
        String filtroSeleccionado = cmbFiltro.getSelectedItem().toString();
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();

        // Limpiar tabla
        modelo.setRowCount(0);

        String sql = """
            SELECT 
                cl.nombre,
                cl.telefono,
                cl.servicio,
                c.fecha,
                c.hora
            FROM citas c
            JOIN clientes cl ON c.cliente_id = cl.id
            WHERE 1=1
        """;

        // Agregar filtros de fecha
        LocalDate hoy = LocalDate.now();
        switch (filtroSeleccionado) {
            case "Solo hoy":
                sql += " AND c.fecha = '" + hoy + "'";
                break;
            case "Solo mañana":
                sql += " AND c.fecha = '" + hoy.plusDays(1) + "'";
                break;
            case "Esta semana":
                sql += " AND c.fecha BETWEEN '" + hoy + "' AND '" + hoy.plusDays(7) + "'";
                break;
            case "Próximas":
                sql += " AND c.fecha >= '" + hoy + "'";
                break;
        }

        // Agregar filtro de búsqueda
        if (!textoBusqueda.isEmpty()) {
            sql += " AND (LOWER(cl.nombre) LIKE '%" + textoBusqueda + "%' OR " +
                    "LOWER(cl.servicio) LIKE '%" + textoBusqueda + "%' OR " +
                    "cl.telefono LIKE '%" + textoBusqueda + "%')";
        }

        sql += " ORDER BY c.fecha, c.hora";

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate fechaCita = rs.getDate("fecha").toLocalDate();
                LocalTime horaCita = rs.getTime("hora").toLocalTime();
                String estado = determinarEstado(fechaCita, horaCita);

                Object[] fila = {
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("servicio"),
                        rs.getDate("fecha"),
                        rs.getTime("hora"),
                        estado
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar citas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarDetalles() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            String mensaje = "📋 Detalles de la reserva:\n\n" +
                    "👤 Nombre: " + modelo.getValueAt(fila, 0) + "\n" +
                    "📞 Teléfono: " + modelo.getValueAt(fila, 1) + "\n" +
                    "💅 Servicio: " + modelo.getValueAt(fila, 2) + "\n" +
                    "📅 Fecha: " + modelo.getValueAt(fila, 3) + "\n" +
                    "⏰ Hora: " + modelo.getValueAt(fila, 4) + "\n" +
                    "🏷️ Estado: " + modelo.getValueAt(fila, 5);
            JOptionPane.showMessageDialog(this, mensaje, "Reserva seleccionada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una cita para ver los detalles.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportarDatos() {
        // Funcionalidad básica de exportar - puedes expandirla
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre,Teléfono,Servicio,Fecha,Hora,Estado\n");

        for (int i = 0; i < modelo.getRowCount(); i++) {
            for (int j = 0; j < modelo.getColumnCount(); j++) {
                sb.append(modelo.getValueAt(i, j));
                if (j < modelo.getColumnCount() - 1) sb.append(",");
            }
            sb.append("\n");
        }

        JOptionPane.showMessageDialog(this, "Datos listos para exportar:\n" +
                modelo.getRowCount() + " citas encontradas.", "Exportar", JOptionPane.INFORMATION_MESSAGE);
    }
}