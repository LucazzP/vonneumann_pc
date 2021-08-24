package vonneumann;

public class EnderecoInvalido extends Exception {
    public int ender;

    public EnderecoInvalido(int ender) {
        this.ender = ender;
    }

    @Override
    public String toString() {
        return "EnderecoInvalido{" +
                "ender=" + ender +
                '}';
    }
}

