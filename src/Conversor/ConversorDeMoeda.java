package Conversor;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class ConversorDeMoeda {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner entrada = new Scanner(System.in);
        HttpClient cliente = HttpClient.newHttpClient();
        String chaveApi = lerChaveAPI();

        if (chaveApi == null) {
            System.out.println("Chave da API não encontrada. Verifique se o arquivo config.properties está presente.");
            return;
        }

        while (true) {
            System.out.println("Bem-vindo ao Conversor de Moedas!");
            System.out.println("\n********** CONVERSOR DE MOEDAS **********");
            System.out.println("1) Dólar (USD) → Peso (ARS)");
            System.out.println("2) Peso (ARS) → Dólar (USD)");
            System.out.println("3) Dólar (USD) → Real (BRL)");
            System.out.println("4) Real (BRL) → Dólar (USD)");
            System.out.println("5) Dólar (USD) → Euro (EUR)");
            System.out.println("6) Euro (EUR) → Dólar (USD)");
            System.out.println("7) Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = entrada.nextInt();

            if (opcao == 7) {
                System.out.println("Conversor Encerrado.");
                break;
            }

            System.out.print("Digite o valor que deseja converter: ");
            double valor = entrada.nextDouble();

            String moedaOrigem = "";
            String moedaDestino = "";

            switch (opcao) {
                case 1 -> { moedaOrigem = "USD"; moedaDestino = "ARS"; }
                case 2 -> { moedaOrigem = "ARS"; moedaDestino = "USD"; }
                case 3 -> { moedaOrigem = "USD"; moedaDestino = "BRL"; }
                case 4 -> { moedaOrigem = "BRL"; moedaDestino = "USD"; }
                case 5 -> { moedaOrigem = "EUR"; moedaDestino = "USD"; }
                case 6 -> { moedaOrigem = "USD"; moedaDestino = "EUR"; }
                default -> {
                    System.out.println("Opção inválida. Tente novamente.");
                    continue;
                }
            }

            String endereco = "https://v6.exchangerate-api.com/v6/" + chaveApi + "/pair/" + moedaOrigem + "/" + moedaDestino + "/" + valor;

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(endereco))
                    .build();

            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            Map<String, Object> resultado = gson.fromJson(resposta.body(), Map.class);

            if (resultado.containsKey("conversion_result")) {
                double convertido = (double) resultado.get("conversion_result");
                System.out.printf("Valor convertido: %.2f %s\n", convertido, moedaDestino);
            } else {
                System.out.println("Erro na conversão. Verifique a chave da API ou tente mais tarde.");
            }
        }
    }

    public static String lerChaveAPI() {
        Properties propriedades = new Properties();
        try (FileInputStream leitor = new FileInputStream("config.properties")) {
            propriedades.load(leitor);
            return propriedades.getProperty("api.key");
        } catch (IOException e) {
            return null;
        }
    }
}
