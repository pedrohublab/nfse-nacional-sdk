package com.hubpedro.nfsenacional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner autônomo para execução da suíte completa de testes do SDK.
 */
public final class TestRunner {

    public static void main(String[] args) {
        List<Class<?>> testClasses = List.of(
                com.hubpedro.nfsenacional.domain.CNPJTest.class,
                com.hubpedro.nfsenacional.domain.CPFTest.class,
                com.hubpedro.nfsenacional.domain.ChaveDPSTest.class,
                com.hubpedro.nfsenacional.domain.DPSTest.class,
                com.hubpedro.nfsenacional.certificate.CertificateServiceTest.class,
                com.hubpedro.nfsenacional.certificate.AssinadorArquivoXmlTest.class,
                com.hubpedro.nfsenacional.xml.DpsXmlGeneratorTest.class,
                com.hubpedro.nfsenacional.xml.XmlSchemaValidatorTest.class,
                com.hubpedro.nfsenacional.xml.PayloadEncoderTest.class,
                com.hubpedro.nfsenacional.gateway.SefinNacionalGatewayTest.class,
                com.hubpedro.nfsenacional.NFSeNacionalClientTest.class,
                com.hubpedro.nfsenacional.homologacao.HomologacaoSmokeTest.class,
                com.hubpedro.nfsenacional.storage.XmlStorageHelperTest.class
        );

        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        List<String> failures = new ArrayList<>();

        System.out.println("=================================================");
        System.out.println("   EXECUTANDO SUÍTE DE TESTES NFS-E NACIONAL SDK  ");
        System.out.println("=================================================");

        for (Class<?> testClass : testClasses) {
            String className = testClass.getSimpleName();
            DisplayName classDisplayName = testClass.getAnnotation(DisplayName.class);
            String classDesc = classDisplayName != null ? classDisplayName.value() : className;
            System.out.println("\n[SUÍTE] " + classDesc + " (" + className + ")");

            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    totalTests++;
                    DisplayName testDisplayName = method.getAnnotation(DisplayName.class);
                    String testDesc = testDisplayName != null ? testDisplayName.value() : method.getName();

                    try {
                        Object instance = testClass.getDeclaredConstructor().newInstance();

                        // Executar @BeforeEach se houver
                        for (Method m : testClass.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(org.junit.jupiter.api.BeforeEach.class)) {
                                m.setAccessible(true);
                                m.invoke(instance);
                            }
                        }

                        // Executar o método de teste
                        method.setAccessible(true);
                        method.invoke(instance);

                        // Executar @AfterEach se houver
                        for (Method m : testClass.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(org.junit.jupiter.api.AfterEach.class)) {
                                m.setAccessible(true);
                                m.invoke(instance);
                            }
                        }

                        passedTests++;
                        System.out.println("  ✓ " + testDesc);
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof org.opentest4j.TestAbortedException) {
                            System.out.println("  ↷ " + testDesc + " (IGNORADO / SKIPPED: " + cause.getMessage() + ")");
                            // Não incrementa falhas pois é teste condicional ignorado
                        } else {
                            failedTests++;
                            failures.add(testClass.getSimpleName() + "." + method.getName() + ": " + (cause != null ? cause.getMessage() : e.getMessage()));
                            System.out.println("  ✗ " + testDesc + " -> " + (cause != null ? cause.getMessage() : e.getMessage()));
                            if (cause != null) {
                                cause.printStackTrace(System.out);
                            }
                        }
                    } catch (Exception e) {
                        failedTests++;
                        failures.add(testClass.getSimpleName() + "." + method.getName() + ": " + e.getMessage());
                        System.out.println("  ✗ " + testDesc + " -> " + e.getMessage());
                        e.printStackTrace(System.out);
                    }
                }
            }
        }

        System.out.println("\n=================================================");
        System.out.println("RESUMO DE EXECUÇÃO DOS TESTES:");
        System.out.println("Total executados: " + totalTests);
        System.out.println("Aprovados (PASSED): " + passedTests);
        System.out.println("Falhas (FAILED): " + failedTests);
        System.out.println("=================================================");

        if (failedTests > 0) {
            System.err.println("\nLista de Falhas:");
            for (String f : failures) {
                System.err.println(" - " + f);
            }
            System.exit(1);
        } else {
            System.out.println("\nTODOS OS TESTES PASSARAM COM 100% DE SUCESSO!");
        }
    }
}
