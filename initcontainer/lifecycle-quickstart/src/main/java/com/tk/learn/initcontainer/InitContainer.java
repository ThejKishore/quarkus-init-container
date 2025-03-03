package com.tk.learn.initcontainer;

import com.azure.security.keyvault.secrets.SecretClient;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@QuarkusMain
public class InitContainer implements QuarkusApplication {

    @Inject
    SecretClient secretClient;

    @Override
    public int run(String... args) throws Exception {
        // Fetch secrets from Azure Key Vault
        String secret1 = secretClient.getSecret("mysecretname").getValue();
        String secret2 = secretClient.getSecret("mysecretname2").getValue();

        // Prepare the secrets to write to YAML
        Map<String, String> secretMap = Map.of(
                "secret1", secret1,
                "secret2", secret2
        );

        // Write secrets to a YAML file
        writeSecretsToYamlFile(secretMap);

        // Optionally, exit the app
        Quarkus.asyncExit();
        return 0;
    }

    private void writeSecretsToYamlFile(Map<String, String> secrets) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2); // Indentation for YAML file
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Use block style for better readability

        Yaml yaml = new Yaml(options);

        String yamlFilePath = System.getenv("SECRET_YAML_PATH");
        if (yamlFilePath == null) {
            // Fallback to default if the environment variable is not set yamlFilePath = "/tmp/secrets.yaml";
            yamlFilePath = "/Users/thejkaruneegar/quarkusInitContainer/initcontainer/lifecycle-quickstart/build/tmp/secrets.yaml";
            // Modify as per your need (e.g., location in Docker container)
        }
        // Specify the file path where you want to store the YAML file

        try (FileWriter writer = new FileWriter(yamlFilePath)) {
            yaml.dump(secrets, writer);
            System.out.println("Secrets have been written to " + yamlFilePath);
        } catch (IOException e) {
            System.err.println("Error writing secrets to YAML file: " + e.getMessage());
        }
    }
}
