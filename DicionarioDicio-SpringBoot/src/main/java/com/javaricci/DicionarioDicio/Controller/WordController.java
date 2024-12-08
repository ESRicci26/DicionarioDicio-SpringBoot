package com.javaricci.DicionarioDicio.Controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.sql.*;

@Controller
@RequestMapping("/")
public class WordController {
    private static final String DB_URL = "jdbc:sqlite:DLP-DICIO.DB";

    //Renderiza a página inicial
    @GetMapping
    public String home() {
        return "index";
    }

    //Processa a busca da palavra
    @PostMapping("/search")
    public String searchWord(@RequestParam("word") String word, Model model) {
        String definition = findWordDefinition(word);
        String formattedDefinition = formatJson(definition);

        if (formattedDefinition == null) {
            model.addAttribute("message", "Palavra não encontrada.");
        } else {
            model.addAttribute("result", formattedDefinition);
        }

        return "index";
    }

    //Consulta a definição da palavra no banco de dados
    private String findWordDefinition(String word) {
        String sql = "SELECT definition FROM words_table WHERE LOWER(word) = LOWER(?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("definition");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Formata a definição JSON com indentação
    private String formatJson(String jsonString) {
        if (jsonString == null) return null;

        JSONArray jsonArray = new JSONArray(jsonString);
        StringBuilder formattedJson = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            formattedJson.append(jsonObject.toString(4)).append("\n");
        }

        return formattedJson.toString();
    }
}
