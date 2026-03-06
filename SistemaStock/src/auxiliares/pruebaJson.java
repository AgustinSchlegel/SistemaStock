package auxiliares;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import InterfazGrafica.ventanaPrincipal;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class pruebaJson {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String valores=darValores();
        cargarValores(valores);
    }

    private static String darValores() {
        System.out.println("cargar valor 1");

        System.out.println("cargar Valor 2");
    }

    private static void cargarValores(String valor){
        try {
            FileReader reader = new FileReader("src/auxiliares/prueba.json");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
