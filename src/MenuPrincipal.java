import javax.swing.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Sistema de Reservas");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JButton btnAgendar = new JButton("Agendar Cita");
        btnAgendar.setBounds(120, 40, 150, 40);
        add(btnAgendar);

        JButton btnBuscar = new JButton("Buscar Citas");
        btnBuscar.setBounds(120, 100, 150, 40);
        add(btnBuscar);

        JButton btnModificar = new JButton("Modificar Citas");
        btnModificar.setBounds(120, 160, 150, 40);
        add(btnModificar);

        // Acción para abrir la ventana de agendar
       /* btnAgendar.addActionListener(e -> {
            AgendaCitasGUI ventanaAgendar = new AgendaCitasGUI();
            ventanaAgendar.setVisible(true);
        });
*/
        // Acción para buscar citas
        btnBuscar.addActionListener(e -> {
            BuscarCitasGUI ventanaBuscar = new BuscarCitasGUI();
            ventanaBuscar.setVisible(true);
        });

        // Acción para modificar citas
        btnModificar.addActionListener(e -> {
            ModificarCitasGUI ventanaModificar = new ModificarCitasGUI();
            ventanaModificar.setVisible(true);
        });
        btnAgendar.addActionListener(e -> {
            SeleccionarSlotGUI selector = new SeleccionarSlotGUI();
            selector.setVisible(true);
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}