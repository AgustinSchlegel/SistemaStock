package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Material;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonMaterialRepository implements MaterialRepository {

    private final String rutaArchivo;

    public JsonMaterialRepository() {
        this("data/materiales.json");
    }

    // Permite inyectar una ruta distinta (clave para poder testear esta clase sin
    // pisar el archivo real que usa la aplicación).
    public JsonMaterialRepository(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Material> leerTodos() {
        File file = new File(rutaArchivo);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Material>>(){}.getType();
            List<Material> materiales = gson.fromJson(reader, tipoLista);
            return materiales != null ? materiales : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error al leer el JSON de materiales: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void guardarTodos(List<Material> materiales) {
        File file = new File(rutaArchivo);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(materiales, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar materiales: " + e.getMessage());
        }
    }
}
