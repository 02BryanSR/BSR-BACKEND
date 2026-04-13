package com.project.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.project.entity.AddressEntity;
import com.project.entity.OrderDetailEntity;
import com.project.entity.OrderEntity;
import com.project.service.InvoiceService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final float PAGE_MARGIN = 48f;
    private static final float PAGE_BREAK_THRESHOLD = 110f;
    private static final PDFont TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont BODY_FONT = PDType1Font.HELVETICA;
    private static final PDFont LABEL_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont MONO_FONT = PDType1Font.COURIER;

    @Override
    public byte[] generateInvoicePdf(OrderEntity order) {
        if (order == null) {
            throw new RuntimeException("Order is required to generate the invoice");
        }

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            InvoiceCanvas canvas = new InvoiceCanvas(document);

            drawHeader(canvas, order);
            drawCustomerBlock(canvas, order);
            drawItemsTable(canvas, order);
            drawTotalsBlock(canvas, order);

            canvas.close();
            document.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo generar la factura PDF", ex);
        }
    }

    @Override
    public String buildInvoiceFilename(Long orderId) {
        long normalizedOrderId = orderId == null ? 0L : orderId;
        return String.format(Locale.ROOT, "factura-bsr-%06d.pdf", normalizedOrderId);
    }

    private void drawHeader(InvoiceCanvas canvas, OrderEntity order) throws IOException {
        float topY = canvas.y();

        canvas.text("BSR", canvas.left(), topY, LABEL_FONT, 13f, new Color(80, 80, 80));
        canvas.text("FACTURA", canvas.left(), topY - 24f, TITLE_FONT, 24f, Color.BLACK);
        canvas.text("Pedido #" + order.getId(), canvas.left(), topY - 48f, BODY_FONT, 11f, Color.DARK_GRAY);

        float metaX = canvas.left() + 310f;
        canvas.text("Numero", metaX, topY, LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text(buildInvoiceFilename(order.getId()), metaX, topY - 14f, MONO_FONT, 10f, Color.BLACK);
        canvas.text("Fecha", metaX, topY - 34f, LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text(formatDate(order), metaX, topY - 48f, BODY_FONT, 10f, Color.BLACK);

        canvas.line(canvas.left(), topY - 68f, canvas.right(), topY - 68f, new Color(220, 220, 220));
        canvas.moveTo(topY - 92f);
    }

    private void drawCustomerBlock(InvoiceCanvas canvas, OrderEntity order) throws IOException {
        canvas.ensureSpace(120f);

        float baseY = canvas.y();
        float rightColumnX = canvas.left() + 280f;

        canvas.text("Cliente", canvas.left(), baseY, LABEL_FONT, 11f, new Color(110, 110, 110));
        canvas.text(resolveCustomerName(order), canvas.left(), baseY - 16f, BODY_FONT, 11f, Color.BLACK);
        canvas.text(resolveCustomerEmail(order), canvas.left(), baseY - 32f, BODY_FONT, 10f, Color.DARK_GRAY);

        canvas.text("Pago", rightColumnX, baseY, LABEL_FONT, 11f, new Color(110, 110, 110));
        canvas.text(resolvePayMethod(order), rightColumnX, baseY - 16f, BODY_FONT, 11f, Color.BLACK);
        canvas.text(resolvePaymentStatus(order), rightColumnX, baseY - 32f, BODY_FONT, 10f, Color.DARK_GRAY);

        canvas.text("Entrega", canvas.left(), baseY - 58f, LABEL_FONT, 11f, new Color(110, 110, 110));

        float addressY = baseY - 74f;
        for (String line : formatAddressLines(order.getAddress())) {
            canvas.text(line, canvas.left(), addressY, BODY_FONT, 10f, Color.BLACK);
            addressY -= 14f;
        }

        canvas.moveTo(addressY - 14f);
    }

    private void drawItemsTable(InvoiceCanvas canvas, OrderEntity order) throws IOException {
        drawItemsHeader(canvas);

        for (OrderDetailEntity detail : resolveOrderDetails(order)) {
            if (canvas.y() < PAGE_BREAK_THRESHOLD) {
                canvas.newPage();
                drawHeader(canvas, order);
                drawItemsHeader(canvas);
            }

            String concept = fitText(buildConcept(detail), BODY_FONT, 10f, 260f);
            String quantity = String.valueOf(detail.getQuantity());
            String unitPrice = formatMoney(detail.getPriceUnit());
            String lineTotal = formatMoney(detail.getPriceUnit().multiply(BigDecimal.valueOf(detail.getQuantity())));

            canvas.text(concept, canvas.left(), canvas.y(), BODY_FONT, 10f, Color.BLACK);
            canvas.text(quantity, canvas.left() + 300f, canvas.y(), BODY_FONT, 10f, Color.BLACK);
            canvas.text(unitPrice, canvas.left() + 350f, canvas.y(), BODY_FONT, 10f, Color.BLACK);
            canvas.text(lineTotal, canvas.left() + 430f, canvas.y(), LABEL_FONT, 10f, Color.BLACK);
            canvas.line(canvas.left(), canvas.y() - 10f, canvas.right(), canvas.y() - 10f, new Color(236, 236, 236));
            canvas.moveDown(24f);
        }

        canvas.moveDown(8f);
    }

    private void drawTotalsBlock(InvoiceCanvas canvas, OrderEntity order) throws IOException {
        canvas.ensureSpace(74f);

        canvas.line(canvas.left(), canvas.y(), canvas.right(), canvas.y(), new Color(220, 220, 220));
        canvas.moveDown(18f);

        canvas.text("Articulos", canvas.left() + 300f, canvas.y(), LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text(String.valueOf(order.getTotalAmount()), canvas.left() + 430f, canvas.y(), BODY_FONT, 10f, Color.BLACK);
        canvas.moveDown(18f);

        canvas.text("Total", canvas.left() + 300f, canvas.y(), LABEL_FONT, 12f, Color.BLACK);
        canvas.text(formatMoney(order.getTotalPrice()), canvas.left() + 430f, canvas.y(), TITLE_FONT, 12f, Color.BLACK);
        canvas.moveDown(30f);

        canvas.text("Gracias por comprar en BSR.", canvas.left(), canvas.y(), BODY_FONT, 10f, Color.DARK_GRAY);
    }

    private void drawItemsHeader(InvoiceCanvas canvas) throws IOException {
        canvas.ensureSpace(34f);
        canvas.text("Concepto", canvas.left(), canvas.y(), LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text("Cant.", canvas.left() + 300f, canvas.y(), LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text("Precio", canvas.left() + 350f, canvas.y(), LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.text("Total", canvas.left() + 430f, canvas.y(), LABEL_FONT, 10f, new Color(110, 110, 110));
        canvas.line(canvas.left(), canvas.y() - 8f, canvas.right(), canvas.y() - 8f, new Color(220, 220, 220));
        canvas.moveDown(24f);
    }

    private List<OrderDetailEntity> resolveOrderDetails(OrderEntity order) {
        return order.getOrderDetails() == null ? List.of() : order.getOrderDetails();
    }

    private String buildConcept(OrderDetailEntity detail) {
        String productName = detail.getProduct() == null ? "Producto" : detail.getProduct().getName();
        String size = detail.getSize() == null ? "" : detail.getSize().trim();
        return size.isBlank() ? productName : productName + " | Talla " + size;
    }

    private String resolveCustomerName(OrderEntity order) {
        if (order.getCustomer() == null) {
            return "Cliente";
        }

        String firstName = order.getCustomer().getName() == null ? "" : order.getCustomer().getName().trim();
        String lastName = order.getCustomer().getLastName() == null ? "" : order.getCustomer().getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();

        return fullName.isBlank() ? "Cliente" : fullName;
    }

    private String resolveCustomerEmail(OrderEntity order) {
        if (order.getCustomer() == null || order.getCustomer().getEmail() == null) {
            return "";
        }

        return order.getCustomer().getEmail().trim();
    }

    private List<String> formatAddressLines(AddressEntity address) {
        if (address == null) {
            return List.of("Sin direccion asociada");
        }

        return List.of(
                safe(address.getAddress()),
                safe(address.getCp()) + " " + safe(address.getCity()),
                safe(address.getState()) + ", " + safe(address.getCountry()));
    }

    private String resolvePayMethod(OrderEntity order) {
        String payMethod = order.getPayMethod() == null ? "" : order.getPayMethod().trim().toUpperCase(Locale.ROOT);

        return switch (payMethod) {
            case "CARD" -> "Tarjeta";
            case "CASH" -> "Contra reembolso";
            default -> safe(order.getPayMethod());
        };
    }

    private String resolvePaymentStatus(OrderEntity order) {
        String paymentStatus = order.getPaymentStatus() == null ? "" : order.getPaymentStatus().trim();

        if (!paymentStatus.isBlank()) {
            return paymentStatus;
        }

        return "CASH".equalsIgnoreCase(order.getPayMethod()) ? "Pendiente" : "Confirmado";
    }

    private String formatDate(OrderEntity order) {
        return order.getCreateDate() == null ? "Pendiente" : DATE_FORMAT.format(order.getCreateDate());
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0.00 EUR";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " EUR";
    }

    private String fitText(String value, PDFont font, float fontSize, float maxWidth) throws IOException {
        String normalized = safe(value);

        if (font.getStringWidth(normalized) / 1000f * fontSize <= maxWidth) {
            return normalized;
        }

        String ellipsis = "...";
        int end = normalized.length();

        while (end > 0 && font.getStringWidth(normalized.substring(0, end) + ellipsis) / 1000f * fontSize > maxWidth) {
            end -= 1;
        }

        return normalized.substring(0, Math.max(end, 1)) + ellipsis;
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\r", " ").replace("\n", " ").trim();
    }

    private static final class InvoiceCanvas {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream content;
        private float y;

        private InvoiceCanvas(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void newPage() throws IOException {
            closeCurrentStream();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - PAGE_MARGIN;
        }

        private void ensureSpace(float requiredHeight) throws IOException {
            if (y - requiredHeight < PAGE_MARGIN) {
                newPage();
            }
        }

        private void moveDown(float amount) {
            y -= amount;
        }

        private void moveTo(float nextY) {
            y = nextY;
        }

        private float y() {
            return y;
        }

        private float left() {
            return PAGE_MARGIN;
        }

        private float right() {
            return page.getMediaBox().getWidth() - PAGE_MARGIN;
        }

        private void text(String value, float x, float y, PDFont font, float size, Color color) throws IOException {
            content.beginText();
            content.setFont(font, size);
            content.setNonStrokingColor(color);
            content.newLineAtOffset(x, y);
            content.showText(value == null ? "" : value);
            content.endText();
        }

        private void line(float startX, float startY, float endX, float endY, Color color) throws IOException {
            content.setStrokingColor(color);
            content.moveTo(startX, startY);
            content.lineTo(endX, endY);
            content.stroke();
        }

        private void close() throws IOException {
            closeCurrentStream();
        }

        private void closeCurrentStream() throws IOException {
            if (content != null) {
                content.close();
            }
        }
    }
}
