package com.jhomilmotors.jhomilwebapp.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai") // Ruta pública para la App
public class MobileAIController {

    private final OpenAiChatModel chatModel;

    @Autowired
    public MobileAIController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/asistente")
    public ResponseEntity<Map<String, String>> chatWithMechanic(@RequestParam String pregunta) {

        // El prompt cambia totalmente: Ahora es un vendedor amable
        String promptText = MessageFormat.format(
                """
                Actúa como 'JhomilBot', un experto mecánico y vendedor de la tienda de repuestos 'Jhomil Motors'.
                
                El cliente pregunta: "{0}"
                
                Tu objetivo es:
                1. Dar un consejo técnico breve y útil.
                2. Recomendar qué tipo de producto buscar (ej: aceite, pastillas, casco).
                3. Ser amable, usar emojis de motos/herramientas y hablar de 'tú'.
                4. NO inventes precios. Di 'Puedes ver el precio actualizado en nuestro catálogo'.
                5. Respuesta máxima de 3 párrafos cortos.
                """,
                pregunta
        );

        Prompt prompt = new Prompt(promptText);
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        return ResponseEntity.ok(Map.of(
                "pregunta", pregunta,
                "respuesta", response
        ));
    }
}