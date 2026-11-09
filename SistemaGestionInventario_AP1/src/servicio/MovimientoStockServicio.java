
package servicio;

import dao.MovimientoStockDAO;
import java.sql.SQLException;
import java.util.List;

public class MovimientoStockServicio {

    private final MovimientoStockDAO movimientoDAO;

    public MovimientoStockServicio() {
        this.movimientoDAO = new MovimientoStockDAO();
    }

    public void registrar(OperacionStock operacion)
            throws SQLException {

        if (operacion == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una operación de stock.");
        }

        operacion.registrar(movimientoDAO);
    }

    public List<String[]> listar() throws SQLException {
        return movimientoDAO.listar();
    }
}
