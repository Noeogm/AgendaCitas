import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class Citadb {

    public boolean insertarCita(Cita cita) {
        String sql = "INSERT INTO citas (cliente_id, fecha, hora, notas) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cita.getCliente().getId());
            stmt.setDate(2, Date.valueOf(cita.getFecha()));
            stmt.setTime(3, Time.valueOf(cita.getHora()));
            stmt.setString(4, cita.getNotas());

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar cita");
            e.printStackTrace();
            return false;
        }
    }
}