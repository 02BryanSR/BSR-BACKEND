package com.project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.project.entity.AddressEntity;
import com.project.entity.OrderDetailEntity;
import com.project.entity.OrderEntity;
import com.project.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        sendHtmlEmail(to, "Recuperacion de contrasena", buildPasswordResetHtml(resetLink));
    }

    @Override
    public void sendOrderConfirmationEmail(OrderEntity order) {
        if (order == null || order.getCustomer() == null || order.getCustomer().getEmail() == null
                || order.getCustomer().getEmail().isBlank()) {
            throw new RuntimeException("Order confirmation email cannot be sent without a customer email");
        }

        String subject = "Confirmacion de pedido #" + order.getId();
        sendHtmlEmail(order.getCustomer().getEmail(), subject, buildOrderConfirmationHtml(order));
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            LOGGER.info("Sending email with Resend SMTP. From={} To={} Subject={}", from, to, subject);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            LOGGER.info("Email accepted by SMTP client for recipient {}", to);
        } catch (MailException | MessagingException ex) {
            LOGGER.error("HTML email send failed for {}", to, ex);
            throw new RuntimeException("No se pudo enviar el correo HTML", ex);
        }
    }

    private String buildPasswordResetHtml(String resetLink) {
        String safeResetLink = escapeHtml(resetLink);

        return """
                <!doctype html>
                <html lang="es">
                  <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  </head>
                  <body style="margin:0;padding:0;background:#f5f1ea;font-family:Arial,Helvetica,sans-serif;color:#171717;">
                    <div style="max-width:680px;margin:0 auto;padding:32px 18px;">
                      <div style="background:#ffffff;border:1px solid #e6dbcc;border-radius:28px;padding:28px 28px 32px;">
                        <div style="font-size:12px;letter-spacing:0.32em;text-transform:uppercase;color:#7b756b;">BSR</div>
                        <h1 style="margin:16px 0 8px;font-size:34px;line-height:1.1;letter-spacing:0.08em;text-transform:uppercase;">Recuperar contrasena</h1>
                        <p style="margin:0;color:#5f5a52;font-size:16px;line-height:1.7;">
                          Hemos recibido una solicitud para restablecer tu contrasena. Pulsa en el boton para continuar.
                        </p>

                        <div style="margin-top:28px;padding:18px 18px 22px;border-radius:20px;background:#171717;color:#ffffff;">
                          <div style="font-size:12px;letter-spacing:0.28em;text-transform:uppercase;color:#d7d1c7;">Acceso seguro</div>
                          <p style="margin:14px 0 0;color:#f4efe8;font-size:15px;line-height:1.7;">
                            Este enlace caduca automaticamente. Si no has solicitado este cambio, puedes ignorar este correo.
                          </p>
                          <a href="%s" style="display:inline-block;margin-top:20px;padding:14px 22px;border-radius:999px;background:#ffffff;color:#171717;text-decoration:none;font-weight:700;">
                            Restablecer contrasena
                          </a>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(safeResetLink);
    }

    private String buildOrderConfirmationHtml(OrderEntity order) {
        String customerName = escapeHtml(resolveCustomerName(order));
        StringBuilder itemsHtml = new StringBuilder();

        if (order.getOrderDetails() != null) {
            for (OrderDetailEntity detail : order.getOrderDetails()) {
                if (detail.getProduct() == null) {
                    continue;
                }

                itemsHtml.append("""
                        <tr>
                          <td style="padding:14px 0;border-bottom:1px solid #ece7df;color:#171717;font-size:14px;">
                            <div style="font-weight:600;">%s</div>
                            <div style="margin-top:4px;color:#6b6b6b;font-size:13px;">%s</div>
                          </td>
                          <td style="padding:14px 0;border-bottom:1px solid #ece7df;color:#171717;font-size:14px;text-align:right;font-weight:600;">
                            %s
                          </td>
                        </tr>
                        """.formatted(
                        escapeHtml(detail.getProduct().getName()),
                        escapeHtml(buildOrderLineMeta(detail)),
                        escapeHtml(formatLineTotal(detail))));
            }
        }

        String addressHtml = "";
        if (order.getAddress() != null) {
            addressHtml = """
                    <div style="margin-top:24px;padding:20px;border:1px solid #ece7df;border-radius:20px;background:#faf8f3;">
                      <div style="font-size:12px;letter-spacing:0.28em;text-transform:uppercase;color:#7b756b;">Direccion de entrega</div>
                      <div style="margin-top:12px;color:#171717;font-size:14px;line-height:1.7;">%s</div>
                    </div>
                    """.formatted(escapeHtml(formatAddress(order.getAddress())).replace("\n", "<br>"));
        }

        return """
                <!doctype html>
                <html lang="es">
                  <body style="margin:0;padding:0;background:#f5f1ea;font-family:Arial,Helvetica,sans-serif;color:#171717;">
                    <div style="max-width:680px;margin:0 auto;padding:32px 18px;">
                      <div style="background:#ffffff;border:1px solid #ebe4d8;border-radius:28px;padding:36px;">
                        <div style="font-size:12px;letter-spacing:0.32em;text-transform:uppercase;color:#7b756b;">BSR</div>
                        <h1 style="margin:16px 0 8px;font-size:34px;line-height:1.1;letter-spacing:0.08em;text-transform:uppercase;">Pedido confirmado</h1>
                        <p style="margin:0;color:#5f5a52;font-size:16px;line-height:1.7;">
                          Hola %s, hemos recibido correctamente tu pedido y ya ha quedado registrado en nuestra tienda.
                        </p>

                        <div style="margin-top:28px;padding:24px;border-radius:24px;background:#171717;color:#ffffff;">
                          <div style="font-size:12px;letter-spacing:0.28em;text-transform:uppercase;color:#d7d1c7;">Resumen</div>
                          <div style="display:block;margin-top:16px;">
                            <div style="margin-bottom:10px;"><strong>Pedido:</strong> #%s</div>
                            <div style="margin-bottom:10px;"><strong>Fecha:</strong> %s</div>
                            <div style="margin-bottom:10px;"><strong>Metodo de pago:</strong> %s</div>
                            <div style="margin-bottom:10px;"><strong>Estado del pago:</strong> %s</div>
                            <div style="margin-bottom:10px;"><strong>Articulos:</strong> %s</div>
                            <div style="font-size:22px;font-weight:700;margin-top:18px;">Total %s</div>
                          </div>
                        </div>

                        %s

                        <div style="margin-top:24px;padding:24px;border:1px solid #ece7df;border-radius:24px;">
                          <div style="font-size:12px;letter-spacing:0.28em;text-transform:uppercase;color:#7b756b;">Productos</div>
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:14px;border-collapse:collapse;">
                            %s
                          </table>
                        </div>

                        <p style="margin:24px 0 0;color:#5f5a52;font-size:14px;line-height:1.7;">
                          Gracias por comprar en BSR. Si hay cualquier cambio en el estado del pedido, te avisaremos por correo.
                        </p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(
                customerName,
                escapeHtml(String.valueOf(order.getId())),
                escapeHtml(formatOrderDate(order)),
                escapeHtml(resolvePayMethod(order)),
                escapeHtml(resolvePaymentStatus(order)),
                escapeHtml(String.valueOf(order.getTotalAmount())),
                escapeHtml(formatMoney(order.getTotalPrice())),
                addressHtml,
                itemsHtml.toString());
    }

    private String resolveCustomerName(OrderEntity order) {
        if (order.getCustomer() == null || order.getCustomer().getName() == null || order.getCustomer().getName().isBlank()) {
            return "cliente";
        }
        return order.getCustomer().getName().trim();
    }

    private String formatAddress(AddressEntity address) {
        return address.getAddress() + "\n"
                + address.getCp() + " - " + address.getCity() + " - " + address.getState() + "\n"
                + address.getCountry();
    }

    private String formatOrderDate(OrderEntity order) {
        if (order.getCreateDate() == null) {
            return "Pendiente";
        }
        return ORDER_DATE_FORMAT.format(order.getCreateDate());
    }

    private String resolvePayMethod(OrderEntity order) {
        String payMethod = order.getPayMethod() == null ? "" : order.getPayMethod().trim().toUpperCase();
        return switch (payMethod) {
            case "CARD" -> "Tarjeta";
            case "CASH" -> "Contra reembolso";
            default -> order.getPayMethod();
        };
    }

    private String resolvePaymentStatus(OrderEntity order) {
        String paymentStatus = order.getPaymentStatus() == null ? "" : order.getPaymentStatus().trim();
        if (!paymentStatus.isBlank()) {
            return paymentStatus;
        }

        String payMethod = order.getPayMethod() == null ? "" : order.getPayMethod().trim().toUpperCase();
        if ("CASH".equals(payMethod)) {
            return "Pendiente de cobro en entrega";
        }
        if ("CARD".equals(payMethod)) {
            return "Confirmado";
        }

        return "Pendiente";
    }

    private String formatLineTotal(OrderDetailEntity detail) {
        return formatMoney(detail.getPriceUnit().multiply(BigDecimal.valueOf(detail.getQuantity())));
    }

    private String buildOrderLineMeta(OrderDetailEntity detail) {
        String size = detail.getSize() == null ? "" : detail.getSize().trim();

        if (size.isEmpty()) {
            return "Cantidad: " + detail.getQuantity();
        }

        return "Cantidad: " + detail.getQuantity() + " | Talla: " + size;
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0.00 EUR";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " EUR";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
