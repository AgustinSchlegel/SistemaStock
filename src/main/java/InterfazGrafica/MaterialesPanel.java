package InterfazGrafica;

import viewmodel.MaterialView;
import viewmodel.StockViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.List;

// View de MVVM: solo dibuja lo que le da el ViewModel y le delega toda decisión de
// negocio. No conoce las clases de model (Material, Producto), solo los DTOs de viewmodel.
public class MaterialesPanel extends JPanel {

    private final StockViewModel viewModel;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private final JTextField campoNombre = new JTextField(10);
    private final JTextField campoPrecio = new JTextField(6);
    private final JTextField campoCantidad = new JTextField(6);

    private final JTextField campoNuevaCantidad = new JTextField(6);
    private final JTextField campoNuevoPrecio = new JTextField(6);

    public MaterialesPanel(StockViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new DefaultTableModel(new Object[]{"Nombre", "Precio total", "Cantidad", "Precio/unidad"}, 0) {
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
    }

    private JPanel construirPanelInferior() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        JPanel panelAlta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAlta.setBorder(BorderFactory.createTitledBorder("Nuevo material"));
        panelAlta.add(new JLabel("Nombre:"));
        panelAlta.add(campoNombre);
        panelAlta.add(new JLabel("Precio total:"));
        panelAlta.add(campoPrecio);
        panelAlta.add(new JLabel("Cantidad:"));
        panelAlta.add(campoCantidad);
        JButton botonAgregar = new JButton("Agregar material");
        botonAgregar.addActionListener(e -> agregarMaterial());
        panelAlta.add(botonAgregar);

        JPanel panelActualizar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelActualizar.setBorder(BorderFactory.createTitledBorder("Actualizar material seleccionado en la tabla"));
        panelActualizar.add(new JLabel("Nueva cantidad:"));
        panelActualizar.add(campoNuevaCantidad);
        JButton botonActualizarCantidad = new JButton("Actualizar cantidad");
        botonActualizarCantidad.addActionListener(e -> actualizarCantidad());
        panelActualizar.add(botonActualizarCantidad);

        panelActualizar.add(new JLabel("Nuevo precio total:"));
        panelActualizar.add(campoNuevoPrecio);
        JButton botonActualizarPrecio = new JButton("Actualizar precio");
        botonActualizarPrecio.addActionListener(e -> actualizarPrecio());
        panelActualizar.add(botonActualizarPrecio);

        contenedor.add(panelAlta);
        contenedor.add(panelActualizar);
        return contenedor;
    }

    private void agregarMaterial() {
        try {
            String nombre = campoNombre.getText().trim();
            double precio = Double.parseDouble(campoPrecio.getText().trim());
            int cantidad = Integer.parseInt(campoCantidad.getText().trim());
            viewModel.agregarMaterial(nombre, precio, cantidad);
            campoNombre.setText("");
            campoPrecio.setText("");
            campoCantidad.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("Precio y cantidad deben ser números válidos.");
        }
    }

    private void actualizarCantidad() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            mostrarError("Seleccioná un material de la tabla.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(campoNuevaCantidad.getText().trim());
            viewModel.actualizarCantidadMaterial(nombre, cantidad);
            campoNuevaCantidad.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un número entero válido.");
        }
    }

    private void actualizarPrecio() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            mostrarError("Seleccioná un material de la tabla.");
            return;
        }
        try {
            double precio = Double.parseDouble(campoNuevoPrecio.getText().trim());
            viewModel.actualizarPrecioMaterial(nombre, precio);
            campoNuevoPrecio.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("El precio debe ser un número válido.");
        }
    }

    private String nombreSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return null;
        return (String) modeloTabla.getValueAt(fila, 0);
    }

    private void onCambio(PropertyChangeEvent evento) {
        if (StockViewModel.EVENTO_MATERIALES.equals(evento.getPropertyName())) {
            SwingUtilities.invokeLater(this::refrescarTabla);
        } else if (StockViewModel.EVENTO_ERROR.equals(evento.getPropertyName())) {
            SwingUtilities.invokeLater(() -> mostrarError((String) evento.getNewValue()));
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<MaterialView> materiales = viewModel.getMateriales();
        for (MaterialView m : materiales) {
            modeloTabla.addRow(new Object[]{m.nombre, m.precio, m.cantidad, m.precioUnidad});
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
