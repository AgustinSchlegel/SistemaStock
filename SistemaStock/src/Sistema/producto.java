package Sistema;

import auxiliares.tupla;

public class producto {
    private String nombre;
    private double precio;
    private tupla <material, Integer>[] materiales;
    private int disponible;

    public producto(String nombre, tupla <material, Integer>[] materiales) {
        this.nombre = nombre;
        this.materiales =  materiales;
    }

    public int produccionPosible(){
        if (materiales == null){
            return 0;
        }

        int produccionPosible = Integer.MAX_VALUE;

        for (tupla<material, Integer> tupla : this.materiales) {
            if (tupla != null && tupla.getItem1() != null && tupla.getItem2() >0) {
                material material =tupla.getItem1();
                int cantidad = tupla.getItem2();
                int cantidadPosible = material.getCantidad()/cantidad;
                if (produccionPosible>cantidadPosible){
                    produccionPosible = cantidadPosible;
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

        for (tupla<material, Integer> tupla : this.materiales) {
            if (tupla!=null || tupla.getItem1()==null){
                material material =tupla.getItem1();
                int cantidad = tupla.getItem2();
                precio += cantidad*material.getPrecioUnidad();
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


}
