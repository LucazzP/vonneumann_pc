import vonneumann.*;

// Lucas Polazzo
// Lincoln Mesatto
// Mateus Andreatta

public class Main {
    public static void main(String[] args) {
        IO io  = new IO(System.out);
        RAM ram = new RAM(24); // tamanho da memória principal é de 16M, a CPU gerará endereços de 24 bits
        Cache cache = new Cache(2, ram);
        // A capacidade da cache deverá ser de 8192 palavras com cada cache line armazenando 64 palavras (i.e., K=64).
        // tamNBits: 8192 / 64 = 128 cache lines = 7 bits.
        // tamKPalavrasNBits: K=64 palavras = 6 bits.
        CacheL1 cacheL1 = new CacheL1(7,6, ram);
        CPU cpu = new CPU(cacheL1, io);

        try {
            final int start = 10;

            ram.write(start, 118);
            ram.write(start + 1, 200);

            cpu.run(start);
        } catch (EnderecoInvalido ei) {
            System.err.println(ei.toString());
        }
    }
}
