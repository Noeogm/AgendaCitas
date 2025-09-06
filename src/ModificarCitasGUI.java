import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


public class ModificarCitasGUI extends JFrame {

    private JTextField txtTelefono;
    private JComboBox<String> cmbServicio;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtnombre;



    public ModificarCitasGUI() {
        setTitle("Modificar Citas");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);




        //  buscar y editar citas en sql dependiendo la seleccion del dropdown


        String[] criterios = {"Nombre", "Teléfono"};
        JComboBox<String> cmbCriterio = new JComboBox<>(criterios);
        cmbCriterio.setBounds(30, 30, 120, 25);
        add(cmbCriterio);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setBounds(160, 30, 200, 25);
        add(txtBuscar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(380, 30, 100, 25);
        add(btnBuscar);

        // boton buscar

        btnBuscar.addActionListener(e -> {
            String criterio = cmbCriterio.getSelectedItem().toString();
            String valor = txtBuscar.getText();

            String campoSQL = switch (criterio) {
                case "Nombre" -> "nombre";
                case "Teléfono" -> "telefono";

                default -> "";
            };

            String sql = "SELECT * FROM clientes JOIN citas ON clientes.id = citas.cliente_id WHERE clientes." + campoSQL + " = ?";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, valor);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String mensaje = "📋 Cita encontrada:\n\n" +
                            "👤 Nombre: " + rs.getString("nombre") + "\n" +
                            "📞 Teléfono: " + rs.getString("telefono") + "\n" +
                            "💅 Servicio: " + rs.getString("servicio") + "\n" +
                            "📅 Fecha: " + rs.getString("fecha") + "\n" +
                            "⏰ Hora: " + rs.getString("hora");

                    JOptionPane.showMessageDialog(null, mensaje, "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ninguna cita con ese dato.", "Sin resultados", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al buscar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });






    }



}