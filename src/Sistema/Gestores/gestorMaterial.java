package Sistema.Gestores;

import Sistema.material;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class gestorMaterial {
    private static final String RUTA_ARCHIVO = "src/Sistema/materiales.json";

    public static List<material> leerMateriales() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) {
            return new ArrayList<>(); // Si no existe el archivo, devolvemos una lista vacía
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            // TypeToken es OBLIGATORIO cuando leemos Listas o Colecciones con Gson
            Type tipoLista = new TypeToken<ArrayList<material>>(){}.getType();

            List<material> materiales = gson.fromJson(reader, tipoLista);

            // Si el archivo está vacío, Gson puede devolver null
            return materiales != null ? materiales : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error al leer el JSON de materiales: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void guardarMateriales(List<material> lista) {
        File file = new File(RUTA_ARCHIVO);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            // GsonBuilder.setPrettyPrinting() hace que el JSON tenga saltos de línea e indentación
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar materiales: " + e.getMessage());
        }
    }
}
