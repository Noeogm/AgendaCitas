import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Sistema de Reservas");

        Image icono = Toolkit.getDefaultToolkit().getImage("estilo/MINIATURA.png");
        setIconImage(icono);

        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        // Fondo personalizado
        getContentPane().setBackground(new Color(245, 245, 245));


        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // margen interno



        // Crear botones
        JButton btnAgendar = new JButton("Agendar Cita");
        JButton btnBuscar = new JButton("Mostrar Reservas");
        JButton btnModificar = new JButton("Modificar Cita");


        for (JButton btn : new JButton[]{btnAgendar, btnBuscar, btnModificar}) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 80));
            btn.setBackground(new Color(0, 153, 102)); // verde elegante
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            panelBotones.add(btn);
            panelBotones.add(Box.createRigidArea(new Dimension(0, 20))); // espacio entre botones
        }

        add(panelBotones, BorderLayout.WEST);

        JPanel panelicono = new JPanel();
        panelicono.setLayout(new BoxLayout(panelicono, BoxLayout.Y_AXIS));
        panelicono.setBackground(new Color(245, 245, 245));

        ImageIcon imagenSalon = new ImageIcon("estilo/logo.png");

        JLabel lblImagen = new JLabel(imagenSalon);
        lblImagen.setAlignmentX(Component.RIGHT_ALIGNMENT);


        panelicono.add(lblImagen);



        add(panelBotones,BorderLayout.WEST);
        add(panelicono, BorderLayout.EAST);



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