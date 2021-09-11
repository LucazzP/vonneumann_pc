package vonneumann;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheL1 extends Memoria {
    private final Memoria ram;
    private final ArrayList<Tagged<Memoria>> cacheLines;
    final private int kTamCacheLine;
    final private int tamKCacheLineNBits;

    public CacheL1(int tamNBits, int tamKCacheLineNBits, Memoria ram) {
        super(tamNBits);
        this.ram = ram;
        this.cacheLines = Stream.iterate(new Tagged<Memoria>(0, null),
                        n -> new Tagged<Memoria>(n.tag + 1, null))
                .limit((int) Math.pow(2, tamNBits))
                .collect(Collectors.toCollection(ArrayList::new));
        this.tamKCacheLineNBits = tamKCacheLineNBits;
        this.kTamCacheLine = (int) Math.pow(2, tamKCacheLineNBits);
    }

    // Variaveis de trabalho
    private int w; // 6 ultimos bits de x
    private int r; // 7 bits depois de w
    private int t; // 11 bits depois de r
    private int s; // concatenação do r e t
    private Tagged<Memoria> memoriaTagged;

    void setVarsWRTS(int pos) {
        memoriaTagged = null;
        w = pos & 0b111111; // 6 ultimos bits de x
        r = (pos & 0b1111111000000) >> 6; // 7 bits depois de w
        t = (pos & 0b111111111110000000000000) >> 13; // 11 bits depois de r
        s = (pos & 0b111111111111111110000000) >> 6; // concatenação do r e t
        memoriaTagged = cacheLines.get(r);
    }

    @Override
    public int read(int pos) throws EnderecoInvalido {
        setVarsWRTS(pos);
        // Se não estiver no cache, pega da memoria e traz pra cache
        if (isNotOnCache()) {
            System.out.println("READ cache MISS pos: " + pos);
            syncCacheRam();
        } else {
            System.out.println("READ cache HIT pos: " + pos);
        }
        return memoriaTagged.data.read(w);
    }

    @Override
    public void write(int pos, int valor) throws EnderecoInvalido {
        setVarsWRTS(pos);
        // Se não estiver no cache, pega da memoria e traz pra cache
        if (isNotOnCache()) {
            System.out.println("WRITE cache MISS pos: " + pos);
            syncCacheRam();
        } else {
            System.out.println("WRITE cache HIT pos: " + pos);
        }
        memoriaTagged.data.write(w, valor);
        memoriaTagged.modified = true;
        cacheLines.set(r, memoriaTagged);
    }

    /// True: Cache Hit
    /// False: Cache miss
    private boolean isNotOnCache() {
        return memoriaTagged.data == null || memoriaTagged.tag != t;
    }

    private void syncCacheRam() throws EnderecoInvalido {
        // Se não existe cache criado no cacheLine, cria e popula a cache com os valores da RAM
        if (memoriaTagged.data == null) {
            Memoria memCache = new Memoria(tamKCacheLineNBits);

            int inicio = s + (r * kTamCacheLine); // s + deslocamento
            int pos = 0;
            for (int i = inicio; i < inicio + kTamCacheLine; i++) {
                memCache.write(pos, ram.read(i));
                pos++;
            }

            memoriaTagged = new Tagged<>(t, memCache);
            cacheLines.set(r, memoriaTagged);
        }

        // Copia o cache para a ram se necessario
        if (memoriaTagged.modified) {
            int inicio = s + (r * kTamCacheLine); // s + deslocamento
            int pos = 0;
            for (int i = inicio; i < inicio + kTamCacheLine; i++) {
                ram.write(i, memoriaTagged.data.read(pos));
                pos++;
            }
        }
    }
}
