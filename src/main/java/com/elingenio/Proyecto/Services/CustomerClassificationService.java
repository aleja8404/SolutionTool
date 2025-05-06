package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.CustomerData;
import com.elingenio.Proyecto.Repository.CustomerDataRepository;
import com.elingenio.Proyecto.Services.dto.PredictionCustomer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CustomerClassificationService {

    private static final Logger LOGGER = Logger.getLogger(CustomerClassificationService.class.getName());
    private Classifier classifier;
    private Instances dataStructure;
    private final CustomerDataRepository repository;

    public CustomerClassificationService(CustomerDataRepository repository) {
        this.repository = repository;
        try {
            ClassPathResource modelResource = new ClassPathResource("customer_classification.model");
            classifier = (Classifier) weka.core.SerializationHelper.read(modelResource.getInputStream());
            LOGGER.info("Modelo de clasificación de clientes cargado exitosamente.");

            ClassPathResource arffResource = new ClassPathResource("clasificacion_clientes.arff");
            DataSource source = new DataSource(arffResource.getInputStream());
            dataStructure = source.getDataSet();
            dataStructure.setClassIndex(dataStructure.numAttributes() - 1);
            LOGGER.info("Estructura clasificacion_clientes.arff cargada exitosamente.");
        } catch (Exception e) {
            LOGGER.severe("Error al inicializar CustomerClassificationService: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar el servicio de clasificación", e);
        }
    }

    public PredictionCustomer classifyAndSave(CustomerData datos) throws Exception {
        LOGGER.info("Datos recibidos para clasificación: " + datos.toString());

        Instance instance = new DenseInstance(6);
        instance.setDataset(dataStructure);
        instance.setValue(0, datos.getEdad());
        instance.setValue(1, datos.getFrecuenciaCompra());
        instance.setValue(2, datos.getValorTotalCompra());
        instance.setValue(3, datos.getUltimaCompra());
        instance.setValue(4, datos.getMetodoPago());

        double predictionValue = classifier.classifyInstance(instance);
        String classification = dataStructure.classAttribute().value((int) predictionValue);

        double[] probabilities = classifier.distributionForInstance(instance);
        double confidence = probabilities[(int) predictionValue];
        double confidencePercentageValue = confidence * 100; // Store as a double (e.g., 90.5)
        DecimalFormat df = new DecimalFormat("#.#");
        String confidencePercentage = df.format(confidencePercentageValue) + "%";

        // Set both categoriaCliente and confianza in the CustomerData object
        datos.setCategoriaCliente(classification);
        datos.setConfianza(confidencePercentageValue); // Store the numeric value (e.g., 90.5)

        CustomerData savedData = repository.save(datos);
        LOGGER.info("Datos guardados en la base de datos: " + savedData.toString());

        return new PredictionCustomer(classification, confidencePercentage);
    }

    public Page<CustomerData> getFilteredPredictions(
            Integer edadMin, Integer edadMax,
            Integer frecuenciaCompraMin, Integer frecuenciaCompraMax,
            Double valorTotalCompraMin, Double valorTotalCompraMax,
            Integer ultimaCompraMin, Integer ultimaCompraMax,
            String metodoPago, String categoriaCliente,
            Pageable pageable) {
        Page<CustomerData> result = repository.findByFilters(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente,
                pageable);
        LOGGER.info("Datos obtenidos con filtros - Total elementos: " + result.getTotalElements() +
                ", Página: " + result.getNumber() + ", Tamaño: " + result.getSize());
        return result;
    }

    public List<CustomerData> getAllPredictions() {
        List<CustomerData> allData = repository.findAll();
        LOGGER.info("Total de datos obtenidos con getAllPredictions: " + allData.size());
        return allData;
    }

    public List<String> getDistinctMetodoPago() {
        List<String> methods = repository.findDistinctMetodoPago();
        LOGGER.info("Métodos de pago distintos: " + methods);
        return methods;
    }

    public List<String> getDistinctCategoriaCliente() {
        List<String> categories = repository.findDistinctCategoriaCliente();
        LOGGER.info("Categorías de cliente distintas: " + categories);
        return categories;
    }

    public Map<String, Long> getCategoriaClienteCounts(
            Integer edadMin, Integer edadMax,
            Integer frecuenciaCompraMin, Integer frecuenciaCompraMax,
            Double valorTotalCompraMin, Double valorTotalCompraMax,
            Integer ultimaCompraMin, Integer ultimaCompraMax,
            String metodoPago, String categoriaCliente) {

        // Validar parámetros antes de ejecutar la consulta
        if (todosParametrosSonNulos(edadMin, edadMax, frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax, ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente)) {
            // Si no hay filtros, devolver todos los datos
            return repository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            CustomerData::getCategoriaCliente,
                            Collectors.counting()
                    ));
        }

        Page<CustomerData> filteredData = repository.findByFilters(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente,
                Pageable.unpaged());

        // Manejar caso cuando no hay resultados
        if (filteredData.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> counts = filteredData.getContent().stream()
                .filter(data -> data.getCategoriaCliente() != null) // Filtrar nulos
                .collect(Collectors.groupingBy(
                        CustomerData::getCategoriaCliente,
                        Collectors.counting()
                ));

        LOGGER.info("Conteo de categorías de cliente (filtrado): " + counts);
        return counts;
    }

    private boolean todosParametrosSonNulos(Object... params) {
        return Arrays.stream(params).allMatch(Objects::isNull);
    }

    public Map<String, Long> getMetodoPagoCounts(
            Integer edadMin, Integer edadMax,
            Integer frecuenciaCompraMin, Integer frecuenciaCompraMax,
            Double valorTotalCompraMin, Double valorTotalCompraMax,
            Integer ultimaCompraMin, Integer ultimaCompraMax,
            String metodoPago, String categoriaCliente) {
        Page<CustomerData> filteredData = repository.findByFilters(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente,
                Pageable.unpaged());

        Map<String, Long> counts = filteredData.getContent().stream()
                .collect(Collectors.groupingBy(CustomerData::getMetodoPago, Collectors.counting()));
        LOGGER.info("Conteo de métodos de pago (filtrado): " + counts);
        return counts;
    }

    public Map<String, Double> getAvgValorTotalCompraByEdadRange(
            Integer edadMin, Integer edadMax,
            Integer frecuenciaCompraMin, Integer frecuenciaCompraMax,
            Double valorTotalCompraMin, Double valorTotalCompraMax,
            Integer ultimaCompraMin, Integer ultimaCompraMax,
            String metodoPago, String categoriaCliente) {
        Page<CustomerData> filteredData = repository.findByFilters(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente,
                Pageable.unpaged());

        Map<String, Double> averages = filteredData.getContent().stream()
                .collect(Collectors.groupingBy(
                        data -> {
                            int edad = data.getEdad();
                            if (edad < 20) return "<20";
                            else if (edad < 30) return "20-29";
                            else if (edad < 40) return "30-39";
                            else if (edad < 50) return "40-49";
                            else return "50+";
                        },
                        Collectors.averagingDouble(CustomerData::getValorTotalCompra)
                ));
        LOGGER.info("Promedio de valor total de compra por rango de edad (filtrado): " + averages);
        return averages;
    }

    public void logError(String message, Exception e) {
        LOGGER.severe(message + ": " + e.getMessage());
    }
}