
package servicio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio encargado de guardar y recuperar el historial
 * de movimientos mediante archivos CSV.
 */
public class ArchivoMovimientosServicio {

    // Arreglo que define las columnas del archivo.
    private static final String[] ENCABEZADOS = {
        "ID", "Tipo", "Cantidad", "Fecha", "Producto", "Usuario"
    };

    /**
     * Exporta los movimientos a un archivo CSV.
     */
    public void exportar(File archivo, List<String[]> movimientos)
            throws IOException {

        if (archivo == null) {
            throw new IOException("No se selecciono un archivo.");
        }

        if (movimientos == null) {
            throw new IOException("No hay datos para exportar.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                archivo.toPath(), StandardCharsets.UTF_8)) {

            writer.write(String.join(",", ENCABEZADOS));
            writer.newLine();

            for (String[] fila : movimientos) {

                if (fila == null || fila.length != ENCABEZADOS.length) {
                    throw new IOException(
                            "Se encontro un registro con formato incorrecto.");
                }

                for (int i = 0; i < fila.length; i++) {

                    if (i > 0) {
                        writer.write(",");
                    }

                    writer.write(escaparCampo(fila[i]));
                }

                writer.newLine();
            }
        }
    }

    /**
     * Recupera los movimientos almacenados en un archivo CSV.
     */
    public ArrayList<String[]> importar(File archivo)
            throws IOException {

        if (archivo == null || !archivo.isFile()) {
            throw new IOException("El archivo seleccionado no existe.");
        }

        ArrayList<String[]> movimientos = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                archivo.toPath(), StandardCharsets.UTF_8)) {

            String encabezado = reader.readLine();

            if (encabezado == null
                    || !Arrays.equals(
                            parsearLinea(encabezado), ENCABEZADOS)) {

                throw new IOException(
                        "El archivo no tiene el formato de movimientos esperado.");
            }

            String linea;
            int numeroLinea = 1;

            while ((linea = reader.readLine()) != null) {

                numeroLinea++;

                if (linea.isBlank()) {
                    continue;
                }

                String[] fila = parsearLinea(linea);

                if (fila.length != ENCABEZADOS.length) {
                    throw new IOException(
                            "Formato incorrecto en la linea " + numeroLinea + ".");
                }

                movimientos.add(fila);
            }
        }

        return movimientos;
    }

    /**
     * Prepara un campo para su almacenamiento en formato CSV.
     */
    private String escaparCampo(String valor) {

        if (valor == null) {
            return "";
        }

        if (valor.contains(",") || valor.contains("\"")
                || valor.contains("\n") || valor.contains("\r")) {

            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }

        return valor;
    }

    /**
     * Separa los campos de una línea CSV respetando las comillas.
     */
    private String[] parsearLinea(String linea) throws IOException {

        ArrayList<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroDeComillas = false;

        for (int i = 0; i < linea.length(); i++) {

            char actual = linea.charAt(i);

            if (actual == '"') {

                if (dentroDeComillas
                        && i + 1 < linea.length()
                        && linea.charAt(i + 1) == '"') {

                    campo.append('"');
                    i++;

                } else {
                    dentroDeComillas = !dentroDeComillas;
                }

            } else if (actual == ',' && !dentroDeComillas) {

                campos.add(campo.toString());
                campo.setLength(0);

            } else {
                campo.append(actual);
            }
        }

        if (dentroDeComillas) {
            throw new IOException("El archivo contiene comillas sin cerrar.");
        }

        campos.add(campo.toString());

        return campos.toArray(new String[0]);
    }
}

