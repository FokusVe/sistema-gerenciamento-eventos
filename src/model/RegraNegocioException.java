package model;

/**
 * Exceção lançada quando alguma regra de negócio ou validação é violada.
 * A View captura essa exceção e mostra a mensagem em um JOptionPane.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
