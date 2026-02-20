package Sistema;

import auxiliares.tupla;

import java.util.List;

public class producto {
    private String nombre;
    private double precio;
    private List<tupla <material, Integer>> materiales;
    private int disponible;

    public producto(String nombre, List<tupla <material, Integer>> materiales) {
        this.nombre = nombre;
        this.materiales =  materiales;
        calcularPrecio();
    }

    public int produccionPosible(){

        if (materiales == null || materiales.isEmpty()) return 0;

        int produccionPosible = Integer.MAX_VALUE;


        for (tupla<material, Integer> tupla : this.materiales) {

                if (tupla != null && tupla.getItem1() != null) {
                    material mat = tupla.getItem1();
                    int cantidadNecesaria = tupla.getItem2();

                    if (cantidadNecesaria > 0) {
                        int unidadesQuePuedoHacer = mat.getCantidad() / cantidadNecesaria;
                        if (unidadesQuePuedoHacer < produccionPosible) {
                            produccionPosible = unidadesQuePuedoHacer;
                        }
                    }
                }


        }

        return (produccionPosible == Integer.MAX_VALUE) ? 0 : produccionPosible;
    }

    public void calcularPrecio(){
        if (materiales==null){
            this.precio = 0;
            return;
        }

        double precio = 0;

        for (tupla<material, Integer> tupla : this.materiales){
                if (tupla!=null || tupla.getItem1()==null) {
                    material material = tupla.getItem1();
                    int cantidad = tupla.getItem2();
                    precio += cantidad * material.getPrecioUnidad();
                }

        }


        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    public String ToString(){
        return "";
    }

    public void actualizarMateriales(List<tupla <material, Integer>> materiales) {
        this.materiales = materiales;
    }

    public void actualizarNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean producir(int produccion){
        if (produccionPosible()>produccion){
            for (tupla<material, Integer> tupla : materiales){

                    int cantidadActual = tupla.getItem1().getCantidad();
                    int cantidadAProducir= tupla.getItem2()*produccion;
                    tupla.getItem1().actualizarCant(cantidadActual-cantidadAProducir);
            }

            this.disponible = produccion;
            return true;
        }
        return false;
    }

    public boolean venderProducto(int venta) {
        if (disponible>venta){
            this.disponible=-venta;
            return true;
        }
        return false;
    }

}
