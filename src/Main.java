import vonneumann.*;

public class Main {
    public static void main(String[] args) {
        IO io  = new IO(System.out);
        RAM ram = new RAM(7);
        Cache cache = new Cache(2, ram);
        CPU cpu = new CPU(cache, io);

        try {
            final int start = 10;

            ram.write(start, 118);
            ram.write(start + 1, 130);

            cpu.run(start);
        } catch (EnderecoInvalido ei) {
            System.err.println(ei.toString());
        }
    }
}
