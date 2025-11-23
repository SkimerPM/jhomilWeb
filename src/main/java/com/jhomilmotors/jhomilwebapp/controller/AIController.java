package com.jhomilmotors.jhomilwebapp.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard/ai")
public class AIController {
    final String location = "País:Perú|Departamento:La Libertad|Capital:Trujillo.";
    private final OpenAiChatModel chatModel;

    @Autowired
    public AIController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/buscar/precio-producto-pro")
    public Map<String, String> findPriceProductPro(@RequestParam(value = "productName") String productName) {
        String promptText = MessageFormat.format(
                """
                        Actúa como analista de pricing para una tienda en {1}. \
                        Busca los precios actuales del producto ''{0}'' en tiendas online y físicas de la zona.
                        
                        IMPORTANTE: Responde en TEXTO PLANO con formato visual usando SOLO emojis, guiones y espacios.
                        NO uses markdown, NO uses asteriscos para negritas, NO uses pipes para tablas.
                        
                        Estructura EXACTA (copia este formato):
                        
                        📊 ANÁLISIS DE MERCADO - [Nombre del producto]
                        ════════════════════════════════════════════
                        
                        💰 RANGO DE MERCADO
                        ─────────────────────
                          • Mínimo: S/[precio] - [Tienda] ([estado])
                          • Promedio: S/[precio]
                          • Máximo: S/[precio] - [Tienda] ([estado])
                        
                        🏪 COMPETENCIA DIRECTA
                        ─────────────────────
                          1. [Tienda].......... S/[precio] - [Estado] - [Detalle]
                          2. [Tienda].......... S/[precio] - [Estado] - [Detalle]
                          3. [Tienda].......... S/[precio] - [Estado] - [Detalle]
                          4. [Tienda].......... S/[precio] - [Estado] - [Detalle]
                        
                        📈 RECOMENDACIÓN DE PRECIO
                        ─────────────────────────
                          → Competitivo: S/[min] - S/[max]
                          → Estándar:    S/[min] - S/[max]
                          → Premium:     S/[min] - S/[max]
                        
                        💡 ESTRATEGIA SUGERIDA
                        ─────────────────────
                          ✓ Precio objetivo: S/[precio]
                          ✓ Margen: [X]%%
                          ✓ Posicionamiento: [Estrategia en 1 línea]
                        
                        ⚠️  NOTAS IMPORTANTES
                        ─────────────────────
                          • [Nota 1]
                          • [Nota 2]
                        
                        ════════════════════════════════════════════
                        
                        REGLAS ESTRICTAS:
                        - USA saltos de línea reales (presiona Enter)
                        - NO escribas \\n, escribe saltos de línea de verdad
                        - USA emojis, guiones (─), puntos (•), flechas (→), checks (✓)
                        - USA espacios para alinear
                        - Máximo 250 palabras
                        - Sé conciso y directo""",
                productName,
                location
        );;

        // Usar sonar-pro para búsquedas más profundas
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("sonar-pro")
                .temperature(0.3)  // ✅ Cambiado a 0.3 para respuestas más consistentes
                .build();

        Prompt prompt = new Prompt(promptText, options);
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        // ✅ PRIMERO: Limpiar backticks
        response = response
                .replace("```markdown", "")
                .replace("```md", "")
                .replace("```", "")
                .trim();

        // ✅ SEGUNDO: Convertir \n literales a saltos de línea reales
        // Nota: Usa solo un backslash en el replace, no dos
        response = response.replace("\\n", "\n");

        return Map.of("generation", response);
    }

//    // Opción 3: Para búsquedas que requieren razonamiento profundo
//    @GetMapping("/buscar/analisis-producto")
//    public Map<String, String> analyzeProduct(@RequestParam(value = "productName") String productName) {
//        String promptText = MessageFormat.format(
//                "Analiza el mercado del producto ''{0}'' en {1}. Incluye: " +
//                        "1) Rango de precios, 2) Principales competidores, 3) Tendencias actuales, " +
//                        "4) Recomendaciones de compra.",
//                productName,
//                location
//        );
//
//        // Usar sonar-reasoning para análisis más complejos
//        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                .model("sonar-reasoning")  // ✅ Para análisis con razonamiento
//                .temperature(0.5)
//                .build();
//
//        Prompt prompt = new Prompt(promptText, options);
//        String response = chatModel.call(prompt).getResult().getOutput().getText();
//
//        return Map.of("generation", response);
//    }
}