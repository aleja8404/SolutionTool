package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.CustomerData;
import com.elingenio.Proyecto.Services.CustomerClassificationService;
import com.elingenio.Proyecto.Services.dto.PredictionCustomer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/clasificacion")
public class CustomerClassificationController {

    private static final Logger LOGGER = Logger.getLogger(CustomerClassificationController.class.getName());
    private final CustomerClassificationService classificationService;

    public CustomerClassificationController(CustomerClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("customerData", new CustomerData());
        return "CustomerClassification";
    }

    @PostMapping("/clasificar")
    public String clasificar(@ModelAttribute CustomerData datos, Model model) {
        try {
            PredictionCustomer prediction = classificationService.classifyAndSave(datos);
            model.addAttribute("clasificacion", prediction.getPrediction());
            model.addAttribute("confianza", prediction.getConfidence());
            model.addAttribute("customerData", datos);
            model.addAttribute("showResult", true);
            return "CustomerClassification";
        } catch (Exception e) {
            classificationService.logError("Error al realizar la clasificación", e);
            model.addAttribute("error", "Error interno al procesar la clasificación");
            model.addAttribute("customerData", datos);
            return "CustomerClassification";
        }
    }

    @GetMapping("/lista")
    public String listPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer edadMin,
            @RequestParam(required = false) Integer edadMax,
            @RequestParam(required = false) Integer frecuenciaCompraMin,
            @RequestParam(required = false) Integer frecuenciaCompraMax,
            @RequestParam(required = false) Double valorTotalCompraMin,
            @RequestParam(required = false) Double valorTotalCompraMax,
            @RequestParam(required = false) Integer ultimaCompraMin,
            @RequestParam(required = false) Integer ultimaCompraMax,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String categoriaCliente,
            Model model) {

        if (page < 0) page = 0;
        if (size <= 0 || size > 50) size = 5;
        if (edadMin != null && edadMax != null && edadMin > edadMax) {
            Integer temp = edadMin;
            edadMin = edadMax;
            edadMax = temp;
        }


        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerData> predictions = classificationService.getFilteredPredictions(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente,
                pageable);

        // Logging y agregar atributos al modelo
        LOGGER.info("Datos obtenidos para la lista - Total: " + predictions.getTotalElements() +
                ", Página: " + predictions.getNumber() + ", Tamaño: " + predictions.getSize());

        model.addAttribute("predictions", predictions);
        model.addAttribute("metodoPagoList", classificationService.getDistinctMetodoPago());
        model.addAttribute("categoriaClienteList", classificationService.getDistinctCategoriaCliente());
        model.addAttribute("categoriaClienteCounts", classificationService.getCategoriaClienteCounts(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente));
        model.addAttribute("metodoPagoCounts", classificationService.getMetodoPagoCounts(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente));
        model.addAttribute("avgValorTotalCompraByEdad", classificationService.getAvgValorTotalCompraByEdadRange(
                edadMin, edadMax,
                frecuenciaCompraMin, frecuenciaCompraMax,
                valorTotalCompraMin, valorTotalCompraMax,
                ultimaCompraMin, ultimaCompraMax,
                metodoPago, categoriaCliente));

        // Pasar todos los parámetros de filtro a la vista
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("edadMin", edadMin);
        model.addAttribute("edadMax", edadMax);
        model.addAttribute("frecuenciaCompraMin", frecuenciaCompraMin);
        model.addAttribute("frecuenciaCompraMax", frecuenciaCompraMax);
        model.addAttribute("valorTotalCompraMin", valorTotalCompraMin);
        model.addAttribute("valorTotalCompraMax", valorTotalCompraMax);
        model.addAttribute("ultimaCompraMin", ultimaCompraMin);
        model.addAttribute("ultimaCompraMax", ultimaCompraMax);
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("categoriaCliente", categoriaCliente);

        return "CustomerClassificationList";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() throws Exception {
        // Obtener todos los datos sin paginación
        List<CustomerData> allData = classificationService.getAllPredictions();

        LOGGER.info("Datos para exportar - Total: " + allData.size());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clasificaciones de Clientes");

        // Crear fila de encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID",
                "Edad",
                "Frecuencia de Compra",
                "Valor Total de Compra",
                "Días desde Última Compra",
                "Método de Pago",
                "Categoría",
                "Confianza (%)"
        };

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Llenar datos
        int rowNum = 1;
        for (CustomerData data : allData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getId());
            row.createCell(1).setCellValue(data.getEdad());
            row.createCell(2).setCellValue(data.getFrecuenciaCompra());
            row.createCell(3).setCellValue(data.getValorTotalCompra());
            row.createCell(4).setCellValue(data.getUltimaCompra());
            row.createCell(5).setCellValue(data.getMetodoPago() != null ? data.getMetodoPago() : "N/A");
            row.createCell(6).setCellValue(data.getCategoriaCliente() != null ? data.getCategoriaCliente() : "N/A");

            // Formatear confianza como porcentaje
            if (data.getConfianza() != null) {
                row.createCell(7).setCellValue(String.format("%.2f%%", data.getConfianza()));
            } else {
                row.createCell(7).setCellValue("N/A");
            }
        }

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headersResponse = new HttpHeaders();
        headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headersResponse.setContentDispositionFormData("attachment", "clientes_completo.xlsx");

        LOGGER.info("Archivo Excel generado con " + (rowNum - 1) + " filas de datos.");
        return ResponseEntity.ok()
                .headers(headersResponse)
                .body(outputStream.toByteArray());
    }
}