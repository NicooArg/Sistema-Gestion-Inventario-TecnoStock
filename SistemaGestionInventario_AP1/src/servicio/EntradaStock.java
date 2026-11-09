
package servicio;

import dao.MovimientoStockDAO;
import java.sql.SQLException;

public class EntradaStock extends OperacionStock {

    public EntradaStock(
            int idProducto, int cantidad, int idUsuario) {

        super(idProducto, cantidad, idUsuario);
    }

    @Override
    public void registrar(MovimientoStockDAO dao)
            throws SQLException {

        dao.registrarEntrada(
                getIdProducto(),
                getCantidad(),
                getIdUsuario()
        );
    }

    @Override
    public String getDescripcion() {
        return "La entrada de stock";
    }
}
