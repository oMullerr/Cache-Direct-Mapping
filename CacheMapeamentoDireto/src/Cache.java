import java.util.LinkedList;

public class Cache extends Memoria {
    private int kWords; //tamanho de cada cache line
    private int tamanhoCache; //tamanho da cache total
    private LinkedList<CacheLine> cacheLines; //Lista de cache lines
    private final Memoria ram;

    public Cache(int tamanho, Memoria ram, int kWords) {
        super(tamanho);
        this.cacheLines = new LinkedList<>();
        this.tamanhoCache = tamanho;
        this.kWords = kWords; // tamanho cache line
        this.ram = ram;

        // cria as cache lines
        for (int line = 0; line < Math.ceilDiv(tamanho, kWords); line++) {
            cacheLines.add(new CacheLine(kWords, line, tamanho, ram)); // ram troca o bloco da ram e tamanho pra ver a quantidade de bits
        }

    }


    @Override
    int Read(int endereco) throws EnderecoInvalido {
        int kWordsBits = (int) (Math.log(kWords) / Math.log(2)); // transforma para bits a quantidade de palavras em cada cache line
        int kRowBits = (int) (Math.log(Math.ceilDiv(tamanhoCache, kWords)) / Math.log(2)); // Quantidade de bits para o R


        int w = (endereco & (kWords - 1)); // pega os primeiros bits fazendo um E lógico para pegar as primeiras posições
        int r = (endereco >> kWordsBits & (1 << kRowBits - 1)); //remove os bits do W e fazemos um E logico para andar para a direita e invertemos o 0
        // Usado para fazer a 'mascara'
        /*
        Exemplo:
        1 << 7 - 1
          1 << 7 = adiciona 7 zeros
          -1 inverte os valores
        10000000
        01111111
        */
        int t = (endereco >> (kRowBits + kWordsBits)); // anda a quantidade de bits de r+w, resultando no t

        return cacheLines.get(r).Read(t, w); //faz read na cache line específica
    }


    @Override
    void Write(int endereco, int valor) throws EnderecoInvalido {
        int kWordsBits = (int) (Math.log(kWords) / Math.log(2)); // Quantidade de bits para uma palavra
        int kRowBits = (int) (Math.log(Math.ceilDiv(tamanhoCache, kWords)) / Math.log(2)); // Quantidade de bits para o R


        int w = (endereco & (kWords - 1)); // pega os primeiros bits fazendo um E lógico utilizando K
        int r = (endereco >> kWordsBits & (1 << kRowBits - 1)); //"anda" a quantidade de bits de w
        // Usado para fazer a 'mascara'
        /*
        Exemplo:
        1 << 7 - 1
          1 << 7 = adiciona 7
          -1 inverte os valores
        10000000
        01111111
        */
        int t = (endereco >> (kRowBits + kWordsBits)); // "anda" a quantidade de bits de r+w, resultando no t

        cacheLines.get(r).Write(t, w, valor); // modifica o valor no endereço passado.
    }
}

