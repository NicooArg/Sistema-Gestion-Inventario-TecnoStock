package dao;

import conexion.ConexionBD;
import modelo.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() throws SQLException {

        List<Categoria> lista = new ArrayList<>();

        String sql = """
                SELECT id_categoria, nombre
                FROM categoria
                WHERE activo = TRUE
                ORDER BY nombre
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre"));
                lista.add(c);
            }
        }

        return lista;
    }

    public void insertar(String nombre) throws SQLException {

        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    public void actualizar(int id, String nombre) throws SQLException {

        String sql = "UPDATE categoria SET nombre=? WHERE id_categoria=?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void desactivar(int id) throws SQLException {

        String sql = "UPDATE categoria SET activo=FALSE WHERE id_categoria=?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}