package com.github.javarar.poke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class Controller {
    public static String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new SecureRandom();

    @GetMapping("/getAll") // возвращает всех запрошенных покемонов синхронно
    public String getAll(@RequestParam List<String> names) throws IOException {
        List<Pokemon> response = new ArrayList<>();
        for (String name : names) {
            response.add(getPokemonInfo(name));
        }

        return mapper.writeValueAsString(response);
    }

    @GetMapping("/oneOf") // возвращает одного из запрошенных покемонов синхронно
    public String oneOf(@RequestParam List<String> names) throws IOException {
        String randomName = names.get(random.nextInt(names.size()));
        return mapper.writeValueAsString(getPokemonInfo(randomName));
    }

    //http://localhost:8080/getAllAsync?names=pikachu,bulbasaur,lapras,scyther
    @GetMapping("/getAllAsync") // возвращает всех запрошенных покем, у сервиса параллельно; метод исполняется синхронно;
    public String getAllParallel(@RequestParam List<String> names) throws ExecutionException, InterruptedException, JsonProcessingException {
        return mapper.writeValueAsString(getAsync(names, true));
    }

    @GetMapping("/getFirstAsync") // возвращает первого вернувшегося из запрошенных покемонов, запрашивая их у сервиса параллельно; метод исполняется синхронно;
    public String anyOf(@RequestParam List<String> names) throws IOException, ExecutionException, InterruptedException {
        return mapper.writeValueAsString(getAsync(names, false));
    }


    private List<Pokemon> getAsync(List<String> names, boolean all) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Pokemon>> futures = new ArrayList<>();
        for (String name : names) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return getPokemonInfo(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        if (all) {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allFutures.thenApply(f -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())).get();
        }
        CompletableFuture<Object> future = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]));
        return List.of((Pokemon) future.get());
    }

    private Pokemon getPokemonInfo(String name) throws IOException {
        String response = restTemplate.getForEntity(POKEMON_URL + name, String.class).getBody();
        Map<String, Object> responseMap = mapper.readValue(response, Map.class);
        return getPokemon(responseMap);
    }

    private Pokemon getPokemon(Map<String, Object> map) {
        List<String> abilitiesList = new ArrayList<>();
        List<Map<String, Object>> abilities = (List<Map<String, Object>>) map.get("abilities");

        for (Map<String, Object> a : abilities) {
            Map<String, Object> ability = (Map<String, Object>) a.get("ability");
            abilitiesList.add(ability.get("name").toString());

        }

        return new Pokemon(
                map.get("name").toString(),
                Double.valueOf(map.get("height").toString()),
                Double.valueOf(map.get("weight").toString()),
                abilitiesList
        );
    }
}