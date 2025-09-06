import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Sistema de Reservas");
        setSize(800, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setBackground(new Color(223, 225, 225));


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 15, 15)); // 3 filas, espacio vertical
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBounds(250, 100, 300, 200);

        /*
        JButton btnAgendar = new JButton("Agendar Cita");
        btnAgendar.setBounds(120, 40, 150, 40);
        add(btnAgendar);

        JButton btnBuscar = new JButton("Buscar Citas");
        btnBuscar.setBounds(120, 100, 150, 40);
        add(btnBuscar);

        JButton btnModificar = new JButton("Modificar Citas");
        btnModificar.setBounds(120, 160, 150, 40);
        add(btnModificar);*/
        // Crear botones
        JButton btnAgendar = new JButton("Agendar Cita");
        JButton btnBuscar = new JButton("Buscar Cita");
        JButton btnModificar = new JButton("Modificar Cita");

        // Personalizar botones
        Font fuente = new Font("Arial", Font.BOLD, 20);
        Color colorFondo = new Color(20, 179, 113); // verde suave

        for (JButton btn : new JButton[]{btnAgendar, btnBuscar, btnModificar}) {
            btn.setFont(fuente);
            btn.setBackground(colorFondo);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            panel.add(btn);
        }




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

        add(panel);
        setVisible(true);



    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}