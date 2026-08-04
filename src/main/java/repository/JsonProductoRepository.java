package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Material;
import model.Producto;
import model.Tupla;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonProductoRepository implements ProductoRepository {

    private final String rutaArchivo;

    public JsonProductoRepository() {
        this("data/productos.json");
    }

    public JsonProductoRepository(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // Guardamos solo el NOMBRE del material y la cantidad requerida, no el objeto Material
    // completo. Si guardáramos el material entero acá, tendríamos dos copias de sus datos
    // (una en materiales.json y otra embebida en cada producto que lo usa) y quedarían
    // desincronizadas apenas se actualice el stock o el precio del material.
    private static class RequisitoMaterialDTO {
        String nombreMaterial;
        int cantidad;

        RequisitoMaterialDTO(String nombreMaterial, int cantidad) {
            this.nombreMaterial = nombreMaterial;
            this.cantidad = cantidad;
        }
    }

    private static class ProductoDTO {
        String nombre;
        int disponible;
        List<RequisitoMaterialDTO> requisitos;

        ProductoDTO(String nombre, int disponible, List<RequisitoMaterialDTO> requisitos) {
            this.nombre = nombre;
            this.disponible = disponible;
            this.requisitos = requisitos;
        }
    }

    @Override
    public void guardarTodos(List<Producto> productos) {
        File file = new File(rutaArchivo);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        List<ProductoDTO> dtos = new ArrayList<>();
        for (Producto p : productos) {
            List<RequisitoMaterialDTO> requisitos = new ArrayList<>();
            for (Tupla<Material, Integer> t : p.getMateriales()) {
                requisitos.add(new RequisitoMaterialDTO(t.getItem1().getNombre(), t.getItem2()));
            }
            dtos.add(new ProductoDTO(p.getNombre(), p.getDisponible(), requisitos));
        }

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(dtos, writer);
        } catch (Exception e) {
            System.out.println("Error al guardar productos: " + e.getMessage());
        }
    }

    @Override
    public List<Producto> leerTodos(Map<String, Material> materialesDisponibles) {
        File file = new File(rutaArchivo);
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<ProductoDTO>>(){}.getType();
            List<ProductoDTO> dtos = gson.fromJson(reader, tipoLista);
            if (dtos == null) return new ArrayList<>();

            List<Producto> productos = new ArrayList<>();
            for (ProductoDTO dto : dtos) {
                List<Tupla<Material, Integer>> materiales = new ArrayList<>();
                for (RequisitoMaterialDTO req : dto.requisitos) {
                    Material mat = materialesDisponibles.get(req.nombreMaterial);
                    if (mat != null) {
                        materiales.add(new Tupla<>(mat, req.cantidad));
                    } else {
                        System.out.println("Aviso: el material '" + req.nombreMaterial +
                                "' del producto '" + dto.nombre + "' ya no existe. Se omite ese requisito.");
                    }
                }
                productos.add(new Producto(dto.nombre, materiales, dto.disponible));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al leer productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
