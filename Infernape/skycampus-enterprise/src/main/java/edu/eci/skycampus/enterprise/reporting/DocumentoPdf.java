package edu.eci.skycampus.enterprise.reporting;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Escribe un PDF 1.4 mínimo de una página con líneas de texto (Helvetica). Sin dependencias externas. */
final class DocumentoPdf {
    private DocumentoPdf() { }

    static String conLineas(List<String> lineas) {
        StringBuilder contenido = new StringBuilder("BT /F1 11 Tf 50 790 Td 14 TL");
        lineas.forEach(linea -> contenido.append(" (").append(escapar(linea)).append(") '"));
        contenido.append(" ET");
        List<String> objetos = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >>"
                        + " /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + contenido.length() + " >>\nstream\n" + contenido + "\nendstream");
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> desplazamientos = new ArrayList<>();
        for (int i = 0; i < objetos.size(); i++) {
            desplazamientos.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n").append(objetos.get(i)).append("\nendobj\n");
        }
        int inicioXref = pdf.length();
        pdf.append("xref\n0 ").append(objetos.size() + 1).append("\n0000000000 65535 f \n");
        desplazamientos.forEach(desplazamiento -> pdf.append(String.format("%010d 00000 n \n", desplazamiento)));
        pdf.append("trailer\n<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(inicioXref).append("\n%%EOF\n");
        return pdf.toString();
    }

    /** Solo ASCII: las tildes se reemplazan para que los desplazamientos en bytes coincidan con los caracteres. */
    private static String escapar(String texto) {
        String ascii = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String soloAscii = new String(ascii.getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
        return soloAscii.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
