import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BuscarCitasGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    public BuscarCitasGUI() {
        setTitle("Buscar Citas");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);


        modelo = new DefaultTableModel();
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Servicio");
        modelo.addColumn("Fecha");
        modelo.addColumn("Hora");
        modelo.addColumn("Notas");

        cargarDatos();
    }

    private void cargarDatos() {
        String sql = """
            SELECT 
                c.id AS cita_id,
                cl.nombre,
                cl.telefono,
                cl.servicio,
                c.fecha,
                c.hora,
                c.notas
            FROM citas c
            JOIN clientes cl ON c.cliente_id = cl.id
            ORDER BY c.fecha, c.hora
        """;


        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("cita_id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("servicio"),
                        rs.getDate("fecha"),
                        rs.getTime("hora"),
                        rs.getString("notas")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas.");
            e.printStackTrace();
        }
    }
}