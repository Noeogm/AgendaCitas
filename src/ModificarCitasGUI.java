import javax.swing.*;

public class ModificarCitasGUI extends JFrame {

    public ModificarCitasGUI() {
        setTitle("Modificar Citas");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);


        JLabel lblInfo = new JLabel("Aquí podrás modificar citas...");
        lblInfo.setBounds(50, 50, 300, 30);
        add(lblInfo);

        // Aquí irán los componentes para buscar y editar citas
    }
}