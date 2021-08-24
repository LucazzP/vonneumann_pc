package vonneumann;

public class Cache extends Memoria {
    private final Memoria ram;
    private int inicio;

    public Cache(int tamNBits, Memoria ram) {
        super(tamNBits);
        this.ram = ram;
        this.inicio = -1;
    }

    @Override
    public int read(int pos) throws EnderecoInvalido {
        // Se estiver no cache, le do cache
        if(isOnCache(pos)) {
            return super.read(pos - inicio);
        }
        syncCacheRam(pos);
        return super.read(pos - inicio);
    }

    @Override
    public void write(int pos, int valor) throws EnderecoInvalido {
        // Se estiver no cache, le do cache
        if(isOnCache(pos)) {
            super.write(pos - inicio, valor);
            return;
        }
        syncCacheRam(pos);
        super.write(pos - inicio, valor);
    }

    private boolean isOnCache(int pos) {
        return inicio != -1 && pos > inicio && pos < fim();
    }

    public int fim() {
        return Math.min(inicio + tam, ram.tam);
    }

    private void syncCacheRam(int pos) throws EnderecoInvalido {
        // Se nao encontra, copia o cache para a ram
        if(inicio > -1) {
            for (int i = inicio; i < fim(); i++) {
                ram.write(i, super.read(i - inicio));
            }
        }
        // Seta a nova posicao de inicio
        inicio = pos;
        // Copia a ram para o cache
        for (int i = pos; i < fim(); i++) {
            super.write(i - inicio, ram.read(i));
        }
    }
}
