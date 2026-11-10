package com.equipe.Supermercado;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.equipe.Supermercado.model.Produto;
import com.equipe.Supermercado.service.EstoqueService;
import com.equipe.Supermercado.service.ProdutoService;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Front-end desktop (JavaFX): Produtos, Cadastrar e Frente de caixa. Usa os services já existentes. */
public class TelaPrincipal extends Application {

    private static final int BAIXO = 5; // até esta quantidade o estoque é "baixo"

    private ConfigurableApplicationContext contexto;
    private ProdutoService produtos;
    private EstoqueService estoque;
    private final ObservableList<Produto> lista = FXCollections.observableArrayList();
    private final NumberFormat brl = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final Label titulo = lbl("", "titulo");
    private final StackPane paginas = new StackPane();
    private final ToggleGroup menu = new ToggleGroup();
    private final Label iValor = lbl("–", "ind-num"), iTotal = lbl("–", "ind-num"),
            iBaixo = lbl("–", "ind-num"), iZero = lbl("–", "ind-num");
    private final TextField vCodigo = campo("Código de barras"), vQtd = campo("Quantidade");

    @Override
    public void init() {
        contexto = new SpringApplicationBuilder(SupermercadoApplication.class)
                .web(WebApplicationType.NONE).headless(false).run();
        produtos = contexto.getBean(ProdutoService.class);
        estoque = contexto.getBean(EstoqueService.class);
    }

    @Override
    public void stop() {
        contexto.close();
    }

    @Override
    public void start(Stage palco) {
        paginas.getChildren().addAll(paginaProdutos(), paginaCadastro(), paginaCaixa());
        String[] nomes = { "Produtos", "Cadastrar", "Frente de caixa" };
        VBox lateral = new VBox(6, lbl("Supermercado", "marca"));
        lateral.getStyleClass().add("lateral");
        for (int i = 0; i < nomes.length; i++) {
            int n = i;
            ToggleButton b = new ToggleButton(nomes[i]);
            b.getStyleClass().add("menu-item");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setToggleGroup(menu);
            b.setOnAction(e -> mostrar(n));
            lateral.getChildren().add(b);
        }
        BorderPane raiz = new BorderPane(paginas);
        raiz.setLeft(lateral);
        raiz.setTop(titulo);
        raiz.getStyleClass().add("app");
        paginas.setPadding(new Insets(10, 28, 28, 28));
        BorderPane.setMargin(titulo, new Insets(26, 28, 6, 28));

        Scene cena = new Scene(raiz, 1100, 700);
        cena.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
        palco.setTitle("Controle de produtos | Supermercado");
        palco.setMinWidth(900);
        palco.setMinHeight(600);
        palco.setScene(cena);
        palco.show();
        mostrar(0);
    }

    private void mostrar(int n) {
        String[] t = { "Produtos em estoque", "Cadastrar produto", "Frente de caixa" };
        titulo.setText(t[n]);
        for (int i = 0; i < paginas.getChildren().size(); i++) {
            paginas.getChildren().get(i).setVisible(i == n);
            paginas.getChildren().get(i).setManaged(i == n);
        }
        menu.getToggles().get(n).setSelected(true);
        if (n != 1) recarregar();
    }

    private void recarregar() {
        lista.setAll(produtos.listarProdutos());
        iTotal.setText(String.valueOf(lista.size()));
        iZero.setText(String.valueOf(lista.stream().filter(p -> qtd(p) <= 0).count()));
        iBaixo.setText(String.valueOf(lista.stream().filter(p -> qtd(p) > 0 && qtd(p) <= BAIXO).count()));
        iValor.setText(brl.format(lista.stream().map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add)));
        atualizarPrevia();
    }

    private int qtd(Produto p) {
        return p.getQuantidadeEstoque() == null ? 0 : p.getQuantidadeEstoque();
    }

    private BigDecimal valor(Produto p) {
        return p.getPreco().multiply(BigDecimal.valueOf(qtd(p)));
    }

    // ---------- Produtos ----------
    private Node paginaProdutos() {
        FlowPane indicadores = new FlowPane(14, 14,
                cartaoInd("Valor em estoque", iValor, "ind-valor"), cartaoInd("Produtos", iTotal, ""),
                cartaoInd("Estoque baixo", iBaixo, "ind-baixo"), cartaoInd("Zerados", iZero, "ind-zero"));

        TextField busca = campo("Buscar por nome ou código");
        busca.setPrefWidth(300);
        FilteredList<Produto> filtrada = new FilteredList<>(lista);
        busca.textProperty().addListener((o, a, b) -> filtrada.setPredicate(
                p -> (p.getNome() + " " + p.getCodigoBarras()).toLowerCase().contains(b.toLowerCase())));
        SortedList<Produto> ordenada = new SortedList<>(filtrada);
        TableView<Produto> tabela = new TableView<>(ordenada);
        ordenada.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabela.setPlaceholder(lbl("Nenhum produto encontrado.", "suave"));

        tabela.getColumns().add(col("Produto", p -> {
            VBox v = new VBox(1, lbl(p.getNome(), "nome"), lbl(p.getCodigoBarras(), "suave"));
            return v;
        }, Comparator.comparing(p -> p.getNome().toLowerCase())));
        tabela.getColumns().add(col("Preço", p -> lbl(brl.format(p.getPreco()), "preco"),
                Comparator.comparing(Produto::getPreco)));
        tabela.getColumns().add(col("Estoque", p -> {
            String s = qtd(p) <= 0 ? "zero" : qtd(p) <= BAIXO ? "baixo" : "ok";
            String texto = qtd(p) <= 0 ? "Zerado" : qtd(p) <= BAIXO ? "Baixo" : "Em estoque";
            HBox h = new HBox(8, lbl(String.valueOf(qtd(p)), "nome"), lbl(texto, "selo", s));
            h.setAlignment(Pos.CENTER_LEFT);
            return h;
        }, Comparator.comparingInt(this::qtd)));
        tabela.getColumns().add(col("Valor em estoque", p -> lbl(brl.format(valor(p)), ""),
                Comparator.comparing(this::valor)));
        tabela.getColumns().add(col("", p -> {
            Button b = new Button("Vender");
            b.getStyleClass().addAll("botao", "secundario");
            b.setDisable(qtd(p) <= 0);
            b.setOnAction(e -> {
                vCodigo.setText(p.getCodigoBarras());
                vQtd.setText("1");
                mostrar(2);
            });
            return b;
        }, null));
        VBox.setVgrow(tabela, Priority.ALWAYS);

        VBox painel = new VBox(12, busca, tabela);
        painel.getStyleClass().add("cartao");
        VBox.setVgrow(painel, Priority.ALWAYS);
        VBox v = new VBox(18, indicadores, painel);
        return v;
    }

    private Node cartaoInd(String rotulo, Label numero, String classe) {
        VBox v = new VBox(6, lbl(rotulo, "ind-rot"), numero);
        v.getStyleClass().add("ind");
        if (!classe.isEmpty()) v.getStyleClass().add(classe);
        v.setPrefWidth(classe.equals("ind-valor") ? 280 : 170);
        return v;
    }

    private TableColumn<Produto, Produto> col(String titulo, Function<Produto, Node> celula, Comparator<Produto> ordem) {
        TableColumn<Produto, Produto> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        c.setCellFactory(x -> new TableCell<>() {
            @Override
            protected void updateItem(Produto p, boolean vazio) {
                super.updateItem(p, vazio);
                setGraphic(vazio || p == null ? null : celula.apply(p));
            }
        });
        if (ordem == null) c.setSortable(false); else c.setComparator(ordem);
        return c;
    }

    // ---------- Cadastrar ----------
    private Node paginaCadastro() {
        TextField codigo = campo("Código de barras"), nome = campo("Nome do produto"),
                preco = campo("Preço (ex.: 12,50)"), qtd = campo("Estoque inicial");
        Label msg = lbl("", "mensagem");
        msg.setVisible(false);
        Button salvar = new Button("Cadastrar produto");
        salvar.getStyleClass().add("botao");

        Label eNome = lbl("Nome do produto", "et-nome"), ePreco = lbl("R$ 0,00", "et-preco"),
                eCodigo = lbl("0000000000000", "suave");
        Runnable atualizar = () -> {
            eNome.setText(nome.getText().isBlank() ? "Nome do produto" : nome.getText());
            eCodigo.setText(codigo.getText().isBlank() ? "0000000000000" : codigo.getText());
            try { ePreco.setText(brl.format(new BigDecimal(preco.getText().trim().replace(',', '.')))); }
            catch (Exception e) { ePreco.setText("R$ 0,00"); }
        };
        for (TextField f : new TextField[] { codigo, nome, preco }) f.textProperty().addListener((o, a, b) -> atualizar.run());

        salvar.setOnAction(e -> {
            Produto p = new Produto();
            p.setCodigoBarras(codigo.getText().trim());
            p.setNome(nome.getText().trim());
            try {
                if (!preco.getText().isBlank()) p.setPreco(new BigDecimal(preco.getText().trim().replace(',', '.')));
                if (!qtd.getText().isBlank()) p.setQuantidadeEstoque(Integer.parseInt(qtd.getText().trim()));
                dizer(msg, "Produto cadastrado: " + produtos.cadastrarProduto(p).getNome(), true);
                codigo.clear(); nome.clear(); preco.clear(); qtd.clear();
            } catch (NumberFormatException ex) {
                dizer(msg, "Preço ou estoque inválido!", false);
            } catch (Exception ex) {
                dizer(msg, ex.getMessage(), false);
            }
        });

        VBox form = new VBox(12, lbl("Código de barras", "rotulo"), codigo, lbl("Nome do produto", "rotulo"), nome,
                lbl("Preço (R$)", "rotulo"), preco, lbl("Estoque inicial", "rotulo"), qtd, salvar, msg);
        form.getStyleClass().add("cartao");
        form.setPrefWidth(480);

        VBox etiqueta = new VBox(8, eNome, ePreco, eCodigo);
        etiqueta.getStyleClass().add("etiqueta");
        etiqueta.setRotate(-1.5);
        VBox lado = new VBox(18, lbl("Etiqueta do produto", "nome"), etiqueta);
        lado.getStyleClass().add("cartao");
        lado.setPrefWidth(320);
        HBox h = new HBox(24, form, lado);
        h.setAlignment(Pos.TOP_LEFT);
        return h;
    }

    // ---------- Frente de caixa ----------
    private final Label previa = lbl("", "previa");

    private void atualizarPrevia() {
        Produto p = lista.stream().filter(x -> x.getCodigoBarras().equals(vCodigo.getText().trim())).findFirst().orElse(null);
        previa.setVisible(!vCodigo.getText().isBlank());
        if (p == null) { previa.setText("Produto não encontrado na lista."); return; }
        String t = p.getNome() + "\nPreço: " + brl.format(p.getPreco()) + "   |   Estoque: " + qtd(p);
        try { t += "\nTotal da venda: " + brl.format(p.getPreco().multiply(BigDecimal.valueOf(Integer.parseInt(vQtd.getText().trim())))); }
        catch (NumberFormatException e) { /* sem total */ }
        previa.setText(t);
    }

    private Node paginaCaixa() {
        vQtd.setText("1");
        vCodigo.textProperty().addListener((o, a, b) -> atualizarPrevia());
        vQtd.textProperty().addListener((o, a, b) -> atualizarPrevia());
        previa.setVisible(false);
        Label msg = lbl("", "mensagem");
        msg.setVisible(false);
        Button consultar = new Button("Consultar estoque");
        consultar.getStyleClass().addAll("botao", "secundario");
        consultar.setOnAction(e -> {
            try { dizer(msg, "Estoque atual: " + estoque.obterQuantidadePorCodigo(vCodigo.getText().trim()), true); }
            catch (Exception ex) { dizer(msg, ex.getMessage(), false); }
        });
        Button vender = new Button("Registrar venda");
        vender.getStyleClass().add("botao");
        vender.setOnAction(e -> {
            try {
                Produto p = estoque.processarSaidaFrenteCaixa(vCodigo.getText().trim(), Integer.parseInt(vQtd.getText().trim()));
                dizer(msg, "Venda realizada! Novo estoque de " + p.getNome() + ": " + p.getQuantidadeEstoque(), true);
                vCodigo.clear();
                vQtd.setText("1");
                recarregar();
            } catch (NumberFormatException ex) {
                dizer(msg, "Quantidade inválida!", false);
            } catch (Exception ex) {
                dizer(msg, ex.getMessage(), false);
            }
        });
        VBox form = new VBox(12, lbl("Código de barras", "rotulo"), vCodigo, lbl("Quantidade", "rotulo"), vQtd,
                previa, new HBox(10, consultar, vender), msg);
        form.getStyleClass().add("cartao");
        form.setMaxWidth(520);
        HBox h = new HBox(form);
        h.setAlignment(Pos.TOP_LEFT);
        return h;
    }

    // ---------- utilitários ----------
    private static Label lbl(String texto, String... classes) {
        Label l = new Label(texto);
        for (String c : classes) if (!c.isEmpty()) l.getStyleClass().add(c);
        l.setWrapText(true);
        return l;
    }

    private static TextField campo(String dica) {
        TextField t = new TextField();
        t.setPromptText(dica);
        return t;
    }

    private void dizer(Label l, String texto, boolean ok) {
        l.setText(texto);
        l.getStyleClass().removeAll("sucesso", "erro");
        l.getStyleClass().add(ok ? "sucesso" : "erro");
        l.setVisible(true);
    }
}
