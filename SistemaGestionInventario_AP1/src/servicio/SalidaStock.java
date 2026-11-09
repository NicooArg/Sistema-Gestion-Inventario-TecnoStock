
package servicio;

import dao.MovimientoStockDAO;
import java.sql.SQLException;

public class SalidaStock extends OperacionStock {

    public SalidaStock(
            int idProducto, int cantidad, int idUsuario) {

        super(idProducto, cantidad, idUsuario);
    }

    @Override
    public void registrar(MovimientoStockDAO dao)
            throws SQLException {

        dao.registrarSalida(
                getIdProducto(),
                getCantidad(),
                getIdUsuario()
        );
    }

    @Override
    public String getDescripcion() {
        return "La salida de stock";
    }
}
