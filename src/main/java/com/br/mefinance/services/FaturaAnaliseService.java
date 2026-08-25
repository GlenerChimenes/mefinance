package com.br.mefinance.services;

import com.br.mefinance.dto.FaturaAnaliseDTO;
import com.br.mefinance.dto.FaturaCategoriaResumoDTO;
import com.br.mefinance.dto.FaturaLancamentoDTO;
import com.br.mefinance.dto.FaturaMesResumoDTO;
import com.br.mefinance.dto.FaturaTitularResumoDTO;
import com.br.mefinance.services.exceptions.RequisicaoInvalidaException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FaturaAnaliseService {

    private static final long TAMANHO_MAXIMO = 10 * 1024 * 1024;
    private static final Pattern LINHA_LANCAMENTO = Pattern.compile(
            "^\\s*(?<data>\\d{2}[/-]\\d{2}(?:[/-]\\d{2,4})?|\\d{4}-\\d{2}-\\d{2}|" +
                    "\\d{2}\\s+(?:JAN|FEV|MAR|ABR|MAI|JUN|JUL|AGO|SET|OUT|NOV|DEZ))" +
                    "[\\s;,\\t]+(?<descricao>.+?)[\\s;,\\t]+" +
                    "(?<valor>-?\\s*(?:R\\$\\s*)?(?:\\d{1,3}(?:\\.\\d{3})+|\\d+),\\d{2}|" +
                    "-?\\s*(?:R\\$\\s*)?\\d+\\.\\d{2})\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA_DESCRICAO_PENDENTE = Pattern.compile(
            "^\\s*(?<data>\\d{2}\\s+(?:JAN|FEV|MAR|ABR|MAI|JUN|JUL|AGO|SET|OUT|NOV|DEZ))" +
                    "\\s+(?<descricao>.+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern LINHA_CONTINUACAO_VALOR = Pattern.compile(
            "^\\s*(?<descricao>.+?)[\\s;,\\t]+" +
                    "(?<valor>-?\\s*(?:R\\$\\s*)?(?:\\d{1,3}(?:\\.\\d{3})+|\\d+),\\d{2}|" +
                    "-?\\s*(?:R\\$\\s*)?\\d+\\.\\d{2})\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PARCELA = Pattern.compile("(?<!\\d)(?<parcela>\\d{1,2}/\\d{1,2})(?!\\d)");
    private static final Pattern PARCELA_ISOLADA = Pattern.compile("^\\d{1,2}/\\d{1,2}$");
    private static final Pattern ESPACOS = Pattern.compile("\\s+");
    private static final Set<String> EXTENSOES_ACEITAS = Set.of("pdf", "csv", "txt");
    private static final List<String> LINHAS_IGNORADAS = List.of(
            "total da fatura", "total fatura", "total de ", "pagamento recebido",
            "pagamento-boleto", "saldo anterior", "movimentacoes da conta",
            "limite disponivel", "melhor dia de compra", "vencimento da fatura");

    private static final Map<String, Integer> MESES = Map.ofEntries(
            Map.entry("JAN", 1), Map.entry("FEV", 2), Map.entry("MAR", 3),
            Map.entry("ABR", 4), Map.entry("MAI", 5), Map.entry("JUN", 6),
            Map.entry("JUL", 7), Map.entry("AGO", 8), Map.entry("SET", 9),
            Map.entry("OUT", 10), Map.entry("NOV", 11), Map.entry("DEZ", 12));

    private static final Map<String, List<String>> REGRAS_CATEGORIA = criarRegrasCategoria();

    public FaturaAnaliseDTO analisar(MultipartFile arquivo, YearMonth mesReferencia, String termo) {
        validarArquivo(arquivo);

        YearMonth referencia = mesReferencia == null ? YearMonth.now() : mesReferencia;
        String texto = extrairTexto(arquivo);
        List<FaturaLancamentoDTO> lancamentos = extrairLancamentos(texto, referencia);

        if (lancamentos.isEmpty()) {
            throw new RequisicaoInvalidaException(
                    "Nenhum lançamento foi identificado. Use PDF com texto ou CSV/TXT com data, descrição e valor.");
        }

        String termoNormalizado = normalizar(termo == null ? "" : termo.trim());
        BigDecimal totalPesquisa = lancamentos.stream()
                .filter(item -> termoNormalizado.isBlank()
                        || normalizar(item.descricao()).contains(termoNormalizado)
                        || normalizar(item.categoria()).contains(termoNormalizado))
                .map(FaturaLancamentoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeral = lancamentos.stream()
                .map(FaturaLancamentoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FaturaAnaliseDTO(
                arquivo.getOriginalFilename(),
                moeda(totalGeral),
                lancamentos.size(),
                termo == null ? "" : termo.trim(),
                moeda(totalPesquisa),
                agruparPorTitular(lancamentos),
                agruparPorMes(lancamentos),
                lancamentos);
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RequisicaoInvalidaException("Selecione uma fatura para analisar.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new RequisicaoInvalidaException("A fatura deve ter no máximo 10 MB.");
        }

        String extensao = extensao(arquivo.getOriginalFilename());
        if (!EXTENSOES_ACEITAS.contains(extensao)) {
            throw new RequisicaoInvalidaException("Formato não suportado. Envie um arquivo PDF, CSV ou TXT.");
        }
    }

    private String extrairTexto(MultipartFile arquivo) {
        String extensao = extensao(arquivo.getOriginalFilename());
        try {
            if ("pdf".equals(extensao)) {
                try (PDDocument documento = Loader.loadPDF(arquivo.getBytes())) {
                    return extrairTextoPdf(documento);
                }
            }
            return new String(arquivo.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RequisicaoInvalidaException("Não foi possível ler a fatura enviada.");
        }
    }

    private String extrairTextoPdf(PDDocument documento) throws IOException {
        List<String> paginas = new ArrayList<>();

        for (int pagina = 1; pagina <= documento.getNumberOfPages(); pagina++) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(pagina);
            stripper.setEndPage(pagina);
            paginas.add(stripper.getText(documento));
        }

        StringBuilder lancamentosSicoob = new StringBuilder();
        for (int indice = 0; indice < paginas.size(); indice++) {
            String paginaNormalizada = normalizar(paginas.get(indice));
            boolean paginaDeLancamentos = paginaNormalizada.contains("lancamentos")
                    && paginaNormalizada.contains("sicoob mastercard");

            if (paginaDeLancamentos) {
                lancamentosSicoob.append(extrairColunasSicoob(documento.getPage(indice))).append('\n');
            }
        }

        if (!lancamentosSicoob.isEmpty()) {
            return lancamentosSicoob.toString();
        }

        return String.join("\n", paginas);
    }

    private String extrairColunasSicoob(PDPage pagina) throws IOException {
        float largura = pagina.getCropBox().getWidth();
        float altura = pagina.getCropBox().getHeight();
        float divisaoColunas = largura * 0.485f;

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        stripper.addRegion("esquerda", new Rectangle2D.Float(0, 0, divisaoColunas, altura));
        stripper.addRegion("direita", new Rectangle2D.Float(
                divisaoColunas, 0, largura - divisaoColunas, altura));
        stripper.extractRegions(pagina);

        return stripper.getTextForRegion("esquerda") + "\n" + stripper.getTextForRegion("direita");
    }

    private List<FaturaLancamentoDTO> extrairLancamentos(String texto, YearMonth referencia) {
        List<FaturaLancamentoDTO> itens = new ArrayList<>();
        String descricaoPendente = "";
        String titularAtual = "Não identificado";

        for (String linha : texto.split("\\R")) {
            String linhaLimpa = limparLinha(linha);
            String titularEncontrado = identificarTitular(linhaLimpa);
            if (titularEncontrado != null) {
                titularAtual = titularEncontrado;
                descricaoPendente = "";
                continue;
            }

            if (linhaLimpa.isBlank() || deveIgnorar(linhaLimpa)) {
                descricaoPendente = "";
                continue;
            }

            if (PARCELA_ISOLADA.matcher(linhaLimpa).matches()) {
                associarParcelaAoUltimoLancamento(itens, linhaLimpa, titularAtual);
                continue;
            }

            Matcher matcher = LINHA_LANCAMENTO.matcher(linhaLimpa);
            if (!matcher.matches()) {
                Matcher dataPendente = DATA_DESCRICAO_PENDENTE.matcher(descricaoPendente);
                Matcher continuacao = LINHA_CONTINUACAO_VALOR.matcher(linhaLimpa);

                if (dataPendente.matches() && continuacao.matches()) {
                    LocalDate data = parseData(dataPendente.group("data"), referencia);
                    String descricao = limparDescricao(
                            dataPendente.group("descricao") + " " + continuacao.group("descricao"));
                    String parcela = extrairParcela(descricao);
                    descricao = removerParcela(descricao);
                    BigDecimal valor = parseValor(continuacao.group("valor"));
                    descricaoPendente = "";

                    adicionarLancamento(itens, data, descricao, titularAtual, parcela, valor);
                    continue;
                }

                if (pareceDescricaoPendente(linhaLimpa)) {
                    descricaoPendente = linhaLimpa;
                }
                continue;
            }

            Matcher dataPendente = DATA_DESCRICAO_PENDENTE.matcher(descricaoPendente);
            LocalDate data = dataPendente.matches()
                    ? parseData(dataPendente.group("data"), referencia)
                    : parseData(matcher.group("data"), referencia);
            String descricaoLinha = limparDescricao(matcher.group("descricao"));
            String descricao = dataPendente.matches()
                    ? limparDescricao(dataPendente.group("descricao") + " " + descricaoLinha)
                    : descricaoPendente.isBlank()
                        ? descricaoLinha
                        : limparDescricao(descricaoPendente + " " + descricaoLinha);
            String parcela = dataPendente.matches() && PARCELA_ISOLADA.matcher(matcher.group("data")).matches()
                    ? matcher.group("data")
                    : extrairParcela(descricao);
            descricao = removerParcela(descricao);
            BigDecimal valor = parseValor(matcher.group("valor"));
            descricaoPendente = "";

            adicionarLancamento(itens, data, descricao, titularAtual, parcela, valor);
        }

        return itens.stream()
                .sorted(Comparator.comparing(FaturaLancamentoDTO::data)
                        .thenComparing(FaturaLancamentoDTO::descricao))
                .toList();
    }

    private void adicionarLancamento(
            List<FaturaLancamentoDTO> itens,
            LocalDate data,
            String descricao,
            String titular,
            String parcela,
            BigDecimal valor) {
        if (data != null && !descricao.isBlank() && !deveIgnorar(descricao)
                && valor.compareTo(BigDecimal.ZERO) != 0) {
            itens.add(new FaturaLancamentoDTO(
                    data, descricao, categorizar(descricao), titular, parcela, moeda(valor)));
        }
    }

    private List<FaturaTitularResumoDTO> agruparPorTitular(List<FaturaLancamentoDTO> lancamentos) {
        Map<String, List<FaturaLancamentoDTO>> porTitular = lancamentos.stream()
                .collect(Collectors.groupingBy(
                        FaturaLancamentoDTO::titular,
                        LinkedHashMap::new,
                        Collectors.toList()));

        return porTitular.entrySet().stream()
                .map(entry -> {
                    List<FaturaCategoriaResumoDTO> categorias = agruparCategorias(entry.getValue());
                    BigDecimal total = entry.getValue().stream()
                            .map(FaturaLancamentoDTO::valor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new FaturaTitularResumoDTO(
                            entry.getKey(), moeda(total), entry.getValue().size(), categorias);
                })
                .sorted(Comparator.comparing(FaturaTitularResumoDTO::titular))
                .toList();
    }

    private List<FaturaCategoriaResumoDTO> agruparCategorias(List<FaturaLancamentoDTO> lancamentos) {
        Map<String, List<FaturaLancamentoDTO>> porCategoria = lancamentos.stream()
                .collect(Collectors.groupingBy(
                        FaturaLancamentoDTO::categoria,
                        LinkedHashMap::new,
                        Collectors.toList()));

        return porCategoria.entrySet().stream()
                .map(categoria -> new FaturaCategoriaResumoDTO(
                        categoria.getKey(),
                        moeda(categoria.getValue().stream()
                                .map(FaturaLancamentoDTO::valor)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)),
                        categoria.getValue().size()))
                .sorted(Comparator.comparing(FaturaCategoriaResumoDTO::total).reversed())
                .toList();
    }

    private List<FaturaMesResumoDTO> agruparPorMes(List<FaturaLancamentoDTO> lancamentos) {
        Map<YearMonth, List<FaturaLancamentoDTO>> porMes = lancamentos.stream()
                .collect(Collectors.groupingBy(
                        item -> YearMonth.from(item.data()),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return porMes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<FaturaCategoriaResumoDTO> categorias = agruparCategorias(entry.getValue());

                    BigDecimal totalMes = categorias.stream()
                            .map(FaturaCategoriaResumoDTO::total)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new FaturaMesResumoDTO(entry.getKey().toString(), moeda(totalMes), categorias);
                })
                .toList();
    }

    private LocalDate parseData(String valor, YearMonth referencia) {
        String data = ESPACOS.matcher(valor.trim()).replaceAll(" ");
        try {
            String dataNormalizada = normalizar(data).toUpperCase(Locale.ROOT);
            if (dataNormalizada.matches("\\d{2}\\s+[A-Z]{3}")) {
                String[] partes = dataNormalizada.split(" ");
                Integer mes = MESES.get(partes[1]);
                if (mes == null) {
                    return null;
                }
                return criarData(Integer.parseInt(partes[0]), mes, referencia);
            }

            data = data.replace('-', '/');
            if (data.matches("\\d{4}/\\d{2}/\\d{2}")) {
                return LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            }
            if (data.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            if (data.matches("\\d{2}/\\d{2}/\\d{2}")) {
                return LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yy"));
            }

            String[] partes = data.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            return criarData(dia, mes, referencia);
        } catch (DateTimeException | NumberFormatException e) {
            return null;
        }
    }

    private LocalDate criarData(int dia, int mes, YearMonth referencia) {
        int ano = referencia.getYear();
        if (mes > referencia.getMonthValue() && mes - referencia.getMonthValue() >= 6) {
            ano--;
        }
        return LocalDate.of(ano, mes, dia);
    }

    private BigDecimal parseValor(String valor) {
        String limpo = valor.replaceAll("(?i)R\\$", "").replace(" ", "");
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(',', '.');
        }
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String categorizar(String descricao) {
        String texto = normalizar(descricao);
        return REGRAS_CATEGORIA.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(texto::contains))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Outros");
    }

    private String identificarTitular(String linha) {
        String normalizada = normalizar(linha);
        if (normalizada.matches("^glener rocha(?:\\s+\\d{4})?$") || normalizada.startsWith("glener rocha ")) {
            return "Glener";
        }
        if (normalizada.matches("^izabela r p lopes(?:\\s+\\d{4})?$") || normalizada.startsWith("izabela r p lopes ")) {
            return "Izabela";
        }
        return null;
    }

    private String extrairParcela(String descricao) {
        Matcher matcher = PARCELA.matcher(descricao);
        return matcher.find() ? matcher.group("parcela") : null;
    }

    private String removerParcela(String descricao) {
        return limparDescricao(PARCELA.matcher(descricao).replaceAll(" "));
    }

    private void associarParcelaAoUltimoLancamento(
            List<FaturaLancamentoDTO> itens,
            String parcela,
            String titularAtual) {
        if (itens.isEmpty()) {
            return;
        }

        int ultimoIndice = itens.size() - 1;
        FaturaLancamentoDTO ultimo = itens.get(ultimoIndice);
        if (ultimo.parcela() == null && ultimo.titular().equals(titularAtual)) {
            itens.set(ultimoIndice, new FaturaLancamentoDTO(
                    ultimo.data(), ultimo.descricao(), ultimo.categoria(), ultimo.titular(),
                    parcela, ultimo.valor()));
        }
    }

    private static Map<String, List<String>> criarRegrasCategoria() {
        Map<String, List<String>> regras = new LinkedHashMap<>();
        regras.put("Combustível", List.of("posto", "shell", "ipiranga", "petrobras", "abastec", "combustivel"));
        regras.put("Alimentação", List.of("ifood", "ifd*", "restaurante", "lanchonete", "padaria", "paes", "mercado", "supermercado", "assai", "cencosud", "frutas e verduras", "acougue", "hortifruti", "mcdonalds", "mc donalds", "coco bambu", "cozinha caseira", "bolosdos", "food"));
        regras.put("Transporte", List.of("uber", "99app", "taxi", "estacionamento", "pedagio", "metro", "onibus"));
        regras.put("Saúde", List.of("farmacia", "drogaria", "droga ", "bryfarma", "rd saude", "hospital", "clinica", "laboratorio", "medic"));
        regras.put("Assinaturas", List.of("netflix", "spotify", "prime video", "disney", "hbo", "youtube", "deezer", "google one", "chatgpt", "icloud"));
        regras.put("Compras", List.of("amazon", "mercado livre", "shopee", "magalu", "magazine luiza", "loja", "shopping"));
        regras.put("Lazer", List.of("cinema", "cinemark", "teatro", "ingresso", "parque", "bar ", "event"));
        regras.put("Educação", List.of("escola", "faculdade", "curso", "livraria", "udemy"));
        regras.put("Moradia", List.of("condominio", "energia", "sabesp", "agua", "aluguel", "internet", "leroy", "tintas", "homeesperanca", "claro", "vivo", "tim "));
        return regras;
    }

    private boolean pareceDescricaoPendente(String linha) {
        String normalizada = normalizar(linha);
        if (linha.length() < 4 || linha.contains("R$") || normalizada.matches(".*\\d{2}/\\d{2}.*")) {
            return false;
        }

        return List.of("data ", "descricao", "cidade", "valor em", "lancamentos", "sicoob",
                        "pagina ", "glener ", "izabela ", "total ", "ref ", "bancario",
                        "protecao perda", "roubo")
                .stream()
                .noneMatch(normalizada::contains);
    }

    private boolean deveIgnorar(String linha) {
        String normalizada = normalizar(linha);
        return LINHAS_IGNORADAS.stream().anyMatch(normalizada::contains);
    }

    private String limparLinha(String linha) {
        return ESPACOS.matcher(linha.replace('\u00a0', ' ').trim()).replaceAll(" ");
    }

    private String limparDescricao(String descricao) {
        String limpa = descricao.replaceAll("^[;,:\\s\\\"]+|[;,:\\s\\\"]+$", "");
        return ESPACOS.matcher(limpa).replaceAll(" ");
    }

    private String extensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private BigDecimal moeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
