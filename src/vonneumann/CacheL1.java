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
    private int wTamBits;
    private int rTamBits;
    private int tTamBits;

    void setVarsWRTS(int pos) {
        wTamBits = tamKCacheLineNBits; // 6
        rTamBits = Integer.bitCount(super.tam - 1); // 7
        tTamBits = Integer.bitCount(ram.tam - 1) - rTamBits - wTamBits; // 11

        int wrTamBits = wTamBits + rTamBits; // 13
        int rtTamBits = rTamBits + tTamBits; // 18

        w = pos & (int) Math.pow(2, wTamBits) - 1; // wTamBits ultimos bits de x
        r = (pos & ((int) Math.pow(2, rTamBits) - 1 << wTamBits)) >> wTamBits; // 7 bits depois de w
        t = (pos & ((int) Math.pow(2, tTamBits) - 1 << wrTamBits)) >> wrTamBits; // 11 bits depois de r
        s = (pos & ((int) Math.pow(2, rtTamBits) - 1 << wTamBits)) >> wTamBits; // concatenação do r e t
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

    // False: Cache Hit
    // True: Cache miss
    private boolean isNotOnCache() {
        return memoriaTagged.data == null || memoriaTagged.tag != t;
    }

    private void syncCacheRam() throws EnderecoInvalido {
        // Copia o cache para a ram se necessario
        if (memoriaTagged.modified) {
            int inicio = s << wTamBits;
            int pos = 0;
            for (int i = inicio; i < inicio + kTamCacheLine; i++) {
                ram.write(i, memoriaTagged.data.read(pos));
                pos++;
            }
        }

        // Cria e popula a cache com os valores da RAM
        Memoria memCache = new Memoria(wTamBits);

        int inicio = s << wTamBits;
        int pos = 0;
        for (int i = inicio; i < inicio + kTamCacheLine; i++) {
            memCache.write(pos, ram.read(i));
            pos++;
        }

        memoriaTagged = new Tagged<>(t, memCache);
        cacheLines.set(r, memoriaTagged);
    }
}
