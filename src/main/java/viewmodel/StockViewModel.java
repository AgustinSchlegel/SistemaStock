package viewmodel;

import model.Material;
import model.Producto;
import model.Sistema;
import model.Tupla;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

// Mediador entre Sistema (Model) y la futura vista Swing (View).
// La View se suscribe a estos eventos y se limita a "dibujar" lo que recibe;
// toda decisión de negocio (validaciones, formato de errores) vive acá, no en la UI.
public class StockViewModel {

    public static final String EVENTO_MATERIALES = "materiales";
    public static final String EVENTO_PRODUCTOS = "productos";
    public static final String EVENTO_ERROR = "error";

    private final Sistema sistema;
    private final PropertyChangeSupport soporte;

    public StockViewModel(Sistema sistema) {
        this.sistema = sistema;
        this.soporte = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        soporte.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        soporte.removePropertyChangeListener(listener);
    }

    // ---------------- Materiales ----------------

    public List<MaterialView> getMateriales() {
        List<MaterialView> vistas = new ArrayList<>();
        for (Material m : sistema.getMateriales()) {
            vistas.add(aVista(m));
        }
        return vistas;
    }

    public void agregarMaterial(String nombre, double precio, int cantidad) {
        String error = validarNuevoMaterial(nombre, precio, cantidad);
        if (error != null) {
            soporte.firePropertyChange(EVENTO_ERROR, null, error);
            return;
        }
        sistema.nuevoMaterial(nombre, precio, cantidad);
        soporte.firePropertyChange(EVENTO_MATERIALES, null, getMateriales());
    }

    public void actualizarPrecioMaterial(String nombre, double precio) {
        if (precio < 0) {
            soporte.firePropertyChange(EVENTO_ERROR, null, "El precio no puede ser negativo.");
            return;
        }
        sistema.actualizarMaterial(precio, nombre, false);
        soporte.firePropertyChange(EVENTO_MATERIALES, null, getMateriales());
    }

    public void actualizarCantidadMaterial(String nombre, int cantidad) {
        if (cantidad < 0) {
            soporte.firePropertyChange(EVENTO_ERROR, null, "La cantidad no puede ser negativa.");
            return;
        }
        sistema.actualizarMaterial(cantidad, nombre, true);
        soporte.firePropertyChange(EVENTO_MATERIALES, null, getMateriales());
    }

    private String validarNuevoMaterial(String nombre, double precio, int cantidad) {
        if (nombre == null || nombre.isBlank()) return "El nombre del material no puede estar vacío.";
        if (sistema.existeMaterial(nombre)) return "Ya existe un material con ese nombre.";
        if (precio < 0) return "El precio no puede ser negativo.";
        if (cantidad < 0) return "La cantidad no puede ser negativa.";
        return null;
    }

    // ---------------- Productos ----------------

    public List<ProductoView> getProductos() {
        List<ProductoView> vistas = new ArrayList<>();
        for (Producto p : sistema.getProductos()) {
            vistas.add(aVista(p));
        }
        return vistas;
    }

    public void agregarProducto(String nombre, List<RequisitoView> requisitos) {
        String error = validarNuevoProducto(nombre, requisitos);
        if (error != null) {
            soporte.firePropertyChange(EVENTO_ERROR, null, error);
            return;
        }

        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        for (RequisitoView r : requisitos) {
            materiales.add(new Tupla<>(sistema.getMaterial(r.nombreMaterial), r.cantidad));
        }
        sistema.nuevoProducto(nombre, materiales);
        soporte.firePropertyChange(EVENTO_PRODUCTOS, null, getProductos());
    }

    public void producir(String nombreProducto, int cantidad) {
        boolean ok = sistema.producirProducto(nombreProducto, cantidad);
        if (!ok) {
            soporte.firePropertyChange(EVENTO_ERROR, null,
                    "No hay stock de materiales suficiente para producir " + cantidad + " de '" + nombreProducto + "'.");
            return;
        }
        soporte.firePropertyChange(EVENTO_PRODUCTOS, null, getProductos());
        soporte.firePropertyChange(EVENTO_MATERIALES, null, getMateriales());
    }

    public void vender(String nombreProducto, int cantidad) {
        boolean ok = sistema.venderProducto(nombreProducto, cantidad);
        if (!ok) {
            soporte.firePropertyChange(EVENTO_ERROR, null,
                    "No hay stock disponible suficiente para vender " + cantidad + " de '" + nombreProducto + "'.");
            return;
        }
        soporte.firePropertyChange(EVENTO_PRODUCTOS, null, getProductos());
    }

    private String validarNuevoProducto(String nombre, List<RequisitoView> requisitos) {
        if (nombre == null || nombre.isBlank()) return "El nombre del producto no puede estar vacío.";
        if (sistema.existeProducto(nombre)) return "Ya existe un producto con ese nombre.";
        if (requisitos == null || requisitos.isEmpty()) return "El producto necesita al menos un material.";
        for (RequisitoView r : requisitos) {
            if (!sistema.existeMaterial(r.nombreMaterial)) {
                return "El material '" + r.nombreMaterial + "' no existe.";
            }
            if (r.cantidad <= 0) {
                return "La cantidad requerida de '" + r.nombreMaterial + "' debe ser mayor a 0.";
            }
        }
        return null;
    }

    // ---------------- Conversión Model -> DTO ----------------

    private MaterialView aVista(Material m) {
        return new MaterialView(m.getNombre(), m.getPrecio(), m.getCantidad(), m.getPrecioUnidad());
    }

    private ProductoView aVista(Producto p) {
        List<RequisitoView> requisitos = new ArrayList<>();
        for (Tupla<Material, Integer> t : p.getMateriales()) {
            requisitos.add(new RequisitoView(t.getItem1().getNombre(), t.getItem2()));
        }
        return new ProductoView(p.getNombre(), p.getPrecio(), p.getDisponible(), requisitos);
    }
}
