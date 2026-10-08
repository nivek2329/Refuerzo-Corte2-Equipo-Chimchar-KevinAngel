import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class RepositorioMisionMySQL implements RepositorioMision {
    private static final String INSERTAR_MISION =
            "INSERT INTO mision (id, drone_id, origen, destino, tipo_carga, estado) VALUES (?, ?, ?, ?, ?, ?)";

    private final String urlConexion;
    private final String usuario;
    private final String contrasena;

    public RepositorioMisionMySQL(String urlConexion, String usuario, String contrasena) {
        this.urlConexion = Objects.requireNonNull(urlConexion, "urlConexion no puede ser null");
        this.usuario = Objects.requireNonNull(usuario, "usuario no puede ser null");
        this.contrasena = Objects.requireNonNull(contrasena, "contrasena no puede ser null");
    }

    @Override
    public void guardar(Mision mision) {
        Objects.requireNonNull(mision, "mision no puede ser null");
        try (Connection conexion = DriverManager.getConnection(urlConexion, usuario, contrasena)) {
            insertar(conexion, mision);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar la misión " + mision.id(), e);
        }
    }

    private void insertar(Connection conexion, Mision mision) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_MISION)) {
            sentencia.setString(1, mision.id());
            sentencia.setString(2, mision.drone().id());
            sentencia.setString(3, mision.origen());
            sentencia.setString(4, mision.destino());
            sentencia.setString(5, mision.tipoCarga().name());
            sentencia.setString(6, mision.estado().name());
            sentencia.executeUpdate();
        }
    }
}
