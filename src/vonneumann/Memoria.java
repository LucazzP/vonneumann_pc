package vonneumann;

public class Memoria {
    protected int tam = 0;
    private final int[] dados;

    public Memoria(int nbits) {
        this.tam = (int) Math.pow(2, nbits);
        this.dados = new int[tam];
    }

    public int read(int ender) throws EnderecoInvalido {
        if (isNotValid(ender)) {
            throw new EnderecoInvalido(ender);
        }
        return dados[ender];
    }

    public void write(int ender, int valor) throws EnderecoInvalido {
        if (isNotValid(ender)) {
            throw new EnderecoInvalido(ender);
        }
        dados[ender] = valor;
    }

    private boolean isNotValid(int ender) throws EnderecoInvalido {
        return ender < 0 || ender >= tam;
    }
}
