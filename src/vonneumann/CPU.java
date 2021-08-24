package vonneumann;

public class CPU {
    private final Memoria memoria;
    private final IO es;
    private int pc = 0;

    public CPU(Memoria memoria, IO es) {
        this.memoria = memoria;
        this.es = es;
    }

    public void run(int ender) throws EnderecoInvalido {
        pc = ender;

        int regA = memoria.read(pc++);
        int regB = memoria.read(pc++);

        for (int i = regA; i <= regB; ++i) {
            int value = i - regA + 1;
            memoria.write(i, value);
            es.Output("cpu> " + i + " -> " + value);
        }
    }
}
