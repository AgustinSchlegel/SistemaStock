package InterfazGrafica;

import viewmodel.MaterialView;
import viewmodel.ProductoView;
import viewmodel.RequisitoView;
import viewmodel.StockViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class ProductosPanel extends JPanel {

    private final StockViewModel viewModel;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private final JTextField campoNombreProducto = new JTextField(10);
    private final JComboBox<String> comboMateriales = new JComboBox<>();
    private final JTextField campoCantidadRequerida = new JTextField(5);
    private final DefaultListModel<String> modeloRequisitos = new DefaultListModel<>();
    private final JList<String> listaRequisitos = new JList<>(modeloRequisitos);
    private final List<RequisitoView> requisitosPendientes = new ArrayList<>();

    private final JTextField campoCantidadProducirVender = new JTextField(5);

    public ProductosPanel(StockViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new DefaultTableModel(new Object[]{"Nombre", "Precio", "Stock disponible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        viewModel.addPropertyChangeListener(this::onCambio);
        refrescarTabla();
        refrescarComboMateriales();
    }

    private JPanel construirPanelInferior() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        JPanel panelAlta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAlta.setBorder(BorderFactory.createTitledBorder("Nuevo producto"));
        panelAlta.add(new JLabel("Nombre:"));
        panelAlta.add(campoNombreProducto);
        panelAlta.add(new JLabel("Material:"));
        panelAlta.add(comboMateriales);
        panelAlta.add(new JLabel("Cantidad:"));
        panelAlta.add(campoCantidadRequerida);
        JButton botonAgregarRequisito = new JButton("+ Agregar material al producto");
        botonAgregarRequisito.addActionListener(e -> agregarRequisitoPendiente());
        panelAlta.add(botonAgregarRequisito);

        JPanel panelRequisitos = new JPanel(new BorderLayout());
        panelRequisitos.setBorder(BorderFactory.createTitledBorder("Materiales del nuevo producto"));
        listaRequisitos.setVisibleRowCount(3);
        panelRequisitos.add(new JScrollPane(listaRequisitos), BorderLayout.CENTER);
        JButton botonCrear = new JButton("Crear producto");
        botonCrear.addActionListener(e -> crearProducto());
        panelRequisitos.add(botonCrear, BorderLayout.EAST);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.setBorder(BorderFactory.createTitledBorder("Producir / Vender (producto seleccionado en la tabla)"));
        panelAcciones.add(new JLabel("Cantidad:"));
        panelAcciones.add(campoCantidadProducirVender);
        JButton botonProducir = new JButton("Producir");
        botonProducir.addActionListener(e -> producir());
        panelAcciones.add(botonProducir);
        JButton botonVender = new JButton("Vender");
        botonVender.addActionListener(e -> vender());
        panelAcciones.add(botonVender);

        contenedor.add(panelAlta);
        contenedor.add(panelRequisitos);
        contenedor.add(panelAcciones);
        return contenedor;
    }

    private void agregarRequisitoPendiente() {
        String material = (String) comboMateriales.getSelectedItem();
        if (material == null) {
            mostrarError("No hay materiales cargados todavía. Agregá materiales primero en la pestaña Materiales.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(campoCantidadRequerida.getText().trim());
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor a 0.");
                return;
            }
            requisitosPendientes.add(new RequisitoView(material, cantidad));
            modeloRequisitos.addElement(material + " x " + cantidad);
            campoCantidadRequerida.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un número entero válido.");
        }
    }

    private void crearProducto() {
        String nombre = campoNombreProducto.getText().trim();
        boolean existiaAntes = viewModel.getProductos().stream().anyMatch(p -> p.nombre.equals(nombre));
        viewModel.agregarProducto(nombre, new ArrayList<>(requisitosPendientes));
        boolean existeAhora = viewModel.getProductos().stream().anyMatch(p -> p.nombre.equals(nombre));

        // Si no existía antes y ahora sí, la creación funcionó -> limpiamos el formulario.
        // Si falló, el ViewModel ya mostró el error correspondiente vía EVENTO_ERROR.
        if (!existiaAntes && existeAhora) {
            campoNombreProducto.setText("");
            requisitosPendientes.clear();
            modeloRequisitos.clear();
        }
    }

    private void producir() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            mostrarError("Seleccioná un producto de la tabla.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(campoCantidadProducirVender.getText().trim());
            viewModel.producir(nombre, cantidad);
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un número entero válido.");
        }
    }

    private void vender() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            mostrarError("Seleccioná un producto de la tabla.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(campoCantidadProducirVender.getText().trim());
            viewModel.vender(nombre, cantidad);
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un número entero válido.");
        }
    }

    private String nombreSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return null;
        return (String) modeloTabla.getValueAt(fila, 0);
    }

    private void onCambio(PropertyChangeEvent evento) {
        if (StockViewModel.EVENTO_PRODUCTOS.equals(evento.getPropertyName())) {
            SwingUtilities.invokeLater(this::refrescarTabla);
        } else if (StockViewModel.EVENTO_MATERIALES.equals(evento.getPropertyName())) {
            SwingUtilities.invokeLater(this::refrescarComboMateriales);
        } else if (StockViewModel.EVENTO_ERROR.equals(evento.getPropertyName())) {
            SwingUtilities.invokeLater(() -> mostrarError((String) evento.getNewValue()));
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<ProductoView> productos = viewModel.getProductos();
        for (ProductoView p : productos) {
            modeloTabla.addRow(new Object[]{p.nombre, p.precio, p.disponible});
        }
    }

    private void refrescarComboMateriales() {
        String seleccionActual = (String) comboMateriales.getSelectedItem();
        comboMateriales.removeAllItems();
        for (MaterialView m : viewModel.getMateriales()) {
            comboMateriales.addItem(m.nombre);
        }
        if (seleccionActual != null) comboMateriales.setSelectedItem(seleccionActual);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
