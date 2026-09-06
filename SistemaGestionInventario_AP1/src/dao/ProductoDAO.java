package dao;

import conexion.ConexionBD;
import modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() throws SQLException {

        List<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT p.*, c.nombre AS nombre_categoria
                FROM producto p
                INNER JOIN categoria c ON p.id_categoria = c.id_categoria
                WHERE p.activo = TRUE
                ORDER BY p.nombre
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Producto p = new Producto();

                p.setIdProducto(rs.getInt("id_producto"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setStockMinimo(rs.getInt("stock_minimo"));
                p.setActivo(rs.getBoolean("activo"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setNombreCategoria(rs.getString("nombre_categoria"));

                lista.add(p);
            }
        }

        return lista;
    }

    public void insertar(Producto p) throws SQLException {

        String sql = """
                INSERT INTO producto
                (codigo, nombre, precio, stock, stock_minimo, activo, id_categoria)
                VALUES (?, ?, ?, 0, ?, TRUE, ?)
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStockMinimo());
            ps.setInt(5, p.getIdCategoria());

            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {

        String sql = """
                UPDATE producto
                SET codigo=?, nombre=?, precio=?, stock_minimo=?, id_categoria=?
                WHERE id_producto=?
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStockMinimo());
            ps.setInt(5, p.getIdCategoria());
            ps.setInt(6, p.getIdProducto());

            ps.executeUpdate();
        }
    }

    public void desactivar(int id) throws SQLException {

        String sql = "UPDATE producto SET activo=FALSE WHERE id_producto=?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Producto> listarStockBajo() throws SQLException {

        List<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT p.*, c.nombre AS nombre_categoria
                FROM producto p
                INNER JOIN categoria c ON p.id_categoria = c.id_categoria
                WHERE p.activo = TRUE
                AND p.stock <= p.stock_minimo
                ORDER BY p.stock ASC
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Producto p = new Producto();

                p.setIdProducto(rs.getInt("id_producto"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setStockMinimo(rs.getInt("stock_minimo"));
                p.setActivo(rs.getBoolean("activo"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setNombreCategoria(rs.getString("nombre_categoria"));

                lista.add(p);
            }
        }

        return lista;
    }
}