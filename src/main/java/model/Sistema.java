package model;

import repository.MaterialRepository;
import repository.ProductoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sistema {

    private final HashMap<String, Material> materiales;
    private final HashMap<String, Producto> productos;
    private final MaterialRepository materialRepository;
    private final ProductoRepository productoRepository;

    // Inyección de dependencias: Sistema no sabe (ni le importa) si los datos vienen de
    // un JSON, una base de datos o una lista en memoria. Solo conoce las interfaces.
    // Esto permite testear toda la lógica de negocio sin tocar el disco.
    public Sistema(MaterialRepository materialRepository, ProductoRepository productoRepository) {
        this.materialRepository = materialRepository;
        this.productoRepository = productoRepository;
        this.materiales = new HashMap<>();
        this.productos = new HashMap<>();
        cargarDatos();
    }

    private void cargarDatos() {
        for (Material m : materialRepository.leerTodos()) {
            this.materiales.put(m.getNombre(), m);
        }
        for (Producto p : productoRepository.leerTodos(this.materiales)) {
            this.productos.put(p.getNombre(), p);
        }
    }

    private void guardarTodo() {
        materialRepository.guardarTodos(new ArrayList<>(this.materiales.values()));
        productoRepository.guardarTodos(new ArrayList<>(this.productos.values()));
    }

    public void nuevoMaterial(String nombre, double precio, int cantidad) {
        Material material = new Material(nombre, precio, cantidad);
        this.materiales.put(nombre, material);
        guardarTodo();
    }

    public void nuevoProducto(String nombre, List<Tupla<Material, Integer>> materiales) {
        Producto producto = new Producto(nombre, materiales);
        this.productos.put(nombre, producto);
        guardarTodo();
    }

    public <T> void actualizarProducto(String producto, T valorActualizar, boolean dato) {
        if (this.productos.containsKey(producto)) {
            if (dato) {
                productos.get(producto).actualizarNombre((String) valorActualizar);
            } else {
                productos.get(producto).actualizarMateriales((List<Tupla<Material, Integer>>) valorActualizar);
            }
            guardarTodo();
        }
    }

    public <T extends Number> void actualizarMaterial(T datoAActualizar, String nombre, boolean dato) {
        if (this.materiales.containsKey(nombre)) {
            if (dato) {
                materiales.get(nombre).actualizarCant((Integer) datoAActualizar);
            } else {
                materiales.get(nombre).actualizarPrecio((Double) datoAActualizar);
            }
            guardarTodo();
        }
    }

    public boolean producirProducto(String nombreProducto, int cantidad) {
        if (!this.productos.containsKey(nombreProducto)) return false;
        boolean resultado = productos.get(nombreProducto).producir(cantidad);
        if (resultado) guardarTodo(); // el stock de materiales también cambió
        return resultado;
    }

    public boolean venderProducto(String nombreProducto, int cantidad) {
        if (!this.productos.containsKey(nombreProducto)) return false;
        boolean resultado = productos.get(nombreProducto).venderProducto(cantidad);
        if (resultado) guardarTodo();
        return resultado;
    }

    public double calcularPrecioProducto(String nombreProducto) {
        if (this.productos.containsKey(nombreProducto)) {
            productos.get(nombreProducto).calcularPrecio();
            return productos.get(nombreProducto).getPrecio();
        }
        return -1;
    }

    public double calcularPrecioMaterial(String nombreMaterial) {
        if (this.materiales.containsKey(nombreMaterial)) {
            return materiales.get(nombreMaterial).getPrecioUnidad();
        }
        return -1;
    }

    public Producto simularProducto(String nombre, List<Tupla<Material, Integer>> materiales) {
        Producto producto = new Producto(nombre, materiales);
        producto.produccionPosible();
        producto.getPrecio();
        return producto;
    }

    public ArrayList<String> mostrarMateriales() {
        ArrayList<String> materiales = new ArrayList<>();
        for (Material material : this.materiales.values()) {
            materiales.add(material.toString());
        }
        return materiales;
    }

    public ArrayList<String> mostrarProductos() {
        ArrayList<String> productos = new ArrayList<>();
        for (Producto producto : this.productos.values()) {
            productos.add(producto.toString());
        }
        return productos;
    }

    public Material getMaterial(String nombre) {
        return this.materiales.get(nombre);
    }

    public Producto getProducto(String nombre) {
        return this.productos.get(nombre);
    }

    public List<Material> getMateriales() {
        return new ArrayList<>(this.materiales.values());
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(this.productos.values());
    }

    public boolean existeMaterial(String nombre) {
        return this.materiales.containsKey(nombre);
    }

    public boolean existeProducto(String nombre) {
        return this.productos.containsKey(nombre);
    }

}
