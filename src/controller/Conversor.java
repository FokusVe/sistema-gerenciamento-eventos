package controller;

import model.RegraNegocioException;

/**
 * Converte os textos digitados nas telas para números,
 * mostrando uma mensagem clara quando o valor é inválido.
 */
public class Conversor {

    public static int paraInteiro(String texto, String nomeCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new RegraNegocioException("Informe o campo " + nomeCampo + ".");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("O campo " + nomeCampo + " deve conter apenas números inteiros.");
        }
    }
}
