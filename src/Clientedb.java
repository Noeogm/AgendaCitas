import java.sql.*;

public class Clientedb {

    public int insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, telefono, servicio) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, cliente.getServicio());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    cliente.setId(idGenerado);
                    return idGenerado;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar cliente");
            e.printStackTrace();
        }

        return -1;
    }
}