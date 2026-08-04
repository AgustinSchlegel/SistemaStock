package viewmodel;

// Representa "necesito X cantidad del material Y" sin que la View conozca model.Material.
public class RequisitoView {
    public final String nombreMaterial;
    public final int cantidad;

    public RequisitoView(String nombreMaterial, int cantidad) {
        this.nombreMaterial = nombreMaterial;
        this.cantidad = cantidad;
    }
}
