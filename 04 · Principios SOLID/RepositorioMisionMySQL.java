import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class RepositorioMisionMySQL implements RepositorioMision {
    private final String urlConexion;

    public RepositorioMisionMySQL(String urlConexion) {
        this.urlConexion = Objects.requireNonNull(urlConexion, "urlConexion no puede ser null");
    }

    @Override
    public void guardar(Mision mision) {
        try (Connection conexion = DriverManager.getConnection(urlConexion, "root", "1234")) {
            persistir(conexion, mision);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo guardar la misión " + mision.id(), e);
        }
    }

    private void persistir(Connection conexion, Mision mision) throws SQLException {
        // insert/update real contra la tabla de misiones iría aquí
    }
}
