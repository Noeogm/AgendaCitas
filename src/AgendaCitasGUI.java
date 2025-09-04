import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
public class AgendaCitasGUI extends JFrame {
    private JTextField txtFecha;
    private JTextField txtHora;

    public AgendaCitasGUI(LocalDate fecha, LocalTime hora) {
        this(); // llama al constructor original
        txtFecha.setText(fecha.toString());
        txtHora.setText(hora.toString());
    }

    public AgendaCitasGUI() {
        setTitle("Agenda de Citas");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana
        setLayout(null); // Usaremos posicionamiento manual por ahora
        setResizable(false);
        // Aquí irán los componentes
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 30, 100, 25);
        add(lblNombre);

        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(140, 30, 200, 25);
        add(txtNombre);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(30, 70, 100, 25);
        add(lblTelefono);

        JTextField txtTelefono = new JTextField();
        txtTelefono.setBounds(140, 70, 200, 25);
        add(txtTelefono);

        JLabel lblServicio = new JLabel("Servicio:");
        lblServicio.setBounds(30, 110, 100, 25);
        add(lblServicio);

        String[] servicios = {"Manicura", "Pedicura", "Uñas acrílicas", "Decoración"};
        JComboBox<String> cmbServicio = new JComboBox<>(servicios);
        cmbServicio.setBounds(140, 110, 200, 25);
        add(cmbServicio);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(30, 150, 100, 25);
        add(lblFecha);

// Aquí puedes usar JDateChooser si ya lo tienes configurado
        txtFecha = new JTextField("YYYY-MM-DD");
        txtFecha.setBounds(140, 150, 200, 25);
        add(txtFecha);


        JLabel lblHora = new JLabel("Hora:");
        lblHora.setBounds(30, 190, 100, 25);
        add(lblHora);

        txtHora = new JTextField("HH:MM");
        txtHora.setBounds(140, 190, 200, 25);
        add(txtHora);

        JButton btnRegistrar = new JButton("Registrar Cita");
        btnRegistrar.setBounds(140, 240, 150, 30);
        add(btnRegistrar);

        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String telefono = txtTelefono.getText();
            String servicio = cmbServicio.getSelectedItem().toString();
            String fechaStr = txtFecha.getText(); // formato YYYY-MM-DD
            String horaStr = txtHora.getText();   // formato HH:MM

            if (nombre.isEmpty() || telefono.isEmpty() || fechaStr.isEmpty() || horaStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor completa todos los campos.");
                return;
            }

            try {
                LocalDate fecha = LocalDate.parse(fechaStr);
                LocalTime hora = LocalTime.parse(horaStr);

                Cliente cliente = new Cliente(nombre, telefono, servicio);
                Clientedb clienteDAO = new Clientedb();
                int clienteId = clienteDAO.insertarCliente(cliente);

                if (clienteId != -1) {
                    Cita cita = new Cita(cliente, fecha, hora, "");
                    Citadb citadb = new Citadb();
                    boolean exito = citadb.insertarCita(cita);

                    if (exito) {
                        JOptionPane.showMessageDialog(null, "Cita registrada correctamente.");
                        // Aquí puedes limpiar los campos si quieres
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al registrar la cita.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error al registrar el cliente.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Formato de fecha u hora incorrecto.");
                ex.printStackTrace();
            }
        });

    }



}
